package com.hotel.macondo.controller.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.HabitacionService;

// Controlador REST para la gestión de habitaciones físicas del hotel
@RestController
@RequestMapping("/api/habitacion")
@CrossOrigin(origins = "http://localhost:4200")
public class HabitacionRestController {

    @Autowired
    private HabitacionService habitacionService;

    // ==========================================
    // CONSULTAS Y ASIGNACION
    // ==========================================

    // Retorna la totalidad de habitaciones del hotel
    @GetMapping
    public List<Habitacion> buscarTodas() {
        Collection<Habitacion> habitaciones = habitacionService.buscarTodas();
        return new ArrayList<>(habitaciones);
    }

    // Busca una habitación por su identificador
    @GetMapping("/find/{id}")
    public Habitacion buscarPorId(@PathVariable Long id) {
        return habitacionService.buscarPorId(id);
    }

    // Busca la habitación asignada a una reserva en particular
    // Retorna null si la reserva aún no tiene una habitación vinculada
    @GetMapping("/reserva/{reservaId}")
    public Habitacion buscarPorReserva(@PathVariable Long reservaId) {
        return habitacionService.buscarPorReserva(reservaId);
    }

    // ==========================================
    // ADMINISTRACIÓN Y MANTENIMIENTO
    // ==========================================

    // Crea una nueva habitación validando su categoría asociada
    @PostMapping
    public Habitacion crear(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        return habitacionService.guardar(habitacion, idTipo);
    }

    // Actualiza los datos de una habitación existente
    @PutMapping
    public Habitacion actualizar(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        return habitacionService.guardar(habitacion, idTipo);
    }

    // Cambia el estado operativo de la habitación entre disponible y no disponible
    @PutMapping("/estado/{id}")
    public Habitacion cambiarEstado(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.cambiarEstado(id);
        if (habitacion == null) {
            throw new RecursoNoEncontradoException("No se encontro habitacion con id " + id);
        }
        return habitacion;
    }

    // Elimina una habitación del sistema si NO TIENE reservas asociadas
    // Lanza PeticionImposible si cuenta con reservas históricas o activas
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        if (!habitacionService.reservasAsociadas(id).isEmpty()) {
            throw new PeticionImposible(
                    "No se puede eliminar la habitación porque tiene reservas asociadas.");
        }
        habitacionService.eliminar(id);
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    // Valida que la habitación contenga la referencia requerida a su tipo
    private void validarTipoHabitacion(Habitacion habitacion) {
        if (habitacion.getTipoHabitacion() == null || habitacion.getTipoHabitacion().getId() == null) {
            throw new FormularioErroneoException("Debes seleccionar un tipo de habitación.");
        }
    }
}