package com.hotel.macondo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;

@Service
@Transactional
public class CuentaClienteServiceImpl implements CuentaClienteService {

  @Autowired
  private ClienteService clienteService;
  @Autowired
  private UsuarioService usuarioService;

  /** {@inheritDoc} */
  @Override
  public void crearCuenta(Cliente cliente, String contrasena) {
    String correo = cliente.getCorreo();
    if (!usuarioService.validarCorreo(correo)) {
      throw new FormularioErroneoException("Correo electrónico ya en uso.");
    }
    if (correo == null || correo.isBlank()) {
      throw new FormularioErroneoException("El correo no puede estar vacío.");
    }

    Cliente clienteGuardado = clienteService.guardar(cliente);
    Usuario usuarioGuardado = usuarioService.registrarCliente(clienteGuardado, contrasena);

    if (usuarioGuardado == null) {
      clienteService.eliminar(clienteGuardado.getId());
      throw new FormularioErroneoException(
          "No se pudo crear la cuenta de usuario para el cliente.");
    }
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

    Usuario usuarioActualizado = usuarioService.actualizarCorreo(correoPrevio, correoNuevo);
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

    try {
      clienteService.eliminar(id);
      return true;
    } catch (PeticionImposible e) {
      // Si tiene reservas asociadas no se permite eliminar y se retorna false
      return false;
    }
  }
}
