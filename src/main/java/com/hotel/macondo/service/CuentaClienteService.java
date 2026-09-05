package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;

public interface CuentaClienteService {

    Boolean crearCuenta(Cliente cliente, String contrasena);

    Boolean actualizarContrasena(Long id, String contrasenaActual, String nuevaContrasena, String confirmarContrasena);

    Boolean actualizarPerfil(Long id, Cliente cliente);

    Boolean eliminarCuenta(Long id);
}
