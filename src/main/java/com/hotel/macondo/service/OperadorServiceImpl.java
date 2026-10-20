package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.OperadorRepository;
import com.hotel.macondo.repository.UsuarioRepository;

@Service
@Transactional
public class OperadorServiceImpl implements OperadorService {

  @Autowired
  private OperadorRepository repository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  // ==========================================
  // CONSULTAS DE OPERADORES
  // ==========================================

  // Retorna todos los operadores registrados ordenados por identificador
  @Override
  public Collection<Operador> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  // Busca un operador por su identificador unico
  @Override
  public Operador buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  // ==========================================
  // GESTIÓN Y MANTENIMIENTO (ADMIN)
  // ==========================================

  // Guarda o actualiza los datos de un operador
  @Override
  public Operador guardar(Operador operador) {
    return repository.save(operador);
  }

  // Alterna el estado activo o inactivo del operador
  @Override
  public Operador cambiarEstado(Long id) {
    Operador operador = repository.findById(id).orElse(null);
    if (operador == null) {
      return null;
    }
    operador.setActivo(!Boolean.TRUE.equals(operador.getActivo()));
    return repository.save(operador);
  }

  // Elimina un operador y desvincula sus credenciales de usuario
  // en la misma transaccion para evitar registros huerfanos
  @Override
  public void eliminar(Long id) {
    Operador operador = repository.findById(id).orElse(null);
    if (operador == null) {
      return;
    }

    Usuario usuario = operador.getUsuario();
    if (usuario != null) {
      operador.setUsuario(null);
      usuarioRepository.delete(usuario);
    }

    repository.delete(operador);
  }
}