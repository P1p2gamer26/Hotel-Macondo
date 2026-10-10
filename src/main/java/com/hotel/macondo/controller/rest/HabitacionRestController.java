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
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;

/**
 * Controlador REST para gestionar habitaciones.
 */
@RestController
@RequestMapping("/api/habitacion")
@CrossOrigin(origins = "http://localhost:4200")
public class HabitacionRestController {

    @Autowired
    private HabitacionService habitacionService;

    /**
     * Retorna todas las habitaciones.
     *
     * @return lista de todas las habitaciones
     */
    @GetMapping
    public List<Habitacion> buscarTodas() {
        Collection<Habitacion> habitaciones = habitacionService.buscarTodas();
        return new ArrayList<>(habitaciones);
    }

    /**
     * Busca una habitación por su identificador.
     *
     * @param id identificador de la habitación
     * @return la habitación encontrada
     * @throws RecursoNoEncontradoException si no existe una habitación con ese id
     */
    @GetMapping("/find/{id}")
    public Habitacion buscarPorId(@PathVariable Long id) {
        return habitacionService.buscarPorId(id);
    }

    /**
     * Busca la habitación asociada a una reserva.
     *
     * @param reservaId identificador de la reserva
     * @return la habitación encontrada
     * Retorna vacio (null) si la reserva no tiene habitacion asignada.
     */
    @GetMapping("/reserva/{reservaId}")
    public Habitacion buscarPorReserva(@PathVariable Long reservaId) {
        return habitacionService.buscarPorReserva(reservaId);
    }

    /**
     * Crea una nueva habitación.
     *
     * @param habitacion datos de la habitación a crear
     * @return la habitación creada
     * @throws FormularioErroneoException si el tipo de habitación es nulo o no existe
     */
    @PostMapping
    public Habitacion crear(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        return habitacionService.guardar(habitacion, idTipo);
    }

    /**
     * Actualiza una habitación existente.
     *
     * @param habitacion datos de la habitación a actualizar
     * @return la habitación actualizada
     * @throws FormularioErroneoException si el tipo de habitación es nulo o no existe
     */
    @PutMapping
    public Habitacion actualizar(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        return habitacionService.guardar(habitacion, idTipo);
    }

    /**
     * Cambia el estado de una habitación (DISPONIBLE/NO_DISPONIBLE).
     *
     * @param id identificador de la habitación
     * @return la habitación con el estado cambiado
     * @throws RecursoNoEncontradoException si no existe una habitación con ese id
     */
    @PutMapping("/estado/{id}")
    public Habitacion cambiarEstado(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.cambiarEstado(id);
        if (habitacion == null) {
            throw new RecursoNoEncontradoException("No se encontro habitacion con id " + id);
        }
        return habitacion;
    }

    /**
     * Elimina una habitación por su identificador.
     *
     * @param id identificador de la habitación
     * @throws RecursoNoEncontradoException si no existe una habitación con ese id
     * @throws PeticionImposible si la habitación tiene reservas asociadas
     */
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        if (!habitacionService.reservasAsociadas(id).isEmpty()) {
            throw new PeticionImposible(
                "No se puede eliminar la habitación porque tiene reservas asociadas.");
        }
        habitacionService.eliminar(id);
    }

    private void validarTipoHabitacion(Habitacion habitacion) {
        if (habitacion.getTipoHabitacion() == null || habitacion.getTipoHabitacion().getId() == null) {
            throw new FormularioErroneoException("Debes seleccionar un tipo de habitación.");
        }
    }
}