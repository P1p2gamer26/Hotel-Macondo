package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    /**
     * Crea el servicio con su repositorio de usuarios.
     */
    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Usuario> buscarTodos(){
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Usuario buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo);
    }

    /** {@inheritDoc} */
    @Override
    public Usuario autenticar(String correo, String contrasena) {
        Usuario usuario = repository.findByCorreo(correo);
        return usuario != null && usuario.iniciarSesion(correo, contrasena)
                ? usuario
                : null;
    }

    /** {@inheritDoc} */
    @Override
    public Usuario registrar(Usuario usuario) {
        if (usuario == null || usuario.getCorreo() == null
                || repository.findByCorreo(usuario.getCorreo()) != null) {
            return null;
        }
        return repository.save(usuario);
    }

    /** {@inheritDoc} */
    @Override
    public Usuario actualizarContrasena(String correo, String nuevaContrasena) {
        if (correo == null || nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return null;
        }

        Usuario usuario = repository.findByCorreo(correo);
        if (usuario == null) {
            return null;
        }

        usuario.setContrasena(nuevaContrasena);
        return repository.save(usuario);
    }

    /** {@inheritDoc} */
    @Override
    public boolean autorizar(Usuario usuario, Rol rol) {
        return usuario != null && usuario.tieneRol(rol);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(String correo){
        repository.delete(correo);
    }
}
