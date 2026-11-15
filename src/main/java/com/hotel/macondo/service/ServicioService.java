package com.hotel.macondo.service;

import java.util.Collection;
import java.util.List;

import com.hotel.macondo.entities.Servicio;

public interface ServicioService {

    // Retorna todos los servicios del hotel
    Collection<Servicio> buscarTodos();

    /**
     * Busca un servicio por identificador sin importar si esta activo (lo usa el
     * panel admin para editar).
     * 
     * @throws RecursoNoEncontradoException si no existe un servicio con ese id.
     */
    Servicio buscarPorIdIncluyendoInactivos(Long id);

    // Crea o actualiza un servicio
    Servicio guardar(Servicio servicio);

    // Retorna solo los servicios activos ordenados por ID para el catalogo publico
    List<Servicio> obtenerCatalogoActivo();

    // Obtiene servicios recomendados/relacionados activos excluyendo el actual
    List<Servicio> obtenerRelacionados(Long servicioActualId, int limite);

    List<Servicio> obtenerRecomendaciones(int limite);

    /**
     * Activa o desactiva un servicio del catalogo y persiste el cambio.
     * Retorna null si el servicio no existe.
     */
    Servicio cambiarEstado(Long id);
}
