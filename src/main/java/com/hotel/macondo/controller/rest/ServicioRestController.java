package com.hotel.macondo.controller.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.service.ServicioService;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

/**
 * Controlador REST para gestionar servicios.
 */
@RestController
@RequestMapping("/api/servicio")
@CrossOrigin(origins = "http://localhost:4200")
public class ServicioRestController {

    @Autowired
    private ServicioService servicioService;

    /**
     * Retorna todos los servicios.
     *
     * @return lista de todos los servicios
     */
    @GetMapping
    public List<Servicio> buscarTodos() {
        Collection<Servicio> servicios = servicioService.buscarTodos();
        return new ArrayList<>(servicios);
    }

    /**
     * Busca un servicio por su identificador.
     *
     * @param id identificador del servicio
     * @return el servicio encontrado
     *         Incluye los desactivados, porque el admin tambien los edita.
     * @throws RecursoNoEncontradoException si no existe un servicio con ese id
     */
    @GetMapping("/find/{id}")
    public Servicio buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorIdIncluyendoInactivos(id);
    }

    /**
     * Retorna servicios recomendados (activos, límite 3).
     *
     * @return lista de servicios recomendados
     */
    @GetMapping("/recomendaciones")
    public List<Servicio> recomendaciones() {
        return servicioService.obtenerRecomendaciones(3);
    }

    /**
     * Crea un nuevo servicio.
     *
     * @param servicio datos del servicio a crear
     * @return el servicio creado
     */
    @PostMapping
    public Servicio crear(@RequestBody Servicio servicio) {
        return servicioService.guardar(servicio);
    }

    /**
     * Actualiza un servicio existente.
     *
     * @param servicio datos del servicio a actualizar
     * @return el servicio actualizado
     */
    @PutMapping
    public Servicio actualizar(@RequestBody Servicio servicio) {
        return servicioService.guardar(servicio);
    }

    /**
     * Cambia el estado de un servicio (activo/inactivo).
     *
     * @param id identificador del servicio
     * @return el servicio con el estado cambiado
     * @throws RecursoNoEncontradoException si no existe un servicio con ese id
     */
    @PutMapping("/estado/{id}")
    public Servicio cambiarEstado(@PathVariable Long id) {
        Servicio servicio = servicioService.cambiarEstado(id);
        if (servicio == null) {
            throw new RecursoNoEncontradoException("Servicio no encontrado");
        }
        return servicio;
    }

    /**
     * Retorna solo los servicios que esten ACTIVOS para el landing
     *
     * @return lista de servicios activos
     */
    @GetMapping("/catalogo")
    public List<Servicio> catalogoActivo() {
        return servicioService.obtenerCatalogoActivo();
    }

    /**
     * Retorna servicios relacionados para la ficha de detalle.
     *
     * @param id identificador del servicio actual
     * @return lista de servicios relacionados
     */
    @GetMapping("/relacionados/{id}")
    public List<Servicio> relacionados(@PathVariable Long id) {
        return servicioService.obtenerRelacionados(id, 2);
    }
}