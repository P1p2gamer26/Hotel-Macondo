package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Testimonio;

public interface TestimonioService {

    /** Retorna todos los testimonios publicados. */
    Collection<Testimonio> buscarTodos();
}
