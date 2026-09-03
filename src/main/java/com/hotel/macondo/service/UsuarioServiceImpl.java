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
    public boolean validarCorreo(String correo){
        return correo != null && correo.trim().isBlank() == false && repository.findByCorreo(correo) == null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validarContrasena(String contrasena){
        return contrasena != null && contrasena.isBlank() == false;
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
    public Usuario registrarCliente(Cliente cliente, String contraseña) {
        if (cliente == null || cliente.getCorreo() == null || contraseña == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(cliente.getCorreo());
        usuario.setContrasena(contraseña);
        usuario.setRol(Rol.CLIENTE);
        
        return registrar(usuario);
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
    public Usuario actualizarCorreo(String correoPrevio, String correoNuevo){
        if(correoNuevo == null || correoPrevio == null){
            return null;
        }

        Usuario usuario = repository.findByCorreo(correoPrevio);
        if(usuario == null) return null;

        usuario.setCorreo(correoNuevo);
        // Se elimina el usuario previo ya que como el correo es la key del map si solo lo 
        // actualizamos entonces duplicaremos la informacion
        repository.delete(correoPrevio);
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
