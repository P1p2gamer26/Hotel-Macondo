package com.hotel.macondo.controller.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthRestController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        if (correo == null || contrasena == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan credenciales");
        }

        try {
            Usuario usuario = usuarioService.autenticar(correo, contrasena);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", usuario.getId());
            respuesta.put("correo", usuario.getCorreo());
            respuesta.put("rol", usuario.getRol().name());

            // Si es un cliente se tiene que dar el ID real del cliente asociado
            if (usuario.getCliente() != null) {
                respuesta.put("clienteId", usuario.getCliente().getId());
            }

            return respuesta;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
    }
}