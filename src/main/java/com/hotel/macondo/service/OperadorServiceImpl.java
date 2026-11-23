package com.hotel.macondo.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.DTOs.Entrada.OperadorCuentaSolicitud;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.repository.OperadorRepository;
import com.hotel.macondo.repository.RolRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.utils.EmailUtils;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@Service
@Transactional
public class OperadorServiceImpl implements OperadorService {

  private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+$");

  @Autowired
  private OperadorRepository repository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private RolRepository rolRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

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
    if (operador.getUsuario() == null) {
      // Compatibilidad con clientes que aún solo mandan un nombre.
      String correoGenerado = "operador." + UUID.randomUUID() + "@macondo.com";
      operador.asignarUsuario(guardarUsuario(correoGenerado, UUID.randomUUID().toString()));
    }
    return repository.save(operador);
  }

  @Override
  public Operador crearCuenta(OperadorCuentaSolicitud solicitud) {
    String correo = EmailUtils.normalizar(solicitud.correo());
    String nombre = solicitud.nombre() == null ? "" : solicitud.nombre().trim();
    String contrasena = solicitud.contrasena();
    if (!correoValido(correo)) {
      throw new FormularioErroneoException("Escribe un correo electrónico válido.");
    }
    if (nombre.isBlank() || nombre.length() > 100) {
      throw new FormularioErroneoException("Escribe un nombre de hasta 100 caracteres.");
    }
    if (contrasena == null || contrasena.isBlank() || contrasena.codePointCount(0, contrasena.length()) < 12
        || contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
      throw new FormularioErroneoException("La contraseña debe tener al menos 12 caracteres y como máximo 72 bytes.");
    }
    if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
      throw new FormularioErroneoException("Ya existe una cuenta con ese correo.");
    }

    Operador operador = new Operador(nombre, true);
    operador.asignarUsuario(guardarUsuario(correo, contrasena));
    return repository.save(operador);
  }

  private UserEntity guardarUsuario(String correo, String contrasena) {
    Rol rolOperador = rolRepository.findByName("OPERADOR")
        .orElseGet(() -> rolRepository.save(new Rol("OPERADOR")));
    UserEntity usuario = UserEntity.builder()
        .correo(correo)
        .contrasena(passwordEncoder.encode(contrasena))
        .roles(new ArrayList<>(List.of(rolOperador)))
        .build();
    try {
      return usuarioRepository.saveAndFlush(usuario);
    } catch (DataIntegrityViolationException ex) {
      throw new FormularioErroneoException("El correo ya está asignado a una cuenta.");
    }
  }

  private boolean correoValido(String correo) {
    if (correo == null || correo.length() > 255 || !CORREO.matcher(correo).matches()) return false;
    try {
      InternetAddress direccion = new InternetAddress(correo, true);
      direccion.validate();
      return correo.equals(direccion.getAddress());
    } catch (AddressException ex) {
      return false;
    }
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

    UserEntity usuario = operador.getUsuario();
    if (usuario != null) {
      usuario.getRoles().clear();
      operador.setUsuario(null);
      usuarioRepository.delete(usuario);
    }

    repository.delete(operador);
  }
}
