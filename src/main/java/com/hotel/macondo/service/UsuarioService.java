package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;

public interface UsuarioService {

    /** Retorna todos los usuarios registrados. */
    Collection<Usuario> buscarTodos();

    /** Busca un usuario por correo. */
    Usuario buscarPorCorreo(String correo);

    /** Valida que un correo se pueda usar. */
    boolean validarCorreo(String correo);

    /** Valida que una contraseña se pueda usar. */
    boolean validarContrasena(String contrasena);

    /** Valida las credenciales y retorna el usuario autenticado. */
    Usuario autenticar(String correo, String contrasena);

    /** Registra un usuario cuando el correo no esta en uso. */
    Usuario registrar(Usuario usuario);

    /** Registrar a un usuario con el rol de cliente. */
    Usuario registrarCliente(Cliente cliente, String contraseña);

    /** Actualiza la contraseña asociada al correo indicado. */
    Usuario actualizarContrasena(String correo, String nuevaContrasena);

    /** Actualiza el correo tomando el correo previo */
    Usuario actualizarCorreo(String correoPrevio, String correoNuevo);

    /** Verifica que el usuario tenga el rol solicitado. */
    boolean autorizar(Usuario usuario, Rol rol);

    /** Elimina un usuario por correo*/
    void eliminar(String correo);
}
