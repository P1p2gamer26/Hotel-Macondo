package com.hotel.macondo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.macondo.DTOs.Salida.CheckoutPagoResponse;
import com.hotel.macondo.DTOs.Salida.ConfirmacionPagoResponse;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.IntentoPago;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.IntentoPagoRepository;
import com.hotel.macondo.repository.ReservaRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MercadoPagoService {
  private static final String API_URL = "https://api.mercadopago.com";
  private static final Pattern CAMPOS_ORDEN = Pattern.compile(
      "\\b(success_url|failure_url|pending_url|auto_return|payer\\.email|email|"
          + "travel\\.passengers|identification_type|identification_number|identification|"
          + "item_references|total_amount|unit_price|category_id|capture_mode|processing_mode|external_code|external_reference)\\b");
  private static final Pattern CREDENCIALES_Y_CORREOS = Pattern.compile(
      "(?i)\\b(?:bearer\\s+[^\\s,;]+|(?:app_usr-|test-)[a-z0-9_-]+)|[\\w.+%-]+@[\\w.-]+\\.[a-z]{2,}");

  private final ReservaRepository reservaRepository;
  private final IntentoPagoRepository intentos;
  private final CuentaService cuentaService;
  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  @Value("${MP_ACCESS_TOKEN:${mercadopago.access-token:}}") private String accessToken;
  @Value("${MP_WEBHOOK_SECRET:${mercadopago.webhook-secret:}}") private String webhookSecret;
  @Value("${mercadopago.environment:test}") private String environment;
  @Value("${app.frontend-base-url:http://localhost:4200}") private String frontendBaseUrl;

  public MercadoPagoService(ReservaRepository reservaRepository, IntentoPagoRepository intentos,
      CuentaService cuentaService, ObjectMapper objectMapper) {
    this.reservaRepository = reservaRepository;
    this.intentos = intentos;
    this.cuentaService = cuentaService;
    this.objectMapper = objectMapper;
    this.restClient = RestClient.builder().baseUrl(API_URL).build();
  }

  @Transactional
  public CheckoutPagoResponse iniciar(Long clienteId, Long reservaId, BigDecimal monto) {
    if (accessToken.isBlank()) {
      throw new PeticionImposible("Mercado Pago no está configurado. Agrega el Access Token de Sandbox al backend.");
    }

    Reserva reserva = reservaRepository.findById(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva."));
    if (reserva.getCliente() == null || !reserva.getCliente().getId().equals(clienteId)) {
      throw new RecursoNoEncontradoException("No se encontró la reserva.");
    }
    if (!"ACTIVA".equalsIgnoreCase(reserva.getEstado())) {
      throw new PeticionImposible("Solo se pueden pagar cuentas de estadías en curso.");
    }

    Cuenta cuenta = reserva.getCuenta();
    if (cuenta == null || monto == null || monto.stripTrailingZeros().scale() > 2 || monto.signum() <= 0
        || monto.compareTo(cuentaService.calcularSaldo(cuenta)) > 0) {
      throw new FormularioErroneoException("El monto debe ser válido y no superar el saldo pendiente.");
    }

    int decimales = monto.stripTrailingZeros().scale() <= 0 ? 0 : 2;
    String amount = monto.setScale(decimales).toPlainString();
    String referencia = "MCD-" + UUID.randomUUID().toString().replace("-", "");
    String codigoItem = "RESERVA-" + reservaId;
    String returnUrl = frontendBaseUrl.replaceAll("/+$", "") + "/cliente/" + clienteId
        + "/reservas/" + reservaId + "?pago=mercadopago";
    Map<String, Object> item = Map.of(
        "external_code", codigoItem,
        "title", "Estadía y servicios - Reserva " + reservaId,
        "category_id", "travels",
        "type", "travel",
        "quantity", 1,
        "unit_price", amount);
    Map<String, Object> payer = Map.of(
        "email", reserva.getCliente().getCorreo(),
        "first_name", reserva.getCliente().getNombre(),
        "last_name", reserva.getCliente().getApellido(),
        "identification", Map.of("type", "CC", "number", reserva.getCliente().getCedula()));
    Map<String, Object> additionalInfo = Map.of(
        "payer.authentication_type", "WEB",
        "travel.passengers", List.of(Map.of(
            "first_name", reserva.getCliente().getNombre(),
            "last_name", reserva.getCliente().getApellido(),
            "identification_type", "CC",
            "identification_number", reserva.getCliente().getCedula(),
            "item_references", List.of(codigoItem))));
    Map<String, Object> order = Map.of(
        "type", "online",
        "processing_mode", "manual",
        "capture_mode", "automatic",
        "total_amount", amount,
        "external_reference", referencia,
        "description", "Pago de estadía y servicios del Hotel Macondo",
        "payer", payer,
        "items", List.of(item),
        "additional_info", additionalInfo,
        "config", Map.of("online", Map.of(
            "success_url", returnUrl,
            "failure_url", returnUrl + "&resultado=fallido",
            "pending_url", returnUrl + "&resultado=pendiente",
            "auto_return", "approved")));

    try {
      JsonNode response = restClient.post()
          .uri("/v1/orders")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
          .header("X-Idempotency-Key", UUID.randomUUID().toString())
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .body(order)
          .retrieve()
          .body(JsonNode.class);
      if (response == null || response.path("id").asText().isBlank()
          || response.path("checkout_url").asText().isBlank()) {
        throw new PeticionImposible("Mercado Pago no devolvió la URL del checkout.");
      }

      IntentoPago intento = new IntentoPago(referencia, monto, cuenta);
      intento.setTransaccionId(response.path("id").asText());
      intentos.save(intento);
      return new CheckoutPagoResponse(referencia, response.path("checkout_url").asText(), monto,
          response.path("status").asText("PENDING"));
    } catch (RestClientResponseException exception) {
      throw new PeticionImposible("Mercado Pago rechazó la solicitud de checkout (HTTP "
          + exception.getStatusCode().value() + ")." + detalleRechazoCheckout(exception, amount, reserva.getCliente()));
    }
  }

  private String detalleRechazoCheckout(RestClientResponseException exception, String montoEnviado, Cliente cliente) {
    HttpHeaders headers = exception.getResponseHeaders();
    String requestId = headers == null ? null : headers.getFirst("x-request-id");
    String referencia = requestId != null && requestId.matches("[a-zA-Z0-9_-]{1,80}")
        ? " ID de solicitud: " + requestId + "." : "";
    String respuesta = exception.getResponseBodyAsString();
    try {
      JsonNode body = objectMapper.readTree(respuesta);
      if (body == null || !body.isObject()) {
        return referencia;
      }
      JsonNode error = body.path("errors").path(0);
      if (error.isMissingNode()) {
        error = body.path("cause").path(0);
      }
      String codigo = error.path("code").asText(body.path("code").asText(body.path("error").asText("")));
      // En producción mostramos el código y los campos. En Sandbox agregamos el detalle filtrado.
      String campos = CAMPOS_ORDEN.matcher(respuesta).results()
          .map(MatchResult::group)
          .distinct()
          .limit(3)
          .collect(Collectors.joining(", "));
      String monto = campos.contains("unit_price") || campos.contains("total_amount")
          ? " Monto enviado: " + montoEnviado + " COP." : "";
      String detalle = "test".equalsIgnoreCase(environment) ? detalleSandbox(body, cliente) : "";
      return (codigo.matches("[a-z][a-z0-9_]{1,63}") ? " Código de Mercado Pago: " + codigo + "." : "")
          + (campos.isEmpty() ? "" : " Campos mencionados: " + campos + ".")
          + monto + detalle + referencia;
    } catch (JsonProcessingException ignored) {
      return referencia;
    }
  }

  private String detalleSandbox(JsonNode body, Cliente cliente) {
    List<String> datosPrivados = List.of(accessToken.strip(), webhookSecret.strip(), cliente.getCorreo(),
        cliente.getCedula(), cliente.getNombre(), cliente.getApellido());
    String detalle = Stream.of("details", "detail", "message", "description", "reason", "path", "field")
        .flatMap(campo -> body.findValues(campo).stream())
        .flatMap(valor -> valor.isContainerNode() ? StreamSupport.stream(valor.spliterator(), false) : Stream.of(valor))
        .filter(JsonNode::isTextual)
        .map(JsonNode::asText)
        .map(texto -> ocultarDatosPrivados(texto, datosPrivados))
        .filter(texto -> !texto.isBlank())
        .distinct()
        .limit(4)
        .collect(Collectors.joining(" / "));
    return detalle.isBlank() ? "" : " Detalle de Sandbox: " + detalle;
  }

  private String ocultarDatosPrivados(String texto, List<String> datosPrivados) {
    for (String dato : datosPrivados) {
      if (!dato.isBlank()) {
        texto = texto.replace(dato, "[oculto]");
      }
    }
    texto = CREDENCIALES_Y_CORREOS.matcher(texto).replaceAll("[oculto]").replaceAll("\\s+", " ").strip();
    return texto.length() <= 240 ? texto : texto.substring(0, 240) + "…";
  }

  @Transactional
  public void procesarNotificacion(JsonNode notification, String signatureHeader, String requestId, String dataId) {
    if (webhookSecret.isBlank()) {
      throw new PeticionImposible("Falta configurar la firma secreta del webhook de Mercado Pago.");
    }
    if (!"order".equals(notification.path("type").asText())) return;
    validarFirma(signatureHeader, requestId, dataId);

    String orderId = notification.path("data").path("id").asText(dataId);
    if (orderId.isBlank() || !orderId.equalsIgnoreCase(dataId)) {
      throw new PeticionImposible("La notificación de Mercado Pago no tiene un identificador de orden válido.");
    }
    actualizarOrden(consultarOrden(orderId), orderId);
  }

  @Transactional
  public ConfirmacionPagoResponse confirmarRetorno(Long clienteId, Long reservaId, String orderId) {
    if (accessToken.isBlank()) {
      throw new PeticionImposible("Mercado Pago no está configurado. Agrega el Access Token de Sandbox al backend.");
    }
    if (orderId == null || orderId.isBlank()) {
      throw new FormularioErroneoException("Falta el identificador de la orden de Mercado Pago.");
    }
    Reserva reserva = reservaRepository.findById(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva."));
    if (reserva.getCliente() == null || !reserva.getCliente().getId().equals(clienteId)) {
      throw new RecursoNoEncontradoException("No se encontró la reserva.");
    }
    IntentoPago intento = intentos.findByTransaccionIdForUpdate(orderId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe el intento de pago."));
    if (intento.getCuenta().getReserva() == null || !intento.getCuenta().getReserva().getId().equals(reservaId)) {
      throw new RecursoNoEncontradoException("No se encontró el intento de pago para esta reserva.");
    }

    String estado = actualizarOrden(consultarOrden(orderId), orderId);
    String mensaje = switch (estado) {
      case "APPROVED" -> "Mercado Pago confirmó el pago.";
      case "DECLINED" -> "Mercado Pago no aprobó el pago. Puedes intentar de nuevo.";
      default -> "El pago sigue pendiente de confirmación de Mercado Pago.";
    };
    return new ConfirmacionPagoResponse(estado, mensaje);
  }

  private JsonNode consultarOrden(String orderId) {
    JsonNode order;
    try {
      order = restClient.get()
          .uri("/v1/orders/{id}", orderId)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
          .retrieve()
          .body(JsonNode.class);
    } catch (RestClientResponseException exception) {
      throw new PeticionImposible("No se pudo confirmar la orden con Mercado Pago (HTTP "
          + exception.getStatusCode().value() + ").");
    }
    if (order == null) throw new PeticionImposible("Mercado Pago devolvió una orden vacía.");
    return order;
  }

  private String actualizarOrden(JsonNode order, String orderId) {
    String expectedLiveMode = environment.equalsIgnoreCase("prod") ? "true" : "false";
    if (!expectedLiveMode.equalsIgnoreCase(order.path("live_mode").asText(expectedLiveMode))) {
      throw new PeticionImposible("La orden pertenece a otro ambiente de Mercado Pago.");
    }
    String currency = order.path("currency").asText(order.path("currency_id").asText(""));
    if (!"COP".equals(currency)) throw new PeticionImposible("La orden de Mercado Pago no está en COP.");

    IntentoPago intento = intentos.findByTransaccionIdForUpdate(orderId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe el intento de pago."));
    if (!orderId.equalsIgnoreCase(order.path("id").asText(""))
        || !intento.getReferencia().equals(order.path("external_reference").asText(""))
        || new BigDecimal(order.path("total_amount").asText("-1")).compareTo(intento.getMonto()) != 0) {
      throw new PeticionImposible("El identificador o el monto de la orden no coincide con el intento de pago.");
    }

    String status = order.path("status").asText("").toLowerCase(Locale.ROOT);
    if ("PENDING".equals(intento.getEstado()) && "processed".equals(status)) {
      BigDecimal totalPaid = new BigDecimal(order.path("total_paid_amount").asText("0"));
      if (totalPaid.compareTo(intento.getMonto()) != 0) {
        throw new PeticionImposible("El monto pagado de Mercado Pago no coincide con el intento.");
      }
      Pago pago = cuentaService.pagar(intento.getCuenta().getId(), intento.getMonto(), "MERCADO_PAGO");
      if (pago == null) throw new PeticionImposible("El saldo de la cuenta cambió; no se pudo aplicar el pago.");
      intento.setEstado("APPROVED");
    } else if ("PENDING".equals(intento.getEstado())
        && List.of("canceled", "failed").contains(status)) {
      intento.setEstado("DECLINED");
    }
    return intento.getEstado();
  }

  private void validarFirma(String signatureHeader, String requestId, String dataId) {
    if (signatureHeader == null || requestId == null || requestId.isBlank() || dataId == null || dataId.isBlank()) {
      throw new PeticionImposible("La notificación de Mercado Pago no contiene los datos para validar la firma.");
    }
    String timestamp = null;
    String receivedHash = null;
    for (String part : signatureHeader.split(",")) {
      String[] pair = part.trim().split("=", 2);
      if (pair.length != 2) continue;
      if ("ts".equals(pair[0].trim())) timestamp = pair[1].trim();
      if ("v1".equals(pair[0].trim())) receivedHash = pair[1].trim();
    }
    if (timestamp == null || receivedHash == null) {
      throw new PeticionImposible("La firma de la notificación de Mercado Pago no es válida.");
    }
    String manifest = "id:" + dataId.toLowerCase(Locale.ROOT) + ";request-id:" + requestId + ";ts:" + timestamp + ";";
    try {
      Mac hmac = Mac.getInstance("HmacSHA256");
      hmac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      String expectedHash = HexFormat.of().formatHex(hmac.doFinal(manifest.getBytes(StandardCharsets.UTF_8)));
      if (!MessageDigest.isEqual(expectedHash.getBytes(StandardCharsets.US_ASCII),
          receivedHash.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII))) {
        throw new PeticionImposible("La firma de la notificación de Mercado Pago no es válida.");
      }
    } catch (PeticionImposible exception) {
      throw exception;
    } catch (Exception exception) {
      throw new IllegalStateException("No fue posible validar la firma de Mercado Pago.", exception);
    }
  }
}
