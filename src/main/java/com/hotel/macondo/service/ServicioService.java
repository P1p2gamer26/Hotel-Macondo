package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Servicio;

public interface ServicioService {

    /** Retorna todos los servicios del hotel. */
    Collection<Servicio> buscarTodos();

    /** Busca un servicio por identificador. */
    Servicio buscarPorId(Integer id);
}
