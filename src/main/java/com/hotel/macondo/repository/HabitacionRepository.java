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
    private final Map<Integer, Habitacion> data = new LinkedHashMap<>();
    private int siguienteId = 5;

    /**
     * Carga las habitaciones de prueba del catalogo tomando los tipos del
     * repositorio de tipos: el catalogo de tipos tiene un unico dueño y este
     * repositorio no fabrica los suyos.
     */
    @Autowired
    public HabitacionRepository(TipoHabitacionRepository tipoHabitacionRepository) {
        sembrar(1, "ACOGEDORA", "/images/HabitacionNormal.avif",
                tipoHabitacionRepository.findById(1));
        sembrar(2, "POPULAR", "/images/HabitacionExecutive.avif",
                tipoHabitacionRepository.findById(2));
        sembrar(3, "EXCLUSIVA", "/images/HabitacionVIP.avif",
                tipoHabitacionRepository.findById(3));
        sembrar(4, "ÚNICO", "/images/HabitacionLuxury.avif",
                tipoHabitacionRepository.findById(4));
    }

    /**
     * Registra una habitacion de prueba en el piso 1, disponible y con los
     * datos comerciales derivados de su tipo.
     */
    private void sembrar(Integer id, String etiqueta, String imagen, TipoHabitacion tipo) {
        Habitacion habitacion = new Habitacion(String.valueOf(id), "DISPONIBLE", 1, tipo);
        habitacion.setId(id);
        habitacion.setEtiqueta(etiqueta);
        habitacion.setImagen(imagen);
        data.put(id, habitacion);
    }

    /** Retorna todas las habitaciones del catalogo. */
    public Collection<Habitacion> findAll() {
        return List.copyOf(data.values());
    }

    /** Busca una habitacion por identificador. */
    public Habitacion findById(Integer id) {
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
    public void delete(Integer id) {
        data.remove(id);
    }

}
