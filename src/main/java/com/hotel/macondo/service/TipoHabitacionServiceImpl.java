package com.hotel.macondo.service;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    return repository.findById(id).orElse(null);
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
  public boolean eliminar(Long id) {
    if (habitacionService.existeHabitacionConTipo(id)) {
      return false;
    }
    repository.deleteById(id);
    return true;
  }
}
