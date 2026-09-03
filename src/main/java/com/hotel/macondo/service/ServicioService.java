package com.hotel.macondo.service;

import java.util.Collection;
import java.util.List;

import com.hotel.macondo.entities.Servicio;

public interface ServicioService {

    // Retorna todos los servicios del hotel
    Collection<Servicio> buscarTodos();

    // Busca un servicio por identificador
    Servicio buscarPorId(Integer id);

    // Retorna solo los servicios activos ordenados por ID para el catalogo publico
    List<Servicio> obtenerCatalogoActivo();

    // Obtiene la lista de categorias unicas a partir de los servicios activos
    List<String> obtenerCategoriasDisponibles();

    // Obtiene servicios recomendados/relacionados activos excluyendo el actual
    List<Servicio> obtenerRelacionados(Integer servicioActualId, int limite);

    List<Servicio> obtenerRecomendaciones(int limite);
}