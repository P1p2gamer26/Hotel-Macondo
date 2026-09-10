package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

@Service
@Transactional
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

  private final TipoHabitacionRepository repository;
  private final HabitacionService habitacionService;

  public TipoHabitacionServiceImpl(
      TipoHabitacionRepository repository, HabitacionService habitacionService) {
    this.repository = repository;
    this.habitacionService = habitacionService;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<TipoHabitacion> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public TipoHabitacion buscarPorId(Long id) {
    return repository.findById(id).orElseThrow(
      () -> new RecursoNoEncontradoException("Tipo de habitacion no encontrado.")
    );
  }

  /** {@inheritDoc} */
  @Override
  public TipoHabitacion guardar(TipoHabitacion tipo) {
    TipoHabitacion guardado = repository.save(tipo);
    habitacionService.actualizarHabitacionesPorTipo(guardado);
    return guardado;
  }

  /** {@inheritDoc} */
  @Override
  public void eliminar(Long id) {
    if (habitacionService.existeHabitacionConTipo(id)) {
      throw new PeticionImposible(
          "No se puede eliminar: el tipo de habitacion tiene habitaciones asignadas.");
    }
    repository.deleteById(id);
  }
}
