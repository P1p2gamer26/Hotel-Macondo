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
import com.hotel.macondo.service.TipoHabitacionService;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.errors.PeticionImposible;

/**
 * Controlador REST para gestionar tipos de habitación.
 */
@RestController
@RequestMapping("/api/tipo-habitacion")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoHabitacionRestController {

    @Autowired
    private TipoHabitacionService tipoHabitacionService;

    @Autowired
    private HabitacionService habitacionService;

    /**
     * Retorna todos los tipos de habitación.
     *
     * @return lista de todos los tipos de habitación
     */
    @GetMapping
    public List<TipoHabitacion> buscarTodos() {
        Collection<TipoHabitacion> tipos = tipoHabitacionService.buscarTodos();
        return new ArrayList<>(tipos);
    }

    /**
     * Busca un tipo de habitación por su identificador.
     *
     * @param id identificador del tipo de habitación
     * @return el tipo de habitación encontrado
     * @throws RecursoNoEncontradoException si no existe un tipo con ese id
     */
    @GetMapping("/find/{id}")
    public TipoHabitacion buscarPorId(@PathVariable Long id) {
        return tipoHabitacionService.buscarPorId(id);
    }

    /**
     * Crea un nuevo tipo de habitación.
     *
     * @param tipo datos del tipo de habitación a crear
     * @return el tipo de habitación creado
     */
    @PostMapping
    public TipoHabitacion crear(@RequestBody TipoHabitacion tipo) {
        return tipoHabitacionService.guardar(tipo);
    }

    /**
     * Actualiza un tipo de habitación existente.
     *
     * @param tipo datos del tipo de habitación a actualizar
     * @return el tipo de habitación actualizado
     */
    @PutMapping
    public TipoHabitacion actualizar(@RequestBody TipoHabitacion tipo) {
        return tipoHabitacionService.guardar(tipo);
    }

    /**
     * Elimina un tipo de habitación por su identificador.
     *
     * @param id identificador del tipo de habitación
     * @throws RecursoNoEncontradoException si no existe un tipo con ese id
     * @throws PeticionImposible si el tipo tiene habitaciones asociadas
     */
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        if (habitacionService.existeHabitacionConTipo(id)) {
            throw new PeticionImposible(
                "No se puede eliminar el tipo porque tiene habitaciones asociadas.");
        }
        tipoHabitacionService.eliminar(id);
    }
}