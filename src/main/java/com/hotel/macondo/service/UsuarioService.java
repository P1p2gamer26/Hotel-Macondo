package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.errors.FormularioErroneoException;

public interface UsuarioService {

    /** Busca un usuario por correo. */
    UserEntity buscarPorCorreo(String correo);

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
    UserEntity autenticar(String correo, String contrasena);

    /** Registrar a un usuario con el rol de cliente. */
    UserEntity registrarCliente(Cliente cliente, String contraseña);

    /** Actualiza la contraseña asociada al correo indicado. */
    UserEntity actualizarContrasena(String correo, String nuevaContrasena);

    /** Actualiza el correo tomando el correo previo */
    UserEntity actualizarCorreo(String correoPrevio, String correoNuevo);

}
