package com.hotel.macondo.service;

import com.hotel.macondo.dto.ComentarioClienteResponse;
import com.hotel.macondo.dto.ComentarioEdicion;
import com.hotel.macondo.dto.ComentarioResponse;
import com.hotel.macondo.dto.ComentarioSolicitud;
import com.hotel.macondo.entities.Comentario;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ComentarioRepository;
import com.hotel.macondo.repository.ReservaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ponytail: clase sin interfaz (una sola implementación)
@Service
@Transactional
public class ComentarioService {

  @Autowired
  private ComentarioRepository repository;

  @Autowired
  private ReservaRepository reservaRepository;

  public ComentarioResponse crear(ComentarioSolicitud s) {
    if (s == null || s.clienteId() == null || s.reservaId() == null) {
      throw new FormularioErroneoException("Debes indicar el cliente y la reserva.");
    }
    if (s.calificacion() == null || s.calificacion() < 1 || s.calificacion() > 5) {
      throw new FormularioErroneoException("La calificación debe estar entre 1 y 5.");
    }
    String texto = s.texto() == null ? "" : s.texto().trim();
    if (texto.isEmpty()) {
      throw new FormularioErroneoException("El comentario no puede estar vacío.");
    }
    if (texto.length() > 500) {
      throw new FormularioErroneoException("El comentario no puede superar 500 caracteres.");
    }
    Reserva reserva = reservaRepository.findById(s.reservaId())
        .orElseThrow(() -> new RecursoNoEncontradoException("La reserva no existe."));
    if (reserva.getCliente() == null || !s.clienteId().equals(reserva.getCliente().getId())) {
      throw new PeticionImposible("Solo el titular de la reserva puede comentarla.");
    }
    if (!"FINALIZADA".equals(reserva.getEstado())) {
      throw new PeticionImposible("Solo puedes comentar una estadía finalizada.");
    }
    if (repository.existsByReservaId(reserva.getId())) {
      throw new PeticionImposible("Esta reserva ya tiene un comentario.");
    }
    Comentario c = new Comentario();
    c.setCliente(reserva.getCliente());
    c.setReserva(reserva);
    c.setCalificacion(s.calificacion());
    c.setTexto(texto);
    return aRespuesta(repository.save(c));
  }

  @Transactional(readOnly = true)
  public List<ComentarioResponse> publicados() {
    return repository.findByPublicadoTrueOrderByFechaCreacionDesc().stream().map(this::aRespuesta).toList();
  }

  @Transactional(readOnly = true)
  public List<ComentarioClienteResponse> porCliente(Long clienteId) {
    return repository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream().map(this::aRespuestaCliente).toList();
  }

  // El cliente edita su comentario; al cambiarlo vuelve a revisión del administrador.
  public ComentarioClienteResponse editar(Long id, Long clienteId, ComentarioEdicion e) {
    Comentario c = propio(id, clienteId);
    if (e.calificacion() != null) {
      if (e.calificacion() < 1 || e.calificacion() > 5) {
        throw new FormularioErroneoException("La calificación debe estar entre 1 y 5.");
      }
      c.setCalificacion(e.calificacion());
    }
    if (e.texto() != null) {
      String texto = e.texto().trim();
      if (texto.isEmpty() || texto.length() > 500) {
        throw new FormularioErroneoException("El comentario debe tener entre 1 y 500 caracteres.");
      }
      c.setTexto(texto);
    }
    c.setPublicado(false);
    return aRespuestaCliente(repository.save(c));
  }

  // El cliente borra su propio comentario.
  public void eliminar(Long id, Long clienteId) {
    repository.delete(propio(id, clienteId));
  }

  private Comentario propio(Long id, Long clienteId) {
    Comentario c = repository.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("El comentario no existe."));
    if (c.getCliente() == null || !clienteId.equals(c.getCliente().getId())) {
      throw new PeticionImposible("Solo el autor puede cambiar este comentario.");
    }
    return c;
  }

  private ComentarioClienteResponse aRespuestaCliente(Comentario c) {
    return new ComentarioClienteResponse(c.getId(), c.getReserva().getId(), c.getCalificacion(), c.getTexto(),
        c.getFechaCreacion(), c.isPublicado());
  }

  public ComentarioResponse publicar(Long id) {
    Comentario c = repository.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("El comentario no existe."));
    c.setPublicado(true);
    return aRespuesta(repository.save(c));
  }

  private ComentarioResponse aRespuesta(Comentario c) {
    return new ComentarioResponse(c.getId(), c.getCliente().getNombre(), c.getCalificacion(), c.getTexto(),
        c.getFechaCreacion(), c.isPublicado());
  }
}
