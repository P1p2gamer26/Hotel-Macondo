package com.hotel.macondo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * ============================================================
 * AQUI VA TODO LO DE INICIAR SESION Y REGISTRARSE
 * ============================================================
 *
 * Este controlador se encarga de la autenticacion.
 *
 * Cuando se implemente, aqui van:
 *
 *   GET  /login     -> mostrar el formulario de inicio de sesion
 *   POST /login     -> validar correo y clave contra UsuarioService
 *   GET  /registro  -> mostrar el formulario de registro
 *   POST /registro  -> crear el usuario (validando correo repetido y clave)
 *   GET  /logout    -> cerrar la sesion
 *
 * Debe usar UsuarioService (nunca el repositorio directo, para no saltarse
 * capas) y devolver las vistas login.html, registro.html y bienvenida.html.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
}
