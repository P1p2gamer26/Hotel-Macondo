package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;

/**
 * Gestiona el perfil del cliente indicado en la ruta privada.
 */
@Controller
@RequestMapping("/cliente/{id}")
public class PerfilController {

    @Autowired
    private ClienteService clienteService;

    /** Renderiza el perfil del cliente solicitado. */
    @GetMapping("/perfil")
    public String mostrarPerfil(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", obtenerCliente(id));
        return "cliente/perfil_cliente";
    }

    /** Actualiza solamente los datos personales editables del cliente. */
    @PostMapping("/perfil")
    public String actualizarPerfil(@PathVariable Integer id, Cliente cliente) {
        Cliente existente = obtenerCliente(id);
        existente.actualizarInformacion(
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getCorreo());
        clienteService.guardar(existente);
        return "redirect:/cliente/" + id + "/perfil?exito=true";
    }

    /** Centraliza la validacion del identificador usado por ambas operaciones. */
    private Cliente obtenerCliente(Integer id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return cliente;
    }
}