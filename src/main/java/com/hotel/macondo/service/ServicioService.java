package com.hotel.macondo.service;

import java.math.BigDecimal;
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

    // Cuenta los servicios activos del catalogo, para el tablero del panel admin
    long contarActivos();

    /**
     * Actualiza la informacion editable de un servicio y la persiste.
     * Retorna null si el servicio no existe.
     */
    Servicio actualizarDatos(Integer id, String nombre, String categoria, BigDecimal precio);

    /**
     * Activa o desactiva un servicio del catalogo y persiste el cambio.
     * Retorna null si el servicio no existe.
     */
    Servicio cambiarEstado(Integer id);
}
