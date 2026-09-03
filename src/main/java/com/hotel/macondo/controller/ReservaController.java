package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.ReservaService;

/**
 * Expone las reservas privadas de un cliente identificado en la URL.
 */
@Controller
@RequestMapping("/cliente/{id}")
public class ReservaController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ReservaService reservaService;

    /** Muestra las reservas activas y futuras que pertenecen al cliente. */
    @GetMapping("/reservas")
    public String reservasActivas(@PathVariable Integer id, Model model) {
        Cliente cliente = obtenerCliente(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("reservas", reservaService.buscarActivasDeCliente(cliente));
        return "cliente/reservas_activas";
    }

    /** Muestra las reservas finalizadas o canceladas del cliente. */
    @GetMapping("/historial")
    public String historialReservas(@PathVariable Integer id, Model model) {
        Cliente cliente = obtenerCliente(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("historial", reservaService.buscarHistorialDeCliente(cliente));
        return "cliente/historial_reservas";
    }

    /** Evita renderizar vistas privadas para identificadores inexistentes. */
    private Cliente obtenerCliente(Integer id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return cliente;
    }
}
