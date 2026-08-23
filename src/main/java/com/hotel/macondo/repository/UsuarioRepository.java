package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.Usuario;

@Repository
public class UsuarioRepository {

    private final Map<String, Usuario> data = new LinkedHashMap<>();

    /**
     * Carga credenciales temporales para cada rol del sistema.
     */
    public UsuarioRepository() {
        save(new Usuario(1, "cliente@macondo.com", "cliente123", Rol.CLIENTE));
        save(new Usuario(2, "operador@macondo.com", "operador123", Rol.OPERADOR));
        save(new Usuario(3, "admin@macondo.com", "admin123", Rol.ADMINISTRADOR));
    }

    /**
     * Retorna una copia de todos los usuarios.
     */
    public Collection<Usuario> findAll() {
        return List.copyOf(data.values());
    }

    /**
     * Busca un usuario por correo sin distinguir mayusculas.
     */
    public Usuario findByCorreo(String correo) {
        return correo == null ? null : data.get(correo.toLowerCase());
    }

    /**
     * Guarda un usuario indexado por su correo.
     */
    public Usuario save(Usuario usuario) {
        data.put(usuario.getCorreo().toLowerCase(), usuario);
        return usuario;
    }
}
