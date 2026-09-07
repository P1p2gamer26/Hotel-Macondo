package com.hotel.macondo.service;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.exceptions.RecursoNoEncontradoException;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HabitacionServiceImpl implements HabitacionService {

  private final HabitacionRepository repository;
  private final TipoHabitacionRepository tipoHabitacionRepository;
  private final ReservaRepository reservaRepository;

  public HabitacionServiceImpl(
      HabitacionRepository repository,
      TipoHabitacionRepository tipoHabitacionRepository,
      ReservaRepository reservaRepository) {
    this.repository = repository;
    this.tipoHabitacionRepository = tipoHabitacionRepository;
    this.reservaRepository = reservaRepository;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Habitacion> buscarTodas() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Habitacion buscarPorId(Long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new RecursoNoEncontradoException(
                    "No se encontro habitacion con id " + id));
  }

  /** {@inheritDoc} */
  @Override
  public Habitacion buscarPorNombre(String nombre) {
    return repository.findByNombreIgnoreCase(nombre).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Habitacion> buscarPorPersonas(int personas) {
    return repository.findByCapacidadGreaterThanEqualOrderByIdAsc(personas);
  }

  /** {@inheritDoc} */
  @Override
  public long contarTodas() {
    return repository.count();
  }

  /** {@inheritDoc} */
  @Override
  public long contarDisponibles() {
    return repository.countByEstado("DISPONIBLE");
  }

  /** {@inheritDoc} */
  @Override
  public Habitacion guardar(Habitacion habitacion, Long idTipo) {
    if (idTipo == null) {
      return null;
    }
    TipoHabitacion tipo = tipoHabitacionRepository.findById(idTipo).orElse(null);
    if (tipo == null) {
      return null;
    }
    if (habitacion.getNombre() == null
        || habitacion.getNombre().isBlank()
        || habitacion.getNombre().equalsIgnoreCase(tipo.getNombre())) {
      return null;
    }
    aplicarDatosDelTipo(habitacion, tipo);
    return repository.save(habitacion);
  }

  /** {@inheritDoc} */
  @Override
  public Habitacion cambiarEstado(Long id) {
    Habitacion habitacion = repository.findById(id).orElse(null);
    if (habitacion == null) {
      return null;
    }
    String nuevoEstado =
        "DISPONIBLE".equals(habitacion.getEstado())
            ? "NO_DISPONIBLE"
            : "DISPONIBLE";
    habitacion.setEstado(nuevoEstado);
    return repository.save(habitacion);
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public boolean existeHabitacionConTipo(Long idTipo) {
    return idTipo != null && repository.existsByTipoHabitacionId(idTipo);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<Reserva> reservasAsociadas(Long id) {
    return reservaRepository.buscarPorHabitacion(id);
  }

  /** {@inheritDoc} */
  @Override
  public boolean eliminar(Long id) {
    Habitacion habitacion = repository.findById(id).orElse(null);
    if (habitacion == null) {
      return false;
    }

    // Una reserva sin habitacion no tiene sentido, y la cascada tampoco es
    // opcion porque borraria reservas ajenas a esta habitacion. Se rechaza.
    if (!habitacion.getReservas().isEmpty()) {
      return false;
    }

    repository.delete(habitacion);
    return true;
  }

  private void aplicarDatosDelTipo(Habitacion habitacion, TipoHabitacion tipo) {
    habitacion.aplicarTipo(tipo);
    habitacion.setDescripcion(tipo.getDescripcion());
    habitacion.setPrecio(tipo.getPrecioNoche());
    habitacion.setCapacidad(tipo.getCapacidadPersonas());
  }
}
