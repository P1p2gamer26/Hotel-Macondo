package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.ServicioRepository;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;

    /**
     * Crea el servicio con su repositorio de servicios.
     */
    @Autowired
    public ServicioServiceImpl(ServicioRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Servicio> buscarTodos() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Servicio buscarPorId(Integer id) {
        return repository.findById(id);
    }
}
