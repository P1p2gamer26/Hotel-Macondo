package com.hotel.macondo.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.TipoHabitacion;

// Repositorio encargado de realizar las operaciones con la base de datos
@Repository
public class HabitacionRepository {

    // Base de datos falsa. Es LinkedHashMap y no HashMap porque el orden de
    // las habitaciones importa: en la landing deben salir de la mas economica
    // a la mas costosa, igual que en el diseño.
    private final Map<Long, Habitacion> data = new LinkedHashMap<>();
    private long siguienteId = 5L;

    /**
     * Carga las habitaciones de prueba del catalogo tomando los tipos del
     * repositorio de tipos: el catalogo de tipos tiene un unico dueño y este
     * repositorio no fabrica los suyos.
     */
    @Autowired
    public HabitacionRepository(TipoHabitacionRepository tipoHabitacionRepository) {
        sembrar(1L, "ACOGEDORA", "/images/HabitacionNormal.avif", tipoHabitacionRepository.findById(1L));
        sembrar(2L, "POPULAR", "/images/HabitacionExecutive.avif", tipoHabitacionRepository.findById(2L));
        sembrar(3L, "EXCLUSIVA", "/images/HabitacionVIP.avif", tipoHabitacionRepository.findById(3L));
        sembrar(4L, "ÚNICO", "/images/HabitacionLuxury.avif", tipoHabitacionRepository.findById(4L));
    }

    /**
     * Registra una habitacion de prueba en el piso 1, disponible y con los
     * datos comerciales derivados de su tipo.
     */
    private void sembrar(Long id, String etiqueta, String imagen, TipoHabitacion tipo) {
        Habitacion habitacion = new Habitacion(
                tipo.getNombre(),
                etiqueta,
                tipo.getDescripcion(),
                tipo.getPrecioNoche(),
                tipo.getCapacidadPersonas(),
                imagen,
                String.valueOf(id),
                "DISPONIBLE",
                1);
        habitacion.aplicarTipo(tipo);
        habitacion.setId(id);
        data.put(id, habitacion);
    }

    /** Retorna todas las habitaciones del catalogo. */
    public Collection<Habitacion> findAll() {
        return List.copyOf(data.values());
    }

    /** Busca una habitacion por identificador. */
    public Habitacion findById(Long id) {
        return data.get(id);
    }

    /** Busca una habitacion por nombre. */
    public Habitacion findByNombre(String nombre) {
        for (Habitacion habitacion : data.values()) {
            if (habitacion.getNombre().equalsIgnoreCase(nombre)) {
                return habitacion;
            }
        }
        return null;
    }

    /** Filtra habitaciones por capacidad minima. */
    public Collection<Habitacion> findByPersonas(int personas) {
        Collection<Habitacion> result = new ArrayList<>();
        for (Habitacion habitacion : data.values()) {
            if (habitacion.getCapacidad() >= personas) {
                result.add(habitacion);
            }
        }
        return result;
    }

    /**
     * Crea o actualiza una habitacion en memoria.
     */
    public Habitacion save(Habitacion habitacion) {
        if (habitacion.getId() == null) {
            habitacion.setId(siguienteId++);
        }
        data.put(habitacion.getId(), habitacion);
        return habitacion;
    }

    /**
     * Elimina una habitacion por identificador.
     */
    public void delete(Long id) {
        data.remove(id);
    }

}
