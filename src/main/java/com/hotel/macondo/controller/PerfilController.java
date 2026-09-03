package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;

/**
 * Gestiona el perfil del cliente indicado en la ruta privada.
 */
@Controller
@RequestMapping("/cliente/{id}")
public class PerfilController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaClienteService cuentaClienteService;

    /** Renderiza el perfil del cliente solicitado. */
    @GetMapping("/perfil")
    public String mostrarPerfil(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        return "cliente/perfil_cliente";
    }

    /** Actualiza solamente los datos personales editables del cliente. */
    @PostMapping("/perfil")
    public String actualizarPerfil(@PathVariable Integer id, Cliente cliente, Model model) {
        
        cuentaClienteService.actualizarPerfil(id, cliente);

        return "redirect:/cliente/" + id + "/perfil";
    }

    /** Actualiza la contraseña del usuario asociado al cliente. */
    @PostMapping("/password")
    public String actualizarPassword(@PathVariable Integer id,
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("nuevaContrasena") String nuevaContrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        cuentaClienteService.actualizarContrasena(id, contrasenaActual, nuevaContrasena, confirmarContrasena);
        
        return "redirect:/cliente/" + id + "/perfil";
    }

}