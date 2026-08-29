package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.ServicioService;
import com.hotel.macondo.service.TestimonioService;

@Controller
public class HomeController {

    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private TestimonioService testimonioService;

    /**
     * Construye la pagina principal con la informacion de sus tres secciones.
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("habitaciones", habitacionService.buscarTodas());
        model.addAttribute("servicios", servicioService.buscarTodos());
        model.addAttribute("testimonios", testimonioService.buscarTodos());
        return "index";
    }
}
