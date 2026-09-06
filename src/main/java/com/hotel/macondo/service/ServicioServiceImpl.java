package com.hotel.macondo.service;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.ServicioRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

  private final ServicioRepository repository;

  public ServicioServiceImpl(ServicioRepository repository) {
    this.repository = repository;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Servicio> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Servicio buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Servicio guardar(Servicio servicio) {
    return repository.save(servicio);
  }

  /** {@inheritDoc} */
  @Override
  public List<Servicio> obtenerCatalogoActivo() {
    return repository.findByActivoTrueOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public List<String> obtenerCategoriasDisponibles() {
    return obtenerCatalogoActivo().stream()
        .map(Servicio::getCategoria)
        .distinct()
        .toList();
  }

  /** {@inheritDoc} */
  @Override
  public List<Servicio> obtenerRelacionados(Long servicioActualId, int limite) {
    if (limite <= 0) {
      return List.of();
    }
    return repository.findByActivoTrueAndIdNotOrderByIdAsc(
        servicioActualId, PageRequest.of(0, limite));
  }

  /** {@inheritDoc} */
  @Override
  public List<Servicio> obtenerRecomendaciones(int limite) {
    if (limite <= 0) {
      return List.of();
    }
    return repository.findByActivoTrueOrderByIdAsc(PageRequest.of(0, limite));
  }

  /** {@inheritDoc} */
  @Override
  public long contarTodos() {
    return repository.count();
  }

  /** {@inheritDoc} */
  @Override
  public long contarActivos() {
    return repository.countByActivoTrue();
  }

  /** {@inheritDoc} */
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
