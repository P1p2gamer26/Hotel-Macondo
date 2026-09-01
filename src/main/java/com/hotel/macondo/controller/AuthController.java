package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Rol;
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
     * Muestra el formulario de registro de usuario en Bootstrap 5.
     */
    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    /**
     * Procesa las credenciales y redirige dinamicamente usando el ID del usuario
     * autenticado.
     */
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(name = "username") String correo,
            @RequestParam(name = "password") String contrasena,
            Model model) {

        Usuario usuario = service.autenticar(correo, contrasena);

        if (usuario == null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        // Redirecciones por rol
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/admin";
        } else if (usuario.getRol() == Rol.OPERADOR) {
            return "redirect:/operador";
        } else if (usuario.getRol() == Rol.CLIENTE) {
            // Redirige al dashboard del cliente con su ID correspondiente
            return "redirect:/cliente/" + usuario.getId();
        }

        return "redirect:/";
    }

}
