package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cliente;

public interface CuentaClienteService {

    /**
     * Crea una cuenta de cliente y usuario en la base de datos.
     * 
     * @throws FormularioErroneoException si el correo ya está en uso o es inválido o si hubo un error al crear el cliente.
     */
    void crearCuenta(Cliente cliente, String contrasena);

    // Actualiza la contraseña del usuario asociado al cliente con el id proporcionado.
    Boolean actualizarContrasena(Long id, String contrasenaActual, String nuevaContrasena, String confirmarContrasena);

    // Actualiza el perfil del cliente con el id proporcionado.
    Boolean actualizarPerfil(Long id, Cliente cliente);

    // Elimina la cuenta del cliente con el id proporcionado.
    Boolean eliminarCuenta(Long id);
}
