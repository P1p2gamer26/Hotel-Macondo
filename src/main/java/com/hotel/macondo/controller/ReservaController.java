package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.service.ReservaService;

@Controller
@RequestMapping("/cliente/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/activas")
    public String reservasActivas(Model model) {
        model.addAttribute("reservas", reservaService.obtenerReservasActivas());
        return "reservas-activas";
    }

    @GetMapping("/historial")
    public String historialReservas(Model model) {
        model.addAttribute("historial", reservaService.obtenerHistorial());
        return "historial-reservas";
    }
}