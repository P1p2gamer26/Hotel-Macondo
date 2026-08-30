package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@Service
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

    @Autowired
    private TipoHabitacionRepository repository;

    @Override
    public Collection<TipoHabitacion> buscarTodos() {
        return repository.findAll();
    }

    @Override
    public TipoHabitacion buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public TipoHabitacion guardar(TipoHabitacion tipo) {
        return repository.save(tipo);
    }

    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }
}