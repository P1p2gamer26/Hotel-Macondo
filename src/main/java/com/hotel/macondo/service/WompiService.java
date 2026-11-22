package com.hotel.macondo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotel.macondo.DTOs.Salida.CheckoutWompiResponse;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.IntentoPagoWompi;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.IntentoPagoWompiRepository;
import com.hotel.macondo.repository.ReservaRepository;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WompiService {
  private final ReservaRepository reservaRepository;
  private final IntentoPagoWompiRepository intentos;
  private final CuentaService cuentaService;

  @Value("${wompi.public-key:}") private String publicKey;
  @Value("${wompi.integrity-secret:}") private String integritySecret;
  @Value("${wompi.events-secret:}") private String eventsSecret;
  @Value("${wompi.environment:test}") private String environment;
  @Value("${wompi.frontend-base-url:http://localhost:4200}") private String frontendBaseUrl;

  public WompiService(ReservaRepository reservaRepository, IntentoPagoWompiRepository intentos,
      CuentaService cuentaService) {
    this.reservaRepository = reservaRepository;
    this.intentos = intentos;
    this.cuentaService = cuentaService;
  }

  @Transactional
  public CheckoutWompiResponse iniciar(Long clienteId, Long reservaId, BigDecimal monto) {
    if (publicKey.isBlank() || integritySecret.isBlank()) {
      throw new PeticionImposible("Wompi no está configurado. Define las llaves de sandbox en el backend.");
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
        || monto.movePointRight(2).stripTrailingZeros().scale() > 0
        || monto.compareTo(cuentaService.calcularSaldo(cuenta)) > 0) {
      throw new FormularioErroneoException("El monto debe ser válido y no superar el saldo pendiente.");
    }
    long centavos = monto.movePointRight(2).longValueExact();
    String referencia = "MCD-" + UUID.randomUUID().toString().replace("-", "");
    IntentoPagoWompi intento = intentos.save(new IntentoPagoWompi(referencia, monto, cuenta));
    String firma = sha256(referencia + centavos + "COP" + integritySecret);
    StringBuilder url = new StringBuilder("https://checkout.wompi.co/p/?")
        .append("public-key=").append(enc(publicKey))
        .append("&currency=COP&amount-in-cents=").append(centavos)
        .append("&reference=").append(enc(referencia))
        .append("&signature%3Aintegrity=").append(firma);
    String redirectUrl = frontendBaseUrl.replaceAll("/$", "") + "/cliente/" + clienteId
        + "/reservas/" + reservaId + "?pago=wompi";
    url.append("&redirect-url=").append(enc(redirectUrl));
    return new CheckoutWompiResponse(intento.getReferencia(), url.toString(), monto, intento.getEstado());
  }

  @Transactional
  public void procesarEvento(JsonNode event, String checksumHeader) {
    if (eventsSecret.isBlank()) throw new PeticionImposible("El webhook de Wompi no está configurado.");
    JsonNode signature = event.path("signature");
    JsonNode properties = signature.path("properties");
    JsonNode timestamp = event.path("timestamp");
    String checksum = signature.path("checksum").asText("");
    if (!checksumHeader.isBlank() && !checksumHeader.equalsIgnoreCase(checksum)) {
      throw new PeticionImposible("Checksum de evento Wompi inválido.");
    }
    StringBuilder base = new StringBuilder();
    JsonNode data = event.path("data");
    for (JsonNode property : properties) {
      JsonNode value = data;
      for (String part : property.asText().split("\\.")) value = value.path(part);
      if (value.isMissingNode() || value.isNull()) throw new PeticionImposible("Evento Wompi incompleto.");
      base.append(value.isValueNode() ? value.asText() : value.toString());
    }
    if (timestamp.isMissingNode()) throw new PeticionImposible("Evento Wompi incompleto.");
    base.append(timestamp.asText()).append(eventsSecret);
    if (checksum.isBlank() || !MessageDigest.isEqual(
        sha256(base.toString()).getBytes(StandardCharsets.US_ASCII), checksum.toLowerCase().getBytes(StandardCharsets.US_ASCII))) {
      throw new PeticionImposible("Firma de evento Wompi inválida.");
    }
    String expectedEnvironment = environment.equalsIgnoreCase("prod") ? "prod" : "test";
    if (!expectedEnvironment.equalsIgnoreCase(event.path("environment").asText())) {
      throw new PeticionImposible("El evento pertenece a otro ambiente de Wompi.");
    }
    if (!"transaction.updated".equals(event.path("event").asText())) return;
    JsonNode tx = data.path("transaction");
    String reference = tx.path("reference").asText("");
    IntentoPagoWompi intento = intentos.findByReferencia(reference)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe el intento de pago."));
    if (!"COP".equals(tx.path("currency").asText("") )
        || tx.path("amount_in_cents").asLong(-1) != intento.getMonto().movePointRight(2).longValueExact()) {
      throw new PeticionImposible("El valor o la moneda del evento no coincide con el intento de pago.");
    }
    String status = tx.path("status").asText("");
      if ("PENDING".equals(intento.getEstado()) && "APPROVED".equals(status)) {
        Pago pago = cuentaService.pagar(intento.getCuenta().getId(), intento.getMonto(), "WOMPI");
        if (pago == null) throw new PeticionImposible("El saldo de la cuenta cambió; no se pudo aplicar el pago.");
        intento.setEstado("APPROVED");
        intento.setTransaccionId(tx.path("id").asText(null));
      } else if ("PENDING".equals(intento.getEstado()) &&
          java.util.List.of("DECLINED", "ERROR", "VOIDED").contains(status)) {
        intento.setEstado(status);
        intento.setTransaccionId(tx.path("id").asText(null));
      }
  }

  private static String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
  private static String sha256(String value) {
    try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
    catch (NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
  }
}
