package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotel.macondo.service.HotelService;

@Controller
public class HomeController {

    // Conexión con el servicio, NO SALTARSE CAPAS
    @Autowired
    HotelService service;

    // http://localhost:8080/
    // La landing arma sus tres secciones dinámicas (habitaciones, servicios y
    // testimonios) desde el modelo, no con HTML quemado, para que agregar una
    // habitación o un servicio sea solo tocar el repositorio.
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("habitaciones", service.buscarHabitaciones());
        model.addAttribute("servicios", service.buscarServicios());
        model.addAttribute("testimonios", service.buscarTestimonios());

        return "index";
    }

}
