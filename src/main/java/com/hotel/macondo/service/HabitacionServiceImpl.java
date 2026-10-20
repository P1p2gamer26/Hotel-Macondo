package com.hotel.macondo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

  @Autowired
  private HabitacionRepository repository;

  @Autowired
  private TipoHabitacionRepository tipoHabitacionRepository;

  @Autowired
  private ReservaRepository reservaRepository;

  // ==========================================
  // CONSULTAS DE HABITACIÓN
  // ==========================================

  // Retorna todas las habitaciones ordenadas por identificador
  @Override
  public Collection<Habitacion> buscarTodas() {
    return repository.findAllByOrderByIdAsc();
  }

  // Busca una habitacion por id
  @Override
  public Habitacion buscarPorId(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new RecursoNoEncontradoException(
                "No se encontro habitacion con id " + id));
  }

  // Busca la habitacion asignada a una reserva especifica
  @Override
  public Habitacion buscarPorReserva(Long reservaId) {
    if (reservaId == null) {
      throw new RecursoNoEncontradoException("ID de reserva no proporcionado");
    }
    return repository.findByReservasId(reservaId);
  }

  // Retorna la lista de reservas vinculadas a una habitacion
  @Override
  @Transactional(readOnly = true)
  public List<Reserva> reservasAsociadas(Long id) {
    return reservaRepository.buscarPorHabitacion(id);
  }

  // Comprueba si existen habitaciones asignadas a un tipo determinado
  @Override
  public boolean existeHabitacionConTipo(Long idTipo) {
    return idTipo != null && repository.existsByTipoHabitacionId(idTipo);
  }

  // ==========================================
  // GESTIÓN Y MANTENIMIENTO (ADMIN)
  // ==========================================

  // Guarda o actualiza una habitacion validando unicidad de numero y nombre,
  // existencia del tipo y sincronizando precio y capacidad desde su categoria.
  @Override
  public Habitacion guardar(Habitacion habitacion, Long idTipo) {
    if (idTipo == null) {
      throw new FormularioErroneoException("El tipo de habitación es obligatorio.");
    }
    TipoHabitacion tipo = tipoHabitacionRepository.findById(idTipo).orElse(null);
    if (tipo == null) {
      throw new FormularioErroneoException("El tipo de habitación no existe.");
    }
    if (habitacion.getNombre() == null
        || habitacion.getNombre().isBlank()
        || habitacion.getNombre().equalsIgnoreCase(tipo.getNombre())) {
      throw new FormularioErroneoException("El nombre de la habitación es inválido.");
    }
    if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()) {
      throw new FormularioErroneoException("El número de la habitación es obligatorio.");
    }

    // Validar si ya existe otra habitacion con el mismo numero de puerta
    boolean numeroRepetido = repository.findAll().stream()
        .anyMatch(h -> h.getNumero().trim().equalsIgnoreCase(habitacion.getNumero().trim())
            && !h.getId().equals(habitacion.getId()));

    if (numeroRepetido) {
      throw new FormularioErroneoException(
          "Ya existe una habitación registrada con el número " + habitacion.getNumero() + ".");
    }

    // Validar si ya existe otra habitacion con el mismo nombre
    Habitacion conMismoNombre = buscarPorNombre(habitacion.getNombre().trim());
    if (conMismoNombre != null && !conMismoNombre.getId().equals(habitacion.getId())) {
      throw new FormularioErroneoException(
          "Ya existe una habitación registrada con el nombre '" + habitacion.getNombre() + "'.");
    }

    aplicarDatosDelTipo(habitacion, tipo);
    return repository.save(habitacion);
  }

  // Alterna el estado operativo entre DISPONIBLE y NO_DISPONIBLE
  @Override
  public Habitacion cambiarEstado(Long id) {
    Habitacion habitacion = repository.findById(id).orElse(null);
    if (habitacion == null) {
      return null;
    }
    String nuevoEstado = "DISPONIBLE".equals(habitacion.getEstado())
        ? "NO_DISPONIBLE"
        : "DISPONIBLE";
    habitacion.setEstado(nuevoEstado);
    return repository.save(habitacion);
  }

  // Actualiza los datos heredados del tipo en todas sus habitaciones asignadas
  @Override
  public void actualizarHabitacionesPorTipo(TipoHabitacion tipo) {
    if (tipo == null || tipo.getId() == null) {
      return;
    }
    for (Habitacion habitacion : repository.findByTipoHabitacionId(tipo.getId())) {
      aplicarDatosDelTipo(habitacion, tipo);
      repository.save(habitacion);
    }
  }

  /**
   * Elimina una habitacion si no registra reservas historicas o activas
   * Si tiene reservas asociadas, bloquea la operacion con PeticionImposible
   */
  @Override
  public void eliminar(Long id) {
    Habitacion habitacion = repository.findById(id).orElse(null);
    if (habitacion == null) {
      throw new RecursoNoEncontradoException(
          "No se encontro habitacion con id " + id);
    }

    if (!reservaRepository.buscarPorHabitacion(id).isEmpty()) {
      throw new PeticionImposible(
          "No se puede eliminar: la habitacion tiene reservas asociadas.");
    }

    repository.delete(habitacion);
  }

  // ==========================================
  // MÉTODOS AUXILIARES
  // ==========================================

  // Busca internamente una habitacion por su nombre exacto ignorando mayusculas
  @Override
  public Habitacion buscarPorNombre(String nombre) {
    return repository.findByNombreIgnoreCase(nombre).orElse(null);
  }

  // Asigna los valores de referencia del tipo a habitacion
  private void aplicarDatosDelTipo(Habitacion habitacion, TipoHabitacion tipo) {
    habitacion.aplicarTipo(tipo);
    habitacion.setPrecio(tipo.getPrecioNoche());
    habitacion.setCapacidad(tipo.getCapacidadPersonas());
  }
}