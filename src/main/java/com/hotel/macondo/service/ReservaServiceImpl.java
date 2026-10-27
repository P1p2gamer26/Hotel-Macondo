package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.dto.ConsumoServicioSolicitud;
import com.hotel.macondo.dto.PagoClienteResponse;
import com.hotel.macondo.dto.ReservaDetalleOperadorResponse;
import com.hotel.macondo.dto.ReservaOperadorResponse;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.PagoRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import com.hotel.macondo.utils.FechaUtils;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

  private static final List<String> ESTADOS_QUE_OCUPAN_HABITACION = List.of("ACTIVA", "CONFIRMADA", "PENDIENTE");

  @Autowired
  private ReservaRepository reservaRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private TipoHabitacionRepository tipoHabitacionRepository;

  @Autowired
  private HabitacionRepository habitacionRepository;

  @Autowired
  private CuentaRepository cuentaRepository;

  @Autowired
  private CuentaService cuentaService;

  @Autowired
  private PagoRepository pagoRepository;

  // ==========================================
  // OPERACIONES DEL CLIENTE
  // ==========================================

  // Retorna las reservas vigentes asociadas a un cliente especifico
  @Override
  public List<Reserva> buscarActivasDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : reservaRepository.buscarVigentesPorCliente(cliente, LocalDate.now());
  }

  // Retorna el historial de reservas finalizadas o canceladas de un cliente
  @Override
  public List<Reserva> buscarHistorialDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : reservaRepository.buscarHistoricasPorCliente(cliente, LocalDate.now());
  }

  // Consulta disponibilidad para una reserva nueva delegando con reserva nula -
  // método de sobrecarga
  @Override
  public List<Habitacion> consultarDisponibilidad(
      Long tipoId,
      String fechaEntrada,
      String fechaSalida,
      Integer cantidadPersonas) {
    return consultarDisponibilidad(null, tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
  }

  /**
   * Consulta las habitaciones libres de un tipo para un rango de fechas.
   * Si se recibe una reserva existente, excluye su identificador para evitar
   * falsos positivos de ocupacion al modificar fechas de esa misma reserva.
   */
  @Override
  public List<Habitacion> consultarDisponibilidad(
      Reserva reserva,
      Long tipoId,
      String fechaEntrada,
      String fechaSalida,
      Integer cantidadPersonas) {
    LocalDate entrada = FechaUtils.convertirFecha(fechaEntrada, "entrada");
    LocalDate salida = FechaUtils.convertirFecha(fechaSalida, "salida");
    validarDatosReserva(tipoId, entrada, salida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    if (tipo.getCapacidadPersonas() < cantidadPersonas) {
      return List.of();
    }
    return buscarHabitacionesDisponibles(
        tipoId,
        entrada,
        salida,
        cantidadPersonas,
        reserva == null ? null : reserva.getId());
  }

  // Obtiene una reserva futura del cliente para precargar su formulario de
  // edicion
  @Override
  @Transactional(readOnly = true)
  public Reserva buscarParaModificar(Long clienteId, Long reservaId) {
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    validarReservaFutura(reserva);
    return reserva;
  }

  // Genera una nueva reserva confirmada asignando la primera habitacion
  // disponible y abre su cuenta hotelera asociada dentro de la misma transaccion

  @Override
  public Reserva crear(
      Long clienteId,
      Long tipoId,
      String fechaEntrada,
      String fechaSalida,
      Integer cantidadPersonas) {
    LocalDate entrada = FechaUtils.convertirFecha(fechaEntrada, "entrada");
    LocalDate salida = FechaUtils.convertirFecha(fechaSalida, "salida");
    Cliente cliente = buscarCliente(clienteId);
    validarDatosReserva(tipoId, entrada, salida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    validarCapacidad(tipo, cantidadPersonas);

    Habitacion habitacion = obtenerPrimeraDisponible(tipoId, entrada, salida, cantidadPersonas, null);
    BigDecimal precioNoche = tipo.getPrecioNoche();
    BigDecimal total = calcularTotal(precioNoche, entrada, salida);

    Reserva reserva = new Reserva(
        generarNumeroReserva(),
        entrada,
        salida,
        cantidadPersonas,
        "CONFIRMADA",
        precioNoche,
        total);
    reserva.asignarCliente(cliente);
    reserva.asignarHabitacion(habitacion);
    reservaRepository.save(reserva);

    Cuenta cuenta = new Cuenta("ABIERTA", total, LocalDateTime.now());
    cuenta.asignarReserva(reserva);
    cuentaRepository.save(cuenta);

    return reserva;
  }

  // Modifica fechas o capacidad de una reserva futura y actualiza el total
  // de la cuenta abierta conservando los consumos de servicios previos
  @Override
  public Reserva modificar(
      Long clienteId,
      Long reservaId,
      Long tipoId,
      String fechaEntrada,
      String fechaSalida,
      Integer cantidadPersonas) {
    LocalDate entrada = FechaUtils.convertirFecha(fechaEntrada, "entrada");
    LocalDate salida = FechaUtils.convertirFecha(fechaSalida, "salida");
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    validarReservaFutura(reserva);
    validarDatosReserva(tipoId, entrada, salida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    validarCapacidad(tipo, cantidadPersonas);

    Habitacion habitacion = obtenerPrimeraDisponible(
        tipoId,
        entrada,
        salida,
        cantidadPersonas,
        reserva.getId());
    BigDecimal precioNoche = tipo.getPrecioNoche();
    BigDecimal total = calcularTotal(precioNoche, entrada, salida);

    reserva.setFechaInicio(entrada);
    reserva.setFechaFin(salida);
    reserva.setCantidadPersonas(cantidadPersonas);
    reserva.setPrecioNoche(precioNoche);
    reserva.setTotal(total);
    reserva.asignarHabitacion(habitacion);
    actualizarCuentaAbierta(reserva);

    return reservaRepository.save(reserva);
  }

  // Cancela una reserva futura perteneciente al cliente
  @Override
  public Reserva cancelar(Long clienteId, Long reservaId) {
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    return cancelarReserva(reserva);
  }

  // ==========================================
  // OPERACIONES DEL OPERADOR
  // ==========================================

  // Retorna todas las reservas proyectadas en DTO resumido para el operador
  @Override
  public List<ReservaOperadorResponse> obtenerTodasParaOperador() {
    return reservaRepository.findAllByOrderByIdAsc().stream()
        .map(this::convertirARespuestaOperador)
        .toList();
  }

  // Cancela una reserva desde el panel del operador y devuelve el DTO resumido
  @Override
  public ReservaOperadorResponse cancelarParaOperador(String numeroReserva) {
    Reserva reserva = buscarReservaPorNumero(numeroReserva);
    validarEstadoConfirmado(reserva, "cancelar");
    return convertirARespuestaOperador(marcarReservaCancelada(reserva));
  }

  // Activa una reserva confirmada si su habitación no tiene otra estadía activa
  @Override
  public ReservaOperadorResponse activarParaOperador(String numeroReserva) {
    Reserva reserva = buscarReservaPorNumero(numeroReserva);
    validarEstadoConfirmado(reserva, "activar");
    Habitacion habitacion = reserva.getHabitacion();
    if (habitacion != null
        && reservaRepository.contarOtrasReservasActivasPorHabitacion(
            habitacion.getId(), reserva.getId()) > 0) {
      throw new PeticionImposible(
          "No se puede activar la reserva porque la habitación "
              + habitacion.getNumero()
              + " ya tiene otra reserva activa.");
    }
    reserva.setEstado("ACTIVA");
    return convertirARespuestaOperador(reservaRepository.save(reserva));
  }

  // Finaliza una reserva activa cuya cuenta ya fue pagada.
  @Override
  public ReservaOperadorResponse finalizarParaOperador(String numeroReserva) {
    Reserva reserva = buscarReservaPorNumero(numeroReserva);
    if (reserva.getEstado() == null || !reserva.getEstado().equalsIgnoreCase("ACTIVA")) {
      throw new PeticionImposible("Solo se pueden finalizar reservas activas.");
    }
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta != null
        && !"PAGADA".equalsIgnoreCase(cuenta.getEstado())
        && cuentaService.calcularSaldo(cuenta).compareTo(BigDecimal.ZERO) > 0) {
      throw new PeticionImposible("La cuenta debe estar pagada para finalizar la estadía.");
    }
    reserva.setEstado("FINALIZADA");
    return convertirARespuestaOperador(reservaRepository.save(reserva));
  }

  // Paga la cuenta de una reserva activa desde el panel del operador.
  @Override
  public Pago pagarCuentaParaOperador(String numeroReserva, String metodoPago) {
    Reserva reserva = buscarReservaPorNumero(numeroReserva);
    if (reserva.getEstado() == null || !reserva.getEstado().equalsIgnoreCase("ACTIVA")) {
      throw new PeticionImposible("Solo se puede pagar la cuenta de una reserva activa.");
    }
    if (metodoPago == null || !List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA").contains(metodoPago.trim().toUpperCase())) {
      throw new FormularioErroneoException("Método de pago inválido.");
    }
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta == null) {
      throw new RecursoNoEncontradoException("La reserva no tiene una cuenta asociada.");
    }
    Pago pago = cuentaService.pagar(cuenta.getId(), cuentaService.calcularSaldo(cuenta), metodoPago.trim().toUpperCase());
    if (pago == null) {
      throw new PeticionImposible("La cuenta no se puede pagar: debe estar abierta y tener saldo.");
    }
    return pago;
  }

  // Agrega servicios a la cuenta de una reserva activa desde el panel del operador
  @Override
  public void agregarServiciosParaOperador(String numeroReserva, List<ConsumoServicioSolicitud> servicios) {
    Reserva reserva = reservaRepository
        .findByNumeroReserva(numeroReserva)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva " + numeroReserva));
    if (reserva.getEstado() == null || !reserva.getEstado().equalsIgnoreCase("ACTIVA")) {
      throw new PeticionImposible("Solo se pueden agregar servicios a una reserva activa.");
    }
    cuentaService.agregarServicios(obtenerCuentaId(numeroReserva), servicios);
  }

  // Elimina un consumo de la cuenta de una reserva activa desde el panel del operador
  @Override
  public void eliminarServicioParaOperador(String numeroReserva, Long detalleId) {
    Reserva reserva = reservaRepository
        .findByNumeroReserva(numeroReserva)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva " + numeroReserva));
    if (reserva.getEstado() == null || !reserva.getEstado().equalsIgnoreCase("ACTIVA")) {
      throw new PeticionImposible("Solo se pueden eliminar servicios de una reserva activa.");
    }
    if (!cuentaService.eliminarDetalle(obtenerCuentaId(numeroReserva), detalleId)) {
      throw new RecursoNoEncontradoException("El consumo no existe en la cuenta abierta de la reserva.");
    }
  }

  // Retorna el historial de pagos de todas las reservas del cliente
  @Override
  @Transactional(readOnly = true)
  public List<PagoClienteResponse> historialPagosDeCliente(Long clienteId) {
    return pagoRepository.buscarPorClienteId(clienteId).stream()
        .map(pago -> new PagoClienteResponse(
            pago.getId(),
            pago.getCuenta() == null || pago.getCuenta().getReserva() == null
                ? null
                : pago.getCuenta().getReserva().getNumeroReserva(),
            pago.getMonto(),
            pago.getMetodoPago(),
            pago.getFechaPago(),
            pago.getEstado()))
        .toList();
  }

  // Cancela una reserva buscada por su numero publico
  @Override
  public Reserva cancelar(String numeroReserva) {
    Reserva reserva = buscarReservaPorNumero(numeroReserva);
    return cancelarReserva(reserva);
  }

  private Reserva buscarReservaPorNumero(String numeroReserva) {
    return reservaRepository
        .findByNumeroReserva(numeroReserva)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva " + numeroReserva));
  }

  private void validarEstadoConfirmado(Reserva reserva, String accion) {
    if (!"CONFIRMADA".equalsIgnoreCase(reserva.getEstado())) {
      throw new PeticionImposible("Solo se pueden " + accion + " reservas confirmadas.");
    }
  }

  // Obtiene la reserva con sus detalles
  @Override
  @Transactional(readOnly = true)
  public Reserva obtenerDetalle(String numeroReserva) {
    Reserva reserva = reservaRepository
        .buscarDetalleCompleto(numeroReserva)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + numeroReserva));

    if (reserva.getCuenta() != null) {
      reserva.getCuenta().getPagos().size();
    }
    return reserva;
  }

  // Retorna el desglose de una reserva en el apartado de detalle
  @Override
  @Transactional(readOnly = true)
  public ReservaDetalleOperadorResponse obtenerDetalleParaOperador(String numeroReserva) {
    return convertirDetalleOperador(obtenerDetalle(numeroReserva));
  }

  @Override
  @Transactional(readOnly = true)
  public ReservaDetalleOperadorResponse obtenerDetalleParaOperador(Long reservaId) {
    Reserva reserva = reservaRepository
        .buscarDetalleCompletoPorId(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva " + reservaId));

    if (reserva.getCuenta() != null) {
      reserva.getCuenta().getPagos().size();
    }
    return convertirDetalleOperador(reserva);
  }

  private ReservaDetalleOperadorResponse convertirDetalleOperador(Reserva reserva) {
    Cliente cliente = reserva.getCliente();
    Habitacion habitacion = reserva.getHabitacion();
    TipoHabitacion tipoHabitacion = habitacion == null ? null : habitacion.getTipoHabitacion();
    Cuenta cuenta = reserva.getCuenta();

    return new ReservaDetalleOperadorResponse(
        reserva.getId(),
        reserva.getNumeroReserva(),
        reserva.getFechaInicio(),
        reserva.getFechaFin(),
        reserva.getCantidadPersonas(),
        reserva.getEstado(),
        reserva.getPrecioNoche(),
        reserva.getTotal(),
        cliente == null
            ? null
            : new ReservaDetalleOperadorResponse.ClienteDetalle(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getCedula(),
                cliente.getTelefono(),
                cliente.getCorreo()),
        habitacion == null
            ? null
            : new ReservaDetalleOperadorResponse.HabitacionDetalle(
                habitacion.getId(),
                habitacion.getNombre(),
                habitacion.getEtiqueta(),
                habitacion.getPrecio(),
                habitacion.getCapacidad(),
                habitacion.getNumero(),
                habitacion.getEstado(),
                habitacion.getPiso(),
                tipoHabitacion == null
                    ? null
                    : new ReservaDetalleOperadorResponse.TipoHabitacionDetalle(
                        tipoHabitacion.getId(),
                        tipoHabitacion.getNombre(),
                        tipoHabitacion.getDescripcion(),
                        tipoHabitacion.getImagen(),
                        tipoHabitacion.getPrecioNoche(),
                        tipoHabitacion.getCapacidadPersonas())),
        cuenta == null
            ? null
            : new ReservaDetalleOperadorResponse.CuentaDetalle(
                cuenta.getId(),
                cuenta.getEstado(),
                cuenta.getTotal(),
                cuenta.getFechaApertura(),
                cuenta.getDetalles().stream()
                    .map(detalle -> new ReservaDetalleOperadorResponse.ConsumoDetalle(
                        detalle.getId(),
                        detalle.getCantidad(),
                        detalle.getPrecio(),
                        detalle.getFechaRegistro(),
                        detalle.getServicio() == null ? null : detalle.getServicio().getNombre()))
                    .toList(),
                cuenta.getPagos().stream()
                    .map(pago -> new ReservaDetalleOperadorResponse.PagoDetalle(
                        pago.getId(),
                        pago.getMonto(),
                        pago.getMetodoPago(),
                        pago.getFechaPago(),
                        pago.getEstado()))
                    .toList()));
  }

  // Retorna el identificador de la cuenta asociada al número público de reserva
  @Override
  public Long obtenerCuentaId(String numeroReserva){
    Reserva reserva = reservaRepository
        .findByNumeroReserva(numeroReserva)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva " + numeroReserva));
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta == null) {
      throw new RecursoNoEncontradoException("La reserva no tiene una cuenta asociada.");
    }
    return cuenta.getId();
  }

  // Busca la reserva activa con cuenta abierta de la habitación para cargarle servicios
  @Override
  @Transactional(readOnly = true)
  public Long obtenerIdReservaActivaPorNumeroHabitacion(String numeroHabitacion) {
    List<Long> idsReserva =
        reservaRepository.buscarIdsActivosConCuentaAbiertaPorNumeroHabitacion(numeroHabitacion);

    if (idsReserva.isEmpty()) {
      throw new RecursoNoEncontradoException(
          "No hay una reserva activa con cuenta abierta en la habitación " + numeroHabitacion);
    }
    if (idsReserva.size() > 1) {
      throw new FormularioErroneoException(
          "Hay más de una reserva activa con cuenta abierta en la habitación " + numeroHabitacion);
    }

    return idsReserva.get(0);
  }

  // Busca la reserva activa de la habitación para el check-out, sin importar el estado de la cuenta
  @Override
  @Transactional(readOnly = true)
  public Long obtenerIdReservaEnCheckout(String numeroHabitacion) {
    List<Long> idsReserva =
        reservaRepository.buscarIdsActivosPorNumeroHabitacion(numeroHabitacion);

    if (idsReserva.isEmpty()) {
      throw new RecursoNoEncontradoException(
          "No hay una reserva activa en la habitación " + numeroHabitacion);
    }
    if (idsReserva.size() > 1) {
      throw new FormularioErroneoException(
          "Hay más de una reserva activa en la habitación " + numeroHabitacion);
    }

    return idsReserva.get(0);
  }

  // ==========================================
  // DISPONIBILIDAD Y CONSULTAS INTERNAS
  // ==========================================

  // Consulta en repositorio las habitaciones que no presenten cruce de fechas
  private List<Habitacion> buscarHabitacionesDisponibles(
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas,
      Long reservaExcluidaId) {
    return habitacionRepository.buscarDisponibles(
        tipoId,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        ESTADOS_QUE_OCUPAN_HABITACION,
        reservaExcluidaId);
  }

  // Retorna la primera habitacion disponible
  private Habitacion obtenerPrimeraDisponible(
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas,
      Long reservaExcluidaId) {
    return buscarHabitacionesDisponibles(
        tipoId,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        reservaExcluidaId)
        .stream()
        .findFirst()
        .orElseThrow(() -> new PeticionImposible("No hay habitaciones disponibles para las fechas seleccionadas."));
  }

  // Busca el cliente por id
  private Cliente buscarCliente(Long clienteId) {
    return clienteRepository
        .findById(clienteId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró cliente con id " + clienteId));
  }

  // Busca el tipo de habitacion por id
  private TipoHabitacion buscarTipo(Long tipoId) {
    return tipoHabitacionRepository
        .findById(tipoId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el tipo de habitación seleccionado."));
  }

  // Valida que la reserva exista y que pertenezca al cliente especificado
  private Reserva buscarReservaDelCliente(Long clienteId, Long reservaId) {
    Reserva reserva = reservaRepository
        .findById(reservaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la reserva."));
    if (reserva.getCliente() == null || !reserva.getCliente().getId().equals(clienteId)) {
      throw new RecursoNoEncontradoException("La reserva no pertenece al cliente indicado.");
    }
    return reserva;
  }

  // ==========================================
  // GESTIÓN DE CUENTA Y TOTALES
  // ==========================================

  // Cancela la reserva y anula el saldo de la cuenta si continua abierta
  private Reserva cancelarReserva(Reserva reserva) {
    validarReservaFutura(reserva);
    return marcarReservaCancelada(reserva);
  }

  private Reserva marcarReservaCancelada(Reserva reserva) {
    reserva.setEstado("CANCELADA");
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta != null && "ABIERTA".equalsIgnoreCase(cuenta.getEstado())) {
      cuenta.setEstado("CANCELADA");
      cuenta.setTotal(BigDecimal.ZERO);
      cuentaRepository.save(cuenta);
    }
    return reservaRepository.save(reserva);
  }

  // Recalcula el total de la cuenta abierta sumando alojamiento y consumos
  private void actualizarCuentaAbierta(Reserva reserva) {
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta == null || !"ABIERTA".equalsIgnoreCase(cuenta.getEstado())) {
      return;
    }
    BigDecimal totalServicios = cuenta.getDetalles().stream()
        .map(this::calcularSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    cuenta.setTotal(reserva.getTotal().add(totalServicios));
    cuentaRepository.save(cuenta);
  }

  // Multiplica el precio por la cantidad de consumos del detalle
  private BigDecimal calcularSubtotal(DetalleCuenta detalle) {
    if (detalle == null
        || detalle.getPrecio() == null
        || detalle.getCantidad() == null
        || detalle.getCantidad() <= 0) {
      return BigDecimal.ZERO;
    }
    return detalle.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
  }

  // Multiplica el costo de la noche por la cantidad de dias de estadia
  private BigDecimal calcularTotal(BigDecimal precioNoche, LocalDate fechaEntrada, LocalDate fechaSalida) {
    long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    return precioNoche.multiply(BigDecimal.valueOf(noches));
  }

  // Genera un codigo unico de reserva con prefijo MCD
  private String generarNumeroReserva() {
    String numero;
    do {
      numero = "MCD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    } while (reservaRepository.existsByNumeroReserva(numero));
    return numero;
  }

  // Mapea una entidad reserva de respuesta para operador
  private ReservaOperadorResponse convertirARespuestaOperador(Reserva reserva) {
    Cliente cliente = reserva.getCliente();
    Habitacion habitacion = reserva.getHabitacion();
    TipoHabitacion tipoHabitacion = habitacion == null ? null : habitacion.getTipoHabitacion();

    return new ReservaOperadorResponse(
        reserva.getId(),
        reserva.getNumeroReserva(),
        reserva.getFechaInicio(),
        reserva.getFechaFin(),
        reserva.getCantidadPersonas(),
        reserva.getEstado(),
        reserva.getPrecioNoche(),
        reserva.getTotal(),
        cliente == null
            ? null
            : new ReservaOperadorResponse.ClienteResumen(
                cliente.getId(), cliente.getNombre(), cliente.getApellido()),
        habitacion == null
            ? null
            : new ReservaOperadorResponse.HabitacionResumen(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getNombre(),
                tipoHabitacion == null
                    ? null
                    : new ReservaOperadorResponse.TipoHabitacionResumen(
                        tipoHabitacion.getId(), tipoHabitacion.getNombre())));
  }

  // ==========================================
  // VALIDACIONES
  // ==========================================

  // Valida que los datos requeridos de reserva no sean nulos ni presenten fechas
  // incoherentes
  private void validarDatosReserva(
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    if (tipoId == null || fechaEntrada == null || fechaSalida == null || cantidadPersonas == null) {
      throw new FormularioErroneoException("Tipo, fechas y cantidad de personas son obligatorios.");
    }
    if (fechaEntrada.isBefore(LocalDate.now())) {
      throw new FormularioErroneoException("La fecha de entrada no puede estar en el pasado.");
    }
    if (!fechaSalida.isAfter(fechaEntrada)) {
      throw new FormularioErroneoException("La fecha de salida debe ser posterior a la fecha de entrada.");
    }
    if (cantidadPersonas <= 0) {
      throw new FormularioErroneoException("La cantidad de personas debe ser mayor que cero.");
    }
  }

  // Comprueba que la capacidad maxima del tipo de habitacion admita a los
  // huespedes
  private void validarCapacidad(TipoHabitacion tipo, Integer cantidadPersonas) {
    if (tipo.getCapacidadPersonas() < cantidadPersonas) {
      throw new PeticionImposible("El tipo de habitación no admite la cantidad de personas indicada.");
    }
  }

  // Valida que la reserva no haya iniciado y que su estado no sea finalizada ni
  // cancelada
  private void validarReservaFutura(Reserva reserva) {
    if (reserva.getFechaInicio() == null || !reserva.getFechaInicio().isAfter(LocalDate.now())) {
      throw new PeticionImposible("Solo se pueden modificar o cancelar reservas que aún no han iniciado.");
    }
    if ("CANCELADA".equalsIgnoreCase(reserva.getEstado()) || "FINALIZADA".equalsIgnoreCase(reserva.getEstado())) {
      throw new PeticionImposible("La reserva ya no se encuentra activa.");
    }
  }


}