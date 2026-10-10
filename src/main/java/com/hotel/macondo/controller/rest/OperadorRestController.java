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

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

/**
 * Controlador REST para gestionar operadores.
 */
@RestController
@RequestMapping("/api/operador")
@CrossOrigin(origins = "http://localhost:4200")
public class OperadorRestController {

    @Autowired
    private OperadorService operadorService;

    /**
     * Retorna todos los operadores.
     *
     * @return lista de todos los operadores
     */
    @GetMapping
    public List<Operador> buscarTodos() {
        Collection<Operador> operadores = operadorService.buscarTodos();
        return new ArrayList<>(operadores);
    }

    /**
     * Crea un nuevo operador. Si el campo activo es null, se establece en true.
     *
     * @param operador datos del operador a crear
     * @return el operador creado
     */
    @PostMapping
    public Operador crear(@RequestBody Operador operador) {
        if (operador.getActivo() == null) {
            operador.setActivo(true);
        }
        return operadorService.guardar(operador);
    }

    /**
     * Cambia el estado de un operador (activo/inactivo).
     *
     * @param id identificador del operador
     * @return el operador con el estado cambiado
     * @throws RecursoNoEncontradoException si no existe un operador con ese id
     */
    @PutMapping("/estado/{id}")
    public Operador cambiarEstado(@PathVariable Long id) {
        Operador operador = operadorService.cambiarEstado(id);
        if (operador == null) {
            throw new RecursoNoEncontradoException("Operador no encontrado");
        }
        return operador;
    }

    /**
     * Elimina un operador por su identificador.
     *
     * @param id identificador del operador
     * @throws RecursoNoEncontradoException si no existe un operador con ese id
     */
    @DeleteMapping("/delete/{id}")
    public void eliminar(@PathVariable Long id) {
        Operador operador = operadorService.buscarPorId(id);
        if (operador == null) {
            throw new RecursoNoEncontradoException("Operador no encontrado");
        }
        operadorService.eliminar(id);
    }
}