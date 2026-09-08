package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

  private static final Locale LOCALE_COLOMBIA = new Locale("es", "CO");
  private static final DateTimeFormatter FORMATO_FECHA =
      DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", LOCALE_COLOMBIA);

  private final ClienteRepository repository;
  private final ReservaRepository reservaRepository;
  private final UsuarioRepository usuarioRepository;

  public ClienteServiceImpl(
      ClienteRepository repository,
      ReservaRepository reservaRepository,
      UsuarioRepository usuarioRepository) {
    this.repository = repository;
    this.reservaRepository = reservaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Cliente> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Cliente buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Cliente buscarPorCedula(String cedula) {
    return repository.findByCedula(cedula).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Cliente guardar(Cliente cliente) {
    return repository.save(cliente);
  }

  /** {@inheritDoc} */
  @Override
  public void eliminar(Long id) {
    Cliente cliente = repository.findById(id).orElse(null);
    if (cliente == null) {
      return;
    }

    // El ON DELETE CASCADE de la base cubre la integridad, pero Hibernate no se
    // entera: si quedan hijos en la sesion apuntando al cliente borrado, el
    // flush falla. Por eso se sueltan y se borran aqui, de arriba hacia abajo.
    for (Reserva reserva : cliente.getReservas()) {
      reserva.getHabitaciones().clear();
      reservaRepository.delete(reserva);
    }
    cliente.getReservas().clear();

    Usuario usuario = cliente.getUsuario();
    if (usuario != null) {
      cliente.setUsuario(null);
      usuarioRepository.delete(usuario);
    }

    repository.delete(cliente);
  }

  /** {@inheritDoc} */
  @Override
  public Reserva obtenerReservaActiva(Cliente cliente) {
    if (cliente == null) {
      return null;
    }
    List<Reserva> activas =
        reservaRepository.buscarVigentesPorCliente(cliente, LocalDate.now());
    return activas.isEmpty() ? null : activas.get(0);
  }

  /** {@inheritDoc} */
  @Override
  public Habitacion obtenerHabitacionActiva(Reserva reserva) {
    if (reserva == null
        || reserva.getHabitaciones() == null
        || reserva.getHabitaciones().isEmpty()) {
      return null;
    }
    return reserva.getHabitaciones().get(0);
  }

  /** {@inheritDoc} */
  @Override
  public long calcularNoches(Reserva reserva) {
    if (reserva == null
        || reserva.getFechaInicio() == null
        || reserva.getFechaFin() == null) {
      return 0;
    }
    return ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
  }

  /** {@inheritDoc} */
  @Override
  public int contarReservasActivas(Cliente cliente) {
    return cliente == null
        ? 0
        : Math.toIntExact(
            reservaRepository.contarVigentesPorCliente(cliente, LocalDate.now()));
  }

  /** {@inheritDoc} */
  @Override
  public int contarHistorialReservas(Cliente cliente) {
    return cliente == null
        ? 0
        : Math.toIntExact(
            reservaRepository.contarHistoricasPorCliente(cliente, LocalDate.now()));
  }

  /** {@inheritDoc} */
  @Override
  public String obtenerFechaActualFormateada() {
    String texto = FORMATO_FECHA.format(LocalDate.now());
    return texto.isEmpty()
        ? texto
        : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
  }
}
