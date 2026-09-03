package com.hotel.macondo.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Habitacion;

// Repositorio encargado de realizar las operaciones con la base de datos
@Repository
public class HabitacionRepository {

    // Base de datos falsa. Es LinkedHashMap y no HashMap porque el orden de
    // las habitaciones importa: en la landing deben salir de la mas economica
    // a la mas costosa, igual que en el diseño.
    private Map<Integer, Habitacion> data = new LinkedHashMap<>();
    private int siguienteId = 5;

    /** Carga las habitaciones de prueba del catalogo.
     * mas raro q tenia una etiqueta ahhhhh */
    public HabitacionRepository() {
        data.put(1, new Habitacion(1, "Normal", "ACOGEDORA",
                "Refugio íntimo con vista al jardín tropical. Cama queen, aire acondicionado y todo el confort que necesitas.",
                350000, 2, "/images/HabitacionNormal.avif"));
        data.put(2, new Habitacion(2, "Executive", "POPULAR",
                "Espacio amplio con sala de trabajo, bañera de lujo y vistas privilegiadas al mar Caribe.",
                580000, 3, "/images/HabitacionExecutive.avif"));
        data.put(3, new Habitacion(3, "VIP", "EXCLUSIVA",
                "Suite boutique con terraza privada, jacuzzi exterior y servicio de mayordomo personalizado.",
                950000, 4, "/images/HabitacionVIP.avif"));
        data.put(4, new Habitacion(4, "Luxury", "ÚNICO",
                "La experiencia definitiva: villa frente al mar, piscina privada y atención sin igual las 24 horas.",
                1800000, 6, "/images/HabitacionLuxury.avif"));
    }

    /** Retorna todas las habitaciones del catalogo. */
    public Collection<Habitacion> findAll() {
        return data.values();
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
     *
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
