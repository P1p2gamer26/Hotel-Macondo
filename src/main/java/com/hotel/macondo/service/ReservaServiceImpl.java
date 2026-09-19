package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

  private static final List<String> ESTADOS_ACTIVOS = List.of("ACTIVA", "CONFIRMADA", "PENDIENTE");
  private static final List<String> ESTADOS_HISTORICOS = List.of("FINALIZADA", "CANCELADA");
  private static final List<String> ESTADOS_QUE_OCUPAN_HABITACION = List.of("ACTIVA", "CONFIRMADA", "PENDIENTE");

  @Autowired
  private ReservaRepository reservaRepository;
  private ClienteRepository clienteRepository;
  private TipoHabitacionRepository tipoHabitacionRepository;
  private HabitacionRepository habitacionRepository;
  private CuentaRepository cuentaRepository;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<Reserva> buscarTodas() {
    return reservaRepository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<Reserva> obtenerReservasActivas() {
    return reservaRepository.findByEstadoInOrderByIdAsc(ESTADOS_ACTIVOS);
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> obtenerHistorial() {
    return reservaRepository.findByEstadoInOrderByIdAsc(ESTADOS_HISTORICOS);
  }

  /** {@inheritDoc} */
  @Override
  public Reserva buscarPorId(String numeroReserva) {
    return reservaRepository.findByNumeroReserva(numeroReserva).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> buscarActivasDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : reservaRepository.buscarVigentesPorCliente(cliente, LocalDate.now());
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> buscarHistorialDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : reservaRepository.buscarHistoricasPorCliente(cliente, LocalDate.now());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public long contarPorEstado(List<Reserva> reservas, String estado) {
    if (reservas == null || estado == null) {
      return 0;
    }
    return reservas.stream()
        .filter(reserva -> estado.equalsIgnoreCase(reserva.getEstado()))
        .count();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public long calcularNoches(List<Reserva> reservas) {
    if (reservas == null) {
      return 0;
    }
    return reservas.stream()
        .filter(
            reserva -> reserva.getFechaInicio() != null && reserva.getFechaFin() != null)
        .mapToLong(
            reserva -> ChronoUnit.DAYS.between(
                reserva.getFechaInicio(), reserva.getFechaFin()))
        .sum();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<Habitacion> consultarDisponibilidad(
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    return consultarDisponibilidad(
        null,
        tipoId,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<Habitacion> consultarDisponibilidad(
      Reserva reserva,
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    validarDatosReserva(tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    if (tipo.getCapacidadPersonas() < cantidadPersonas) {
      return List.of();
    }
    return buscarHabitacionesDisponibles(
        tipoId,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        reserva == null ? null : reserva.getId());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public Reserva buscarParaModificar(Long clienteId, Long reservaId) {
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    validarReservaFutura(reserva);
    return reserva;
  }

  /** {@inheritDoc} */
  @Override
  public Reserva crear(
      Long clienteId,
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    Cliente cliente = buscarCliente(clienteId);
    validarDatosReserva(tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    validarCapacidad(tipo, cantidadPersonas);

    Habitacion habitacion = obtenerPrimeraDisponible(
        tipoId, fechaEntrada, fechaSalida, cantidadPersonas, null);
    BigDecimal precioNoche = tipo.getPrecioNoche();
    BigDecimal total = calcularTotal(precioNoche, fechaEntrada, fechaSalida);

    Reserva reserva = new Reserva(
        generarNumeroReserva(),
        fechaEntrada,
        fechaSalida,
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

  /** {@inheritDoc} */
  @Override
  public Reserva modificar(
      Long clienteId,
      Long reservaId,
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    validarReservaFutura(reserva);
    validarDatosReserva(tipoId, fechaEntrada, fechaSalida, cantidadPersonas);
    TipoHabitacion tipo = buscarTipo(tipoId);
    validarCapacidad(tipo, cantidadPersonas);

    Habitacion habitacion = obtenerPrimeraDisponible(
        tipoId,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        reserva.getId());
    BigDecimal precioNoche = tipo.getPrecioNoche();
    BigDecimal total = calcularTotal(precioNoche, fechaEntrada, fechaSalida);

    reserva.setFechaInicio(fechaEntrada);
    reserva.setFechaFin(fechaSalida);
    reserva.setCantidadPersonas(cantidadPersonas);
    reserva.setPrecioNoche(precioNoche);
    reserva.setTotal(total);
    reserva.asignarHabitacion(habitacion);
    actualizarCuentaAbierta(reserva);
    return reservaRepository.save(reserva);
  }

  /** {@inheritDoc} */
  @Override
  public Reserva cancelar(Long clienteId, Long reservaId) {
    Reserva reserva = buscarReservaDelCliente(clienteId, reservaId);
    return cancelarReserva(reserva);
  }

  /** {@inheritDoc} */
  @Override
  public Reserva cancelar(String numeroReserva) {
    Reserva reserva = reservaRepository
        .findByNumeroReserva(numeroReserva)
        .orElseThrow(
            () -> new RecursoNoEncontradoException(
                "No se encontró la reserva " + numeroReserva));
    return cancelarReserva(reserva);
  }

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
        .orElseThrow(
            () -> new PeticionImposible(
                "No hay habitaciones disponibles para las fechas seleccionadas."));
  }

  private void validarDatosReserva(
      Long tipoId,
      LocalDate fechaEntrada,
      LocalDate fechaSalida,
      Integer cantidadPersonas) {
    if (tipoId == null
        || fechaEntrada == null
        || fechaSalida == null
        || cantidadPersonas == null) {
      throw new FormularioErroneoException(
          "Tipo, fechas y cantidad de personas son obligatorios.");
    }
    if (fechaEntrada.isBefore(LocalDate.now())) {
      throw new FormularioErroneoException(
          "La fecha de entrada no puede estar en el pasado.");
    }
    if (!fechaSalida.isAfter(fechaEntrada)) {
      throw new FormularioErroneoException(
          "La fecha de salida debe ser posterior a la fecha de entrada.");
    }
    if (cantidadPersonas <= 0) {
      throw new FormularioErroneoException(
          "La cantidad de personas debe ser mayor que cero.");
    }
  }

  private void validarCapacidad(TipoHabitacion tipo, Integer cantidadPersonas) {
    if (tipo.getCapacidadPersonas() < cantidadPersonas) {
      throw new PeticionImposible(
          "El tipo de habitación no admite la cantidad de personas indicada.");
    }
  }

  private void validarReservaFutura(Reserva reserva) {
    if (reserva.getFechaInicio() == null
        || !reserva.getFechaInicio().isAfter(LocalDate.now())) {
      throw new PeticionImposible(
          "Solo se pueden modificar o cancelar reservas que aún no han iniciado.");
    }
    if ("CANCELADA".equalsIgnoreCase(reserva.getEstado())
        || "FINALIZADA".equalsIgnoreCase(reserva.getEstado())) {
      throw new PeticionImposible("La reserva ya no se encuentra activa.");
    }
  }

  private Cliente buscarCliente(Long clienteId) {
    return clienteRepository
        .findById(clienteId)
        .orElseThrow(
            () -> new RecursoNoEncontradoException(
                "No se encontró cliente con id " + clienteId));
  }

  private TipoHabitacion buscarTipo(Long tipoId) {
    return tipoHabitacionRepository
        .findById(tipoId)
        .orElseThrow(
            () -> new RecursoNoEncontradoException(
                "No se encontró el tipo de habitación seleccionado."));
  }

  private Reserva buscarReservaDelCliente(Long clienteId, Long reservaId) {
    Reserva reserva = reservaRepository
        .findById(reservaId)
        .orElseThrow(
            () -> new RecursoNoEncontradoException("No se encontró la reserva."));
    if (reserva.getCliente() == null
        || !reserva.getCliente().getId().equals(clienteId)) {
      throw new RecursoNoEncontradoException(
          "La reserva no pertenece al cliente indicado.");
    }
    return reserva;
  }

  private Reserva cancelarReserva(Reserva reserva) {
    validarReservaFutura(reserva);
    reserva.setEstado("CANCELADA");
    Cuenta cuenta = reserva.getCuenta();
    if (cuenta != null && "ABIERTA".equalsIgnoreCase(cuenta.getEstado())) {
      cuenta.setEstado("CANCELADA");
      cuenta.setTotal(BigDecimal.ZERO);
      cuentaRepository.save(cuenta);
    }
    return reservaRepository.save(reserva);
  }

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

  private BigDecimal calcularSubtotal(DetalleCuenta detalle) {
    if (detalle == null
        || detalle.getPrecio() == null
        || detalle.getCantidad() == null
        || detalle.getCantidad() <= 0) {
      return BigDecimal.ZERO;
    }
    return detalle.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
  }

  private BigDecimal calcularTotal(
      BigDecimal precioNoche, LocalDate fechaEntrada, LocalDate fechaSalida) {
    long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    return precioNoche.multiply(BigDecimal.valueOf(noches));
  }

  private String generarNumeroReserva() {
    String numero;
    do {
      numero = "MCD-"
          + UUID.randomUUID()
              .toString()
              .substring(0, 8)
              .toUpperCase();
    } while (reservaRepository.existsByNumeroReserva(numero));
    return numero;
  }
}
