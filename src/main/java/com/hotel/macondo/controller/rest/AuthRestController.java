package com.hotel.macondo.controller.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.DTOs.Mapper.ClienteMapper;
import com.hotel.macondo.DTOs.Salida.ClienteDTO;
import com.hotel.macondo.DTOs.Salida.LoginResponseDTO;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.service.UsuarioService;

// Controlador REST para la autenticación y control de acceso.

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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody Map<String, String> credenciales) {
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

            String rolPrincipal = nombresRoles.isEmpty() ? null : nombresRoles.get(0);

            // Se envía el perfil básico convertido a DTO mediante MapStruct
            ClienteDTO clienteDTO = usuario.getCliente() != null
                    ? ClienteMapper.INSTANCE.convert(usuario.getCliente())
                    : null;

            LoginResponseDTO respuesta = new LoginResponseDTO(
                    usuario.getId(),
                    usuario.getCorreo(),
                    rolPrincipal,
                    nombresRoles,
                    clienteDTO);

            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }
    }
}