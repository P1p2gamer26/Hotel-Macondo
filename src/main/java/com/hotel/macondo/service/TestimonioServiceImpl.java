package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.repository.TestimonioRepository;

@Service
public class TestimonioServiceImpl implements TestimonioService {

    private final TestimonioRepository repository;

    /**
     * Crea el servicio con su repositorio de testimonios.
     */
    @Autowired
    public TestimonioServiceImpl(TestimonioRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Testimonio> buscarTodos() {
        return repository.findAll();
    }
}
