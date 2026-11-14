package com.hotel.macondo.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthRestController {

    @Autowired
    private UsuarioService usuarioService;

    // ==========================================
    // AUTENTICACIÓN / SESIÓN
    // ==========================================

    // Valida credenciales de acceso para cualquier rol (Cliente, Operador, Admin)
    // http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        if (correo == null || contrasena == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan credenciales");
        }

        try {
            UserEntity usuario = usuarioService.autenticar(correo, contrasena);

            List<String> nombresRoles = usuario.getRoles().stream()
                    .map(Rol::getName)
                    .toList();

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", usuario.getId());
            respuesta.put("correo", usuario.getCorreo());
            respuesta.put("roles", nombresRoles);
            respuesta.put("rol", nombresRoles.isEmpty() ? null : nombresRoles.get(0));

            // Se envía el perfil básico. Cliente ignora usuario y reservas para evitar
            // ciclos en JSON
            if (usuario.getCliente() != null) {
                respuesta.put("cliente", usuario.getCliente());
            }

            return respuesta;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
    }
}