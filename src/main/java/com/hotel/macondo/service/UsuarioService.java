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

    /** Valida las credenciales y retorna el usuario autenticado. */
    Usuario autenticar(String correo, String contrasena);

    /** Registra un usuario cuando el correo no esta en uso. */
    Usuario registrar(Usuario usuario);

    /** Verifica que el usuario tenga el rol solicitado. */
    boolean autorizar(Usuario usuario, Rol rol);

    /** Elimina un usuario por correo*/
    void eliminar(String correo);
}
