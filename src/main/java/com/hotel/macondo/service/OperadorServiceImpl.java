package com.hotel.macondo.service;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.OperadorRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OperadorServiceImpl implements OperadorService {

  @Autowired

  private OperadorRepository repository;
  private UsuarioRepository usuarioRepository;

  /** {@inheritDoc} */
  @Override
  public Collection<Operador> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Operador buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Operador guardar(Operador operador) {
    return repository.save(operador);
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
  public Operador cambiarEstado(Long id) {
    Operador operador = repository.findById(id).orElse(null);
    if (operador == null) {
      return null;
    }
    operador.setActivo(!Boolean.TRUE.equals(operador.getActivo()));
    return repository.save(operador);
  }

  /** {@inheritDoc} */
  @Override
  public void eliminar(Long id) {
    Operador operador = repository.findById(id).orElse(null);
    if (operador == null) {
      return;
    }

    // se desvincula y elimina el usuario asociado para evitar registros huerfanos
    // en usuario
    Usuario usuario = operador.getUsuario();
    if (usuario != null) {
      operador.setUsuario(null);
      usuarioRepository.delete(usuario);
    }

    repository.delete(operador);
  }
}
