package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;

public interface CuentaClienteService {

    Boolean crearCuenta(Cliente cliente, String contrasena);

    Boolean actualizarContrasena(Integer id, String contrasenaActual, String nuevaContrasena, String confirmarContrasena);

    Boolean actualizarPerfil(Integer id, Cliente cliente);

    Boolean eliminarCuenta(Integer id);
}
