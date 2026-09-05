package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ReservaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservaServiceImpl implements ReservaService {

  private static final List<String> ESTADOS_ACTIVOS =
      List.of("ACTIVA", "CONFIRMADA", "PENDIENTE");
  private static final List<String> ESTADOS_HISTORICOS =
      List.of("FINALIZADA", "CANCELADA");

  private final ReservaRepository repository;

  public ReservaServiceImpl(ReservaRepository repository) {
    this.repository = repository;
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> obtenerReservasActivas() {
    return repository.findByEstadoInOrderByIdAsc(ESTADOS_ACTIVOS);
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> obtenerHistorial() {
    return repository.findByEstadoInOrderByIdAsc(ESTADOS_HISTORICOS);
  }

  /** {@inheritDoc} */
  @Override
  public Reserva buscarPorId(String numeroReserva) {
    return repository.findByNumeroReserva(numeroReserva).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> buscarActivasDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : repository.buscarVigentesPorCliente(cliente, LocalDate.now());
  }

  /** {@inheritDoc} */
  @Override
  public List<Reserva> buscarHistorialDeCliente(Cliente cliente) {
    return cliente == null
        ? List.of()
        : repository.buscarHistoricasPorCliente(cliente, LocalDate.now());
  }
}
