package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.repository.OperadorRepository;

@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository repository;

    /**
     * Crea el servicio con su repositorio de operadores.
     */
    @Autowired
    public OperadorServiceImpl(OperadorRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Operador> buscarTodos() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Operador buscarPorId(Integer id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Operador guardar(Operador operador) {
        return repository.save(operador);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }
}
