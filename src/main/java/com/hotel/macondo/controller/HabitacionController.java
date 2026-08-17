package com.hotel.macondo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.service.HotelService;

@RequestMapping("/habitaciones")
@Controller
public class HabitacionController {

    @Autowired
    HotelService service;

    // http://localhost:8080/habitaciones
    @GetMapping()
    public String mostrarHabitaciones(Model model) {
        model.addAttribute("habitaciones", service.buscarHabitaciones());

        return "habitaciones";
    }

    // http://localhost:8080/habitaciones?personas=4
    @GetMapping(params = "personas")
    public String buscarPorPersonas(@RequestParam int personas, Model model) {
        model.addAttribute("habitaciones", service.buscarHabitacionesPorPersonas(personas));
        model.addAttribute("personas", personas);

        return "habitaciones";
    }

    // http://localhost:8080/habitaciones/1
    @GetMapping("/{id}")
    public String mostrarHabitacion(@PathVariable("id") Integer id, Model model) {
        Habitacion habitacion = service.buscarHabitacionPorId(id);

        // Si el id no existe volvemos al listado
        if (habitacion == null) {
            return "redirect:/habitaciones";
        }

        model.addAttribute("habitacion", habitacion);

        return "detalle_habitacion";
    }

}
