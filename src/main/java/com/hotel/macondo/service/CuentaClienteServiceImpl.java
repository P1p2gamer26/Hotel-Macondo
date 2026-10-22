package com.hotel.macondo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.repository.ClienteRepository;

@Service
@Transactional
public class CuentaClienteServiceImpl implements CuentaClienteService {

  @Autowired
  private ClienteService clienteService;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private ClienteRepository clienteRepository;

  // ==========================================
  // REGISTRO Y CREACIÓN DE CUENTAS
  // ==========================================

  /**
   * Registra un nuevo cliente y genera sus credenciales de usuario
   * Valida unicidad de correo y cedula antes de persistir para evitar
   * fallos a nivel de restriccion foranea en base de datos
   */
  @Override
  public void crearCuenta(Cliente cliente, String contrasena) {
    String correo = cliente.getCorreo();
    if (!usuarioService.validarCorreo(correo)) {
      throw new FormularioErroneoException("Correo electrónico ya en uso.");
    }
    if (correo == null || correo.isBlank()) {
      throw new FormularioErroneoException("El correo no puede estar vacío.");
    }

    if (clienteRepository.findByCedula(cliente.getCedula()).isPresent()) {
      throw new FormularioErroneoException("Ya existe un cliente con esa cédula.");
    }

    Cliente clienteGuardado = clienteService.guardar(cliente);
    Usuario usuarioGuardado = usuarioService.registrarCliente(clienteGuardado, contrasena);

    if (usuarioGuardado == null) {
      clienteService.eliminar(clienteGuardado.getId());
      throw new FormularioErroneoException(
          "No se pudo crear la cuenta de usuario para el cliente.");
    }
  }

  // ==========================================
  // PERFIL Y CONTRASEÑA
  // ==========================================

  // Valida la clave actual del cliente y asigna la nueva clave si coincide
  // con la confirmacion y cumple con las restricciones requeridas
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

  // Actualiza datos personales del cliente y sincroniza el correo en usuario
  // verificando previamente que el nuevo correo no se encuentre tomado
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

  // ==========================================
  // BAJA DE CUENTA
  // ==========================================

  // Da de baja la cuenta del cliente
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
      return false;
    }
  }
}