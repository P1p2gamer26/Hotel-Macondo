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

    if (!usuario.iniciarSesion(cliente.getCorreo(), contrasenaActual)
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

    if (clienteService.actualizarInformacion(existente, cliente) == null) {
      return false;
    }
    return usuarioService.actualizarCorreo(correoPrevio, correoNuevo) != null;
  }

  /** {@inheritDoc} */
  @Override
  public Boolean eliminarCuenta(Long id) {
    Cliente cliente = clienteService.buscarPorId(id);
    if (cliente == null) {
      return false;
    }

    usuarioService.eliminar(cliente.getCorreo());
    clienteService.eliminar(id);
    return true;
  }
}
