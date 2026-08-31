package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.repository.HabitacionRepository;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository repository;

    /**
     * Crea el servicio con su repositorio de habitaciones.
     */
    @Autowired
    public HabitacionServiceImpl(HabitacionRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Habitacion> buscarTodas() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Habitacion buscarPorId(Integer id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Habitacion buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Habitacion> buscarPorPersonas(int personas) {
        return repository.findByPersonas(personas);
    }

    /** {@inheritDoc} */
    @Override
    public Habitacion guardar(Habitacion habitacion) {
        return repository.save(habitacion);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }
}
