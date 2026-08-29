package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.UsuarioService;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService service;

    /**
     * Muestra el formulario de inicio de sesion.
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    /**
     * Valida las credenciales mediante Usuario y expone su rol a la vista.
     */
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo,
            @RequestParam String contrasena, Model model) {
        Usuario usuario = service.autenticar(correo, contrasena);
        if (usuario == null) {
            model.addAttribute("error", "Correo o contrasena incorrectos");
            return "login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("rol", usuario.getRol());
        return "bienvenida";
    }
}
