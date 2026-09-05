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
        Usuario administrador = new Usuario("admin@macondo.com", "admin123", Rol.ADMINISTRADOR);
        administrador.setId(99L);
        save(administrador);

        // --- OPERADORES (Vinculado con Operador id: 1) ---
        Usuario operador = new Usuario("operador@macondo.com", "operador123", Rol.OPERADOR);
        operador.setId(1L);
        save(operador);

        // --- CLIENTES (Vinculados con los IDs reales de ClienteRepository) ---
        Usuario ana = new Usuario("ana@macondo.com", "ana123", Rol.CLIENTE);
        ana.setId(1L);
        save(ana);
        Usuario luis = new Usuario("luis@macondo.com", "luis123", Rol.CLIENTE);
        luis.setId(2L);
        save(luis);
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
