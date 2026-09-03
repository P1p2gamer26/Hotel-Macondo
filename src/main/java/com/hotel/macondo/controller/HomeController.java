package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.ServicioService;
import com.hotel.macondo.service.TestimonioService;
import com.hotel.macondo.service.TipoHabitacionService;

@Controller
public class HomeController {

    @Autowired
    private HabitacionService habitacionService;
    @Autowired
    private TipoHabitacionService tipoHabitacionService;
    @Autowired
    private ServicioService servicioService;
    @Autowired
    private TestimonioService testimonioService;

    /**
     * Construye la pagina principal con la informacion de sus tres secciones.
     * El buscador de la portada se arma con los tipos de habitacion, no con
     * las habitaciones: lo que el visitante escoge es un tipo.
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("habitaciones", habitacionService.buscarTodas());
        model.addAttribute("tiposHabitacion", tipoHabitacionService.buscarTodos());
        model.addAttribute("servicios", servicioService.obtenerCatalogoActivo());
        model.addAttribute("testimonios", testimonioService.buscarTodos());
        return "index";
    }
}
