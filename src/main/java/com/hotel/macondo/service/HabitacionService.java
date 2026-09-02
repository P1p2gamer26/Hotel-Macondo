package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.TipoHabitacion;

public interface HabitacionService {

    /** Retorna todas las habitaciones disponibles en el catalogo. */
    Collection<Habitacion> buscarTodas();

    /** Busca una habitacion por su identificador. */
    Habitacion buscarPorId(Integer id);

    /** Busca una habitacion por su nombre comercial. */
    Habitacion buscarPorNombre(String nombre);

    /** Filtra las habitaciones que admiten la cantidad de personas indicada. */
    Collection<Habitacion> buscarPorPersonas(int personas);

    /**
     * Crea o actualiza una habitacion asignandole un tipo de habitacion por
     * su identificador. El tipo es la fuente de verdad: de el se derivan el
     * nombre, la descripcion, el precio y la capacidad de la habitacion.
     */
    Habitacion guardar(Habitacion habitacion, Integer idTipo);

    /**
     * Propaga los cambios de un tipo de habitacion a todas las habitaciones
     * que lo referencian, de modo que reflejen los nuevos datos comerciales.
     */
    void actualizarHabitacionesPorTipo(TipoHabitacion tipo);

    /**
     * Indica si existe al menos una habitacion asignada al tipo indicado.
     */
    boolean existeHabitacionConTipo(Integer idTipo);

    /** Elimina una habitacion por identificador. */
    void eliminar(Integer id);
}
