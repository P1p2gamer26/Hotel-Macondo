package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@Service
public class TipoHabitacionServiceImpl implements TipoHabitacionService {

    private final TipoHabitacionRepository repository;
    private final HabitacionService habitacionService;

    @Autowired
    public TipoHabitacionServiceImpl(TipoHabitacionRepository repository,
            HabitacionService habitacionService) {
        this.repository = repository;
        this.habitacionService = habitacionService;
    }

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
        TipoHabitacion guardado = repository.save(tipo);
        habitacionService.actualizarHabitacionesPorTipo(guardado);
        return guardado;
    }

    @Override
    public boolean eliminar(Integer id) {
        if (habitacionService.existeHabitacionConTipo(id)) {
            return false;
        }
        repository.delete(id);
        return true;
    }
}