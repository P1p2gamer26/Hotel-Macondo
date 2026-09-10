package com.hotel.macondo.service;

import java.util.Collection;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.errors.FormularioErroneoException;


@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository repository;

  public UsuarioServiceImpl(UsuarioRepository repository) {
    this.repository = repository;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Usuario> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Usuario buscarPorCorreo(String correo) {
    if (correo == null) {
      return null;
    }
    return repository.findByCorreoIgnoreCase(normalizarCorreo(correo)).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public boolean validarCorreo(String correo) {
    return !repository.existsByCorreoIgnoreCase(normalizarCorreo(correo));
  }

  /** {@inheritDoc} */
  @Override
  public boolean validarContrasena(String contrasena) {
    return contrasena != null && !contrasena.isBlank();
  }

  /** {@inheritDoc} */
  @Override
  public Usuario autenticar(String correo, String contrasena) {
    if (correo == null) {
      throw new FormularioErroneoException("Ingrese un correo valido");
    }
    String correoNormalizado = normalizarCorreo(correo);
    Usuario usuario =
        repository.findByCorreoIgnoreCase(correoNormalizado).orElse(null);
    if (usuario == null
        || usuario.getContrasena() == null
        || !usuario.getContrasena().equals(contrasena)) {
      throw new FormularioErroneoException("Correo o contraseña incorrectos");
    }
    return usuario;
  }

  /** {@inheritDoc} */
  @Override
  public Usuario registrar(Usuario usuario) {
    if (usuario == null || usuario.getCorreo() == null) {
      return null;
    }

    String correoNormalizado = normalizarCorreo(usuario.getCorreo());
    if (repository.existsByCorreoIgnoreCase(correoNormalizado)) {
      return null;
    }

    usuario.setCorreo(correoNormalizado);
    return repository.save(usuario);
  }

  /** {@inheritDoc} */
  @Override
  public Usuario registrarCliente(Cliente cliente, String contrasena) {
    if (cliente == null || cliente.getCorreo() == null || contrasena == null) {
      return null;
    }

    String correoNormalizado = normalizarCorreo(cliente.getCorreo());
    if (repository.existsByCorreoIgnoreCase(correoNormalizado)) {
      return null;
    }

    Usuario usuario =
        new Usuario(correoNormalizado, contrasena, Rol.CLIENTE);
    usuario.asignarCliente(cliente);
    return repository.save(usuario);
  }

  /** {@inheritDoc} */
  @Override
  public Usuario actualizarContrasena(String correo, String nuevaContrasena) {
    if (correo == null || nuevaContrasena == null || nuevaContrasena.isBlank()) {
      return null;
    }

    Usuario usuario =
        repository
            .findByCorreoIgnoreCase(normalizarCorreo(correo))
            .orElse(null);
    if (usuario == null) {
      return null;
    }

    usuario.setContrasena(nuevaContrasena);
    return repository.save(usuario);
  }

  /** {@inheritDoc} */
  @Override
  public Usuario actualizarCorreo(String correoPrevio, String correoNuevo) {
    if (correoPrevio == null || correoNuevo == null) {
      return null;
    }

    Usuario usuario =
        repository
            .findByCorreoIgnoreCase(normalizarCorreo(correoPrevio))
            .orElse(null);
    if (usuario == null) {
      return null;
    }

    usuario.setCorreo(normalizarCorreo(correoNuevo));
    return repository.save(usuario);
  }

  /** {@inheritDoc} */
  @Override
  public boolean autorizar(Usuario usuario, Rol rol) {
    return usuario != null && usuario.getRol() == rol;
  }

  /** {@inheritDoc} */
  @Override
  public void eliminar(String correo) {
    if (correo != null) {
      repository.deleteByCorreoIgnoreCase(normalizarCorreo(correo));
    }
  }

  private String normalizarCorreo(String correo) {
    return correo.trim().toLowerCase(Locale.ROOT);
  }
}
