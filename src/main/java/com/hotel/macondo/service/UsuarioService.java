package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.errors.FormularioErroneoException;

public interface UsuarioService {

    /** Busca un usuario por correo. */
    Usuario buscarPorCorreo(String correo);

    /** Valida que un correo se pueda usar. */
    boolean validarCorreo(String correo);

    /** Valida que una contraseña se pueda usar. */
    boolean validarContrasena(String contrasena);

    /**
     * Valida las credenciales y retorna el usuario autenticado.
     *
     * @throws FormularioErroneoException si el correo o la contraseña no son
     *                                    validos
     */
    Usuario autenticar(String correo, String contrasena);

    /** Registrar a un usuario con el rol de cliente. */
    Usuario registrarCliente(Cliente cliente, String contraseña);

    /** Actualiza la contraseña asociada al correo indicado. */
    Usuario actualizarContrasena(String correo, String nuevaContrasena);

    /** Actualiza el correo tomando el correo previo */
    Usuario actualizarCorreo(String correoPrevio, String correoNuevo);

}
