package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaClienteServiceImpl implements CuentaClienteService {

  private final ClienteService clienteService;
  private final UsuarioService usuarioService;

  public CuentaClienteServiceImpl(
      ClienteService clienteService, UsuarioService usuarioService) {
    this.clienteService = clienteService;
    this.usuarioService = usuarioService;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean crearCuenta(Cliente cliente, String contrasena) {
    String correo = cliente.getCorreo();
    if (!usuarioService.validarCorreo(correo)) {
      return false;
    }

    Cliente clienteGuardado = clienteService.guardar(cliente);
    Usuario usuarioGuardado =
        usuarioService.registrarCliente(clienteGuardado, contrasena);

    if (usuarioGuardado == null) {
      clienteService.eliminar(clienteGuardado.getId());
      return false;
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean actualizarContrasena(
      Long id,
      String contrasenaActual,
      String nuevaContrasena,
      String confirmarContrasena) {
    Cliente cliente = clienteService.buscarPorId(id);
    if (cliente == null) {
      return false;
    }

    Usuario usuario = usuarioService.buscarPorCorreo(cliente.getCorreo());
    if (usuario == null) {
      return false;
    }

    if (usuarioService.autenticar(cliente.getCorreo(), contrasenaActual) == null
        || !usuarioService.validarContrasena(nuevaContrasena)
        || !nuevaContrasena.equals(confirmarContrasena)) {
      return false;
    }

    usuarioService.actualizarContrasena(cliente.getCorreo(), nuevaContrasena);
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean actualizarPerfil(Long id, Cliente cliente) {
    Cliente existente = clienteService.buscarPorId(id);
    if (existente == null
        || cliente == null
        || cliente.getCorreo() == null
        || cliente.getCorreo().isBlank()) {
      return false;
    }

    String correoPrevio = existente.getCorreo();
    String correoNuevo = cliente.getCorreo();
    if (!correoNuevo.equalsIgnoreCase(correoPrevio)
        && !usuarioService.validarCorreo(correoNuevo)) {
      return false;
    }

    Usuario usuarioActualizado =
        usuarioService.actualizarCorreo(correoPrevio, correoNuevo);
    if (usuarioActualizado == null) {
      return false;
    }

    existente.setNombre(cliente.getNombre());
    existente.setApellido(cliente.getApellido());
    existente.setTelefono(cliente.getTelefono());
    existente.setCorreo(correoNuevo);
    return clienteService.guardar(existente) != null;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean eliminarCuenta(Long id) {
    Cliente cliente = clienteService.buscarPorId(id);
    if (cliente == null) {
      return false;
    }

    // Borrar el usuario ya es parte de eliminar el cliente: la cascada vive en
    // ClienteService y aqui solo se orquesta.
    clienteService.eliminar(id);
    return true;
  }
}
