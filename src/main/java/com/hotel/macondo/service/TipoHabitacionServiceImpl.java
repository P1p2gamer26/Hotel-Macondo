package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@Service
@Transactional
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

  @Autowired
  private TipoHabitacionRepository repository;

  @Autowired
  private HabitacionService habitacionService;

  // ==========================================
  // CONSULTAS DE CATEGORÍA
  // ==========================================

  // Retorna todas las categorias de habitacion ordenadas por identificador
  @Override
  public Collection<TipoHabitacion> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  // Busca una categoria por su id
  @Override
  public TipoHabitacion buscarPorId(Long id) {
    return repository.findById(id).orElseThrow(
        () -> new RecursoNoEncontradoException("Tipo de habitacion no encontrado."));
  }

  // ==========================================
  // GESTIÓN Y MANTENIMIENTO (ADMIN)
  // ==========================================

  // Guarda o actualiza la categoria y propaga en cascada las variaciones
  // de tarifa y capacidad a todas las habitaciones vinculadas a este tipo

  @Override
  public TipoHabitacion guardar(TipoHabitacion tipo) {
    TipoHabitacion guardado = repository.save(tipo);
    habitacionService.actualizarHabitacionesPorTipo(guardado);
    return guardado;
  }

  // Elimina la categoria unicamente si no tiene habitaciones asociadas
  // Si existen registros vinculados bloquea la operacion
  @Override
  public void eliminar(Long id) {
    if (habitacionService.existeHabitacionConTipo(id)) {
      throw new PeticionImposible(
          "No se puede eliminar: el tipo de habitacion tiene habitaciones asignadas.");
    }
    repository.deleteById(id);
  }
}