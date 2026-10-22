package com.hotel.macondo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.utils.FechaUtils;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

  @Autowired
  private ClienteRepository repository;

  @Autowired
  private ReservaRepository reservaRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  // ==========================================
  // CONSULTAS DE CLIENTE
  // ==========================================

  // Busca un cliente por id
  @Override
  public Cliente buscarPorId(Long id) {
    return repository.findById(id).orElseThrow(
        () -> new RecursoNoEncontradoException("No se encontró cliente con id " + id));
  }

  // ==========================================
  // PERSISTENCIA
  // ==========================================

  // Guarda o actualiza un cliente en la base de datos
  @Override
  public Cliente guardar(Cliente cliente) {
    return repository.save(cliente);
  }

  /**
   * Elimina un cliente y desvincula su usuario si no cuenta con reservas.
   * Si registra historial o reservas activas, bloquea la operacion
   * mediante PeticionImposible para proteger la integridad contable
   */
  @Override
  public void eliminar(Long id) {
    Cliente cliente = repository.findById(id).orElse(null);
    if (cliente == null) {
      return;
    }

    if (cliente.getReservas() != null && !cliente.getReservas().isEmpty()) {
      throw new PeticionImposible(
          "No se puede eliminar el cliente: registra reservas en su historial.");
    }

    Usuario usuario = cliente.getUsuario();
    if (usuario != null) {
      cliente.setUsuario(null);
      usuarioRepository.delete(usuario);
    }

    repository.delete(cliente);
  }

  // ==========================================
  // RESERVAS VINCULADAS AL CLIENTE
  // ==========================================

  // Obtiene la primera reserva vigente del cliente para la fecha actual
  @Override
  public Reserva obtenerReservaActiva(Cliente cliente) {
    if (cliente == null) {
      return null;
    }
    List<Reserva> activas = reservaRepository.buscarVigentesPorCliente(cliente, LocalDate.now());
    return activas.isEmpty() ? null : activas.get(0);
  }

  // Calcula la cantidad de noches de estancia entre fecha de inicio y fin
  @Override
  public long calcularNoches(Reserva reserva) {
    if (reserva == null) {
      return 0;
    }
    return FechaUtils.calcularNoches(reserva.getFechaInicio(), reserva.getFechaFin());
  }
}