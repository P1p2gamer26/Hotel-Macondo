package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.TipoHabitacion;

public interface TipoHabitacionService {

    Collection<TipoHabitacion> buscarTodos();

    TipoHabitacion buscarPorId(Integer id);

    TipoHabitacion guardar(TipoHabitacion tipo);

    void eliminar(Integer id);
}