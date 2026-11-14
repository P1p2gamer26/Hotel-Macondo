package com.hotel.macondo.controller.rest;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.DTOs.Mapper.HabitacionMapper;
import com.hotel.macondo.DTOs.Salida.HabitacionDTO;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.errors.FormularioErroneoException;
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
    // http://localhost:8080/api/habitacion
    @GetMapping
    public List<HabitacionDTO> buscarTodas() {
        Collection<Habitacion> habitaciones = habitacionService.buscarTodas();
        return habitaciones.stream()
                .map(HabitacionMapper.INSTANCE::convert)
                .toList();
    }

    // Busca una habitación por su identificador
    // http://localhost:8080/api/habitacion/find/{id}
    @GetMapping("/find/{id}")
    public HabitacionDTO buscarPorId(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.buscarPorId(id);
        return HabitacionMapper.INSTANCE.convert(habitacion);
    }

    // Busca la habitación asignada a una reserva en particular
    // Retorna null si la reserva aún no tiene una habitación vinculada
    // http://localhost:8080/api/habitacion/reserva/{reservaId}
    @GetMapping("/reserva/{reservaId}")
    public HabitacionDTO buscarPorReserva(@PathVariable Long reservaId) {
        Habitacion habitacion = habitacionService.buscarPorReserva(reservaId);
        return HabitacionMapper.INSTANCE.convert(habitacion);
    }

    // ==========================================
    // ADMINISTRACIÓN Y MANTENIMIENTO
    // ==========================================

    // Crea una nueva habitación validando su categoría asociada
    // http://localhost:8080/api/habitacion
    @PostMapping
    public ResponseEntity<HabitacionDTO> crear(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        Habitacion guardada = habitacionService.guardar(habitacion, idTipo);
        HabitacionDTO dto = HabitacionMapper.INSTANCE.convert(guardada);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Actualiza los datos de una habitación existente
    // http://localhost:8080/api/habitacion
    @PutMapping
    public HabitacionDTO actualizar(@RequestBody Habitacion habitacion) {
        validarTipoHabitacion(habitacion);
        Long idTipo = habitacion.getTipoHabitacion().getId();
        Habitacion actualizada = habitacionService.guardar(habitacion, idTipo);
        return HabitacionMapper.INSTANCE.convert(actualizada);
    }

    // Cambia el estado operativo de la habitación entre disponible y no disponible
    // http://localhost:8080/api/habitacion/estado/{id}
    @PutMapping("/estado/{id}")
    public HabitacionDTO cambiarEstado(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.cambiarEstado(id);
        if (habitacion == null) {
            throw new RecursoNoEncontradoException("No se encontro habitacion con id " + id);
        }
        return HabitacionMapper.INSTANCE.convert(habitacion);
    }

    // Elimina una habitación del sistema si NO TIENE reservas asociadas
    // Pasa a inactiva si registra reservas
    // http://localhost:8080/api/habitacion/delete/{id}
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
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