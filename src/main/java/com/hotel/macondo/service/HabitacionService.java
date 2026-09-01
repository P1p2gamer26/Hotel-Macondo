package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Habitacion;

public interface HabitacionService {

    /** Retorna todas las habitaciones disponibles en el catalogo. */
    Collection<Habitacion> buscarTodas();

    /** Busca una habitacion por su identificador. */
    Habitacion buscarPorId(Integer id);

    /** Busca una habitacion por su nombre comercial. */
    Habitacion buscarPorNombre(String nombre);

    /** Filtra las habitaciones que admiten la cantidad de personas indicada. */
    Collection<Habitacion> buscarPorPersonas(int personas);

    /** Crea o actualiza una habitacion. */
    Habitacion guardar(Habitacion habitacion);

    /** Elimina una habitacion por identificador. */
    void eliminar(Integer id);
}
