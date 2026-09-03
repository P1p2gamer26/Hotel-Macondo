package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.TipoHabitacion;

public interface TipoHabitacionService {

    Collection<TipoHabitacion> buscarTodos();

    TipoHabitacion buscarPorId(Integer id);

    TipoHabitacion guardar(TipoHabitacion tipo);

    /**
     * Elimina un tipo de habitacion solo si no hay habitaciones asignadas.
     * Devuelve {@code true} si se elimino y {@code false} en caso contrario.
     */
    boolean eliminar(Integer id);
}