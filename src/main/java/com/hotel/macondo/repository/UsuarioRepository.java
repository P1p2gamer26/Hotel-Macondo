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
     * Carga credenciales vinculadas directamente con los IDs de Cliente y Operador
     */
    public UsuarioRepository() {
        // --- ADMINISTRADORES (No tienen entidad propia, se manejan como Usuario) ---
        save(new Usuario(99, "admin@macondo.com", "admin123", Rol.ADMINISTRADOR));

        // --- OPERADORES (Vinculado con Operador id: 1) ---
        save(new Usuario(1, "operador@macondo.com", "operador123", Rol.OPERADOR));

        // --- CLIENTES (Vinculados con los IDs reales de ClienteRepository) ---
        save(new Usuario(1, "ana@macondo.com", "ana123", Rol.CLIENTE));
        save(new Usuario(2, "luis@macondo.com", "luis123", Rol.CLIENTE));
    }

    public Collection<Usuario> findAll() {
        return List.copyOf(data.values());
    }

    public Usuario findByCorreo(String correo) {
        return correo == null ? null : data.get(correo.toLowerCase());
    }

    public Usuario save(Usuario usuario) {
        if (usuario != null && usuario.getCorreo() != null) {
            data.put(usuario.getCorreo().toLowerCase(), usuario);
        }
        return usuario;
    }

    public void delete(String correo){
        data.remove(correo);
    }

    public Usuario updatePassword(String correo, String nuevaContrasena) {
        Usuario usuario = findByCorreo(correo);
        if (usuario != null && nuevaContrasena != null && !nuevaContrasena.isBlank()) {
            usuario.setContrasena(nuevaContrasena);
            return save(usuario);
        }
        return null;
    }
}