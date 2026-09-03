package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@Service
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository repository;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    /**
     * Crea el servicio con su repositorio de habitaciones y el de tipos,
     * necesario para resolver el tipo que se asigna a cada habitacion.
     */
    @Autowired
    public HabitacionServiceImpl(HabitacionRepository repository,
            TipoHabitacionRepository tipoHabitacionRepository) {
        this.repository = repository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
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
    public long contarDisponibles() {
        return repository.findAll().stream()
                .filter(Habitacion::estaHabilitada)
                .count();
    }

    /** {@inheritDoc} */
    @Override
    public Habitacion guardar(Habitacion habitacion, Integer idTipo) {
        if (idTipo == null) {
            return null;
        }
        TipoHabitacion tipo = tipoHabitacionRepository.findById(idTipo);
        if (tipo == null) {
            return null;
        }
        habitacion.aplicarTipo(tipo);
        return repository.save(habitacion);
    }

    /** {@inheritDoc} */
    @Override
    public Habitacion cambiarEstado(Integer id) {
        Habitacion habitacion = repository.findById(id);
        if (habitacion == null) {
            return null;
        }
        if (habitacion.estaHabilitada()) {
            habitacion.deshabilitar();
        } else {
            habitacion.habilitar();
        }
        return repository.save(habitacion);
    }

    /** {@inheritDoc} */
    @Override
    public void actualizarHabitacionesPorTipo(TipoHabitacion tipo) {
        if (tipo == null || tipo.getId() == null) {
            return;
        }
        for (Habitacion habitacion : repository.findAll()) {
            if (habitacion.getTipoHabitacion() != null
                    && tipo.getId().equals(habitacion.getTipoHabitacion().getId())) {
                habitacion.aplicarTipo(tipo);
                repository.save(habitacion);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean existeHabitacionConTipo(Integer idTipo) {
        if (idTipo == null) {
            return false;
        }
        return repository.findAll().stream()
                .anyMatch(habitacion -> habitacion.getTipoHabitacion() != null
                        && idTipo.equals(habitacion.getTipoHabitacion().getId()));
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }
}
