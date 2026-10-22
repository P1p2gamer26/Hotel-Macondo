package com.hotel.macondo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ServicioRepository;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

  @Autowired
  private ServicioRepository repository;

  // ==========================================
  // CONSULTAS PÚBLICAS Y CLIENTE
  // ==========================================

  // Retorna unicamente los servicios activos para el portal publico y landing
  @Override
  public List<Servicio> obtenerCatalogoActivo() {
    return repository.findByActivoTrueOrderByIdAsc();
  }

  // Retorna una lista acotada de servicios activos recomendados para el cliente
  @Override
  public List<Servicio> obtenerRecomendaciones(int limite) {
    if (limite <= 0) {
      return List.of();
    }
    return repository.findByActivoTrueOrderByIdAsc(PageRequest.of(0, limite));
  }

  // Retorna servicios relacionados excluyendo el servicio que se esta
  // visualizando
  @Override
  public List<Servicio> obtenerRelacionados(Long servicioActualId, int limite) {
    if (limite <= 0) {
      return List.of();
    }
    return repository.findByActivoTrueAndIdNotOrderByIdAsc(
        servicioActualId, PageRequest.of(0, limite));
  }

  // ==========================================
  // ADMINISTRACIÓN Y MANTENIMIENTO (ADMIN)
  // ==========================================

  // Retorna la totalidad de servicios registrados ordenados por identificador
  @Override
  public Collection<Servicio> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  // Busca un servicio por su identificador unico sin filtrar por su estado
  // activo, permitiendo al administrador editar elementos que esten temporalmente
  // inactivos
  @Override
  public Servicio buscarPorIdIncluyendoInactivos(Long id) {
    return repository.findById(id).orElseThrow(
        () -> new RecursoNoEncontradoException("Servicio no encontrado"));
  }

  // Guarda o actualiza un servicio en la base de datos
  @Override
  public Servicio guardar(Servicio servicio) {
    return repository.save(servicio);
  }

  // Alterna el estado de activacion del servicio
  @Override
  public Servicio cambiarEstado(Long id) {
    Servicio servicio = repository.findById(id).orElse(null);
    if (servicio == null) {
      return null;
    }
    servicio.setActivo(!servicio.isActivo());
    return repository.save(servicio);
  }
}