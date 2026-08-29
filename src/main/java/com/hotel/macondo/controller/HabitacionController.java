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

    @Autowired
    private HabitacionService service;

    /**
     * Muestra todas las habitaciones.
     */
    @GetMapping
    public String mostrarHabitaciones(Model model) {
        // Se entrega el catalogo completo a la vista publica de habitaciones.
        model.addAttribute("habitaciones", service.buscarTodas());
        return "habitacion/habitaciones";
    }

    /**
     * Filtra habitaciones por capacidad.
     */
    @GetMapping(params = "personas")
    public String buscarPorPersonas(@RequestParam int personas, Model model) {
        // El servicio aplica la capacidad minima solicitada antes de renderizar la
        // vista.
        model.addAttribute("habitaciones", service.buscarPorPersonas(personas));
        model.addAttribute("personas", personas);
        return "habitacion/habitaciones";
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

        // La ficha recibe una unica habitacion validada por identificador.
        model.addAttribute("habitacion", habitacion);
        return "habitacion/detalle_habitacion";
    }
}
