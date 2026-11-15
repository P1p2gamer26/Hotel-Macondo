package com.hotel.macondo.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.repository.UsuarioRepository;

// Servicio de autenticacion, credenciales y sincronizacion de usuarios
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

  @Autowired
  private UsuarioRepository repository;

  // ==========================================
  // AUTENTICACION Y CONSULTAS
  // ==========================================

  // Valida credenciales contra la base de datos comparando correos normalizados
  @Override
  public Usuario autenticar(String correo, String contrasena) {
    if (correo == null) {
      throw new FormularioErroneoException("Ingrese un correo valido");
    }
    String correoNormalizado = normalizarCorreo(correo);
    Usuario usuario = repository.findByCorreoIgnoreCase(correoNormalizado).orElse(null);
    if (usuario == null
        || usuario.getContrasena() == null
        || !usuario.getContrasena().equals(contrasena)) {
      throw new FormularioErroneoException("Correo o contraseña incorrectos");
    }
    return usuario;
  }

  // Busca un usuario por su direccion de correo electronico
  @Override
  public Usuario buscarPorCorreo(String correo) {
    if (correo == null) {
      return null;
    }
    return repository.findByCorreoIgnoreCase(normalizarCorreo(correo)).orElse(null);
  }

  // Comprueba que el correo electronico no este registrado previamente
  @Override
  public boolean validarCorreo(String correo) {
    return !repository.existsByCorreoIgnoreCase(normalizarCorreo(correo));
  }

  // Valida que la contrasena cumpla con no ser nula ni estar en blanco
  @Override
  public boolean validarContrasena(String contrasena) {
    return contrasena != null && !contrasena.isBlank();
  }

  // ==========================================
  // GESTION DE ACCESOS Y CREDENCIALES
  // ==========================================

  // Crea y asocia un usuario con rol CLIENTE a la entidad cliente persistida
  @Override
  public Usuario registrarCliente(Cliente cliente, String contrasena) {
    if (cliente == null || cliente.getCorreo() == null || contrasena == null) {
      return null;
    }

    String correoNormalizado = normalizarCorreo(cliente.getCorreo());
    if (repository.existsByCorreoIgnoreCase(correoNormalizado)) {
      return null;
    }

    Usuario usuario = new Usuario(correoNormalizado, contrasena, Rol.CLIENTE);
    usuario.asignarCliente(cliente);
    return repository.save(usuario);
  }

  // Actualiza la clave de acceso para el correo especificado
  @Override
  public Usuario actualizarContrasena(String correo, String nuevaContrasena) {
    if (correo == null || nuevaContrasena == null || nuevaContrasena.isBlank()) {
      return null;
    }

    Usuario usuario = repository
        .findByCorreoIgnoreCase(normalizarCorreo(correo))
        .orElse(null);
    if (usuario == null) {
      return null;
    }

    usuario.setContrasena(nuevaContrasena);
    return repository.save(usuario);
  }

  // Sincroniza el correo de acceso cuando el cliente actualiza su perfil
  @Override
  public Usuario actualizarCorreo(String correoPrevio, String correoNuevo) {
    if (correoPrevio == null || correoNuevo == null) {
      return null;
    }

    Usuario usuario = repository
        .findByCorreoIgnoreCase(normalizarCorreo(correoPrevio))
        .orElse(null);
    if (usuario == null) {
      return null;
    }

    usuario.setCorreo(normalizarCorreo(correoNuevo));
    return repository.save(usuario);
  }

  // ==========================================
  // METODOS AUXILIARES
  // ==========================================

  // Estandariza la cadena eliminando espacios y transformando a minusculas
  private String normalizarCorreo(String correo) {
    return correo.trim().toLowerCase(Locale.ROOT);
  }
}