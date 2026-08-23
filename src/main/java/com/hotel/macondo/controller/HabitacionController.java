package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.service.HabitacionService;

@RequestMapping("/habitaciones")
@Controller
public class HabitacionController {

    private final HabitacionService service;

    /**
     * Conecta el controlador con la interfaz de habitaciones.
     */
    @Autowired
    public HabitacionController(HabitacionService service) {
        this.service = service;
    }

    /**
     * Muestra todas las habitaciones.
     */
    @GetMapping
    public String mostrarHabitaciones(Model model) {
        model.addAttribute("habitaciones", service.buscarTodas());
        return "habitaciones";
    }

    /**
     * Filtra habitaciones por capacidad.
     */
    @GetMapping(params = "personas")
    public String buscarPorPersonas(@RequestParam int personas, Model model) {
        model.addAttribute("habitaciones", service.buscarPorPersonas(personas));
        model.addAttribute("personas", personas);
        return "habitaciones";
    }

    /**
     * Muestra el detalle de una habitacion o vuelve al listado si no existe.
     */
    @GetMapping("/{id}")
    public String mostrarHabitacion(@PathVariable("id") Integer id, Model model) {
        Habitacion habitacion = service.buscarPorId(id);
        if (habitacion == null) {
            return "redirect:/habitaciones";
        }

        model.addAttribute("habitacion", habitacion);
        return "detalle_habitacion";
    }
}
