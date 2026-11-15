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

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.TipoHabitacionService;

// Controlador REST para la gestión de tipos o categorías de habitación
@RestController
@RequestMapping("/api/tipo-habitacion")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoHabitacionRestController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HabitacionService habitacionService;

    // ==========================================
    // CONSULTAS
    // ==========================================

    // Retorna todos los tipos de habitación registrados
    // http://localhost:8080/api/tipo-habitacion
    @GetMapping
    public List<TipoHabitacion> buscarTodos() {
        Collection<TipoHabitacion> tipos = tipoHabitacionService.buscarTodos();
        return new ArrayList<>(tipos);
    }

    // Busca un tipo de habitación por su identificador
    // http://localhost:8080/api/tipo-habitacion/find/{id}
    @GetMapping("/find/{id}")
    public TipoHabitacion buscarPorId(@PathVariable Long id) {
        return tipoHabitacionService.buscarPorId(id);
    }

    // ==========================================
    // GESTIÓN ADMINISTRATIVA
    // ==========================================

    // Crea un nuevo tipo o categoría de habitación
    // http://localhost:8080/api/tipo-habitacion
    @PostMapping
    public TipoHabitacion crear(@RequestBody TipoHabitacion tipo) {
        return tipoHabitacionService.guardar(tipo);
    }

    // Actualiza la configuración y datos de un tipo de habitación existente
    // http://localhost:8080/api/tipo-habitacion
    @PutMapping
    public TipoHabitacion actualizar(@RequestBody TipoHabitacion tipo) {
        return tipoHabitacionService.guardar(tipo);
    }

    // Elimina una categoría si no tiene habitaciones asociadas
    // Si existen habitaciones ligadas a esta categoría, bloquea la acción
    // http://localhost:8080/api/tipo-habitacion/delete/{id}
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        if (habitacionService.existeHabitacionConTipo(id)) {
            throw new PeticionImposible(
                    "No se puede eliminar el tipo porque tiene habitaciones asociadas.");
        }
        tipoHabitacionService.eliminar(id);
    }
}