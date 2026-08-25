package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;

@Controller
@RequestMapping("/cliente")
public class PerfilController {

    private final ClienteService clienteService;

    @Autowired
    public PerfilController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // http://localhost:8080/cliente/perfil
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        Cliente cliente = clienteService.buscarPorId(1);
        model.addAttribute("cliente", cliente);
        return "cliente/perfil_cliente";
    }

    // http://localhost:8080/cliente/perfil/actualizar
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(Cliente cliente) {
        // Buscar el cliente existente para mantener su ID y reservas
        Cliente existente = clienteService.buscarPorId(cliente.getId());
        if (existente != null) {
            existente.actualizarInformacion(
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getTelefono(),
                    cliente.getCorreo());
            clienteService.guardar(existente);
        }
        return "redirect:/cliente/perfil?exito=true";
    }
}