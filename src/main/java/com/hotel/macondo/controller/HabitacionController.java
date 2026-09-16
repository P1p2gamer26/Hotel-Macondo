package com.hotel.macondo.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.TipoHabitacionService;

@RequestMapping("/habitaciones")
@Controller
public class HabitacionController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HabitacionService habitacionService;

    /**
     * Muestra todos los tipos de habitacion disponibles para seleccionar.
     */
    @GetMapping
    public String mostrarHabitaciones(
            @RequestParam(required = false) Integer personas,
            @RequestParam(required = false) Long tipoId,
            Model model) {
        Collection<TipoHabitacion> tipos;
        if (tipoId != null) {
            TipoHabitacion tipo = tipoHabitacionService.buscarPorId(tipoId);
            tipos = personas == null || tipo.getCapacidadPersonas() >= personas
                    ? List.of(tipo)
                    : List.of();
        } else if (personas != null) {
            tipos = tipoHabitacionService.buscarPorPersonas(personas);
        } else {
            tipos = tipoHabitacionService.buscarTodos();
        }

        model.addAttribute("tiposHabitacion", tipos);
        model.addAttribute("personas", personas);
        return "habitacion/habitaciones";
    }

    /**
     * Muestra el tipo elegido; la unidad fisica se asigna al confirmar la reserva.
     */
    @GetMapping("/{id}")
    public String mostrarHabitacion(@PathVariable("id") Long id, Model model) {
        TipoHabitacion tipoHabitacion = tipoHabitacionService.buscarPorId(id);
        boolean hayDisponibilidad;
        try {
            habitacionService.buscarDisponiblePorTipo(id);
            hayDisponibilidad = true;
        } catch (RecursoNoEncontradoException e) {
            hayDisponibilidad = false;
        }

        model.addAttribute("tipoHabitacion", tipoHabitacion);
        model.addAttribute("hayDisponibilidad", hayDisponibilidad);
        return "habitacion/detalle_habitacion";
    }
}
