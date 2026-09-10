package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.TipoHabitacion;

public interface TipoHabitacionService {

    // Busca todos los tipos de habitacion ordenados por ID.
    Collection<TipoHabitacion> buscarTodos();

    /**
     * Busca un tipo de habitacion por su ID.
     *
     * @throws RecursoNoEncontradoException si el tipo de habitacion no es encontrado
     */
    TipoHabitacion buscarPorId(Long id);

    // Guarda un tipo de habitacion y actualiza las habitaciones asociadas.
    TipoHabitacion guardar(TipoHabitacion tipo);

    /**
     * Elimina un tipo de habitacion solo si no hay habitaciones asignadas.
     * 
     * @throws PeticionImposible si el tipo de habitacion tiene habitaciones asignadas
     */
    void eliminar(Long id);
}