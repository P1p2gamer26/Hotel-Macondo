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
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.service.ServicioService;

// Controlador REST para catálogo y mantenimiento de servicios del hotel
@RestController
@RequestMapping("/api/servicio")
@CrossOrigin(origins = "http://localhost:4200")
public class ServicioRestController {

    @Autowired
    private ServicioService servicioService;

    // ==========================================
    // CATÁLOGO DE LANDING Y CLIENTE
    // ==========================================

    // Retorna únicamente los servicios con estado activo para el portal público
    // http://localhost:8080/api/servicio/catalogo
    @GetMapping("/catalogo")
    public List<Servicio> catalogoActivo() {
        return servicioService.obtenerCatalogoActivo();
    }

    // Obtiene servicios destacados o recomendados para el dashboard del cliente
    // http://localhost:8080/api/servicio/recomendaciones
    @GetMapping("/recomendaciones")
    public List<Servicio> recomendaciones() {
        return servicioService.obtenerRecomendaciones(3);
    }

    // Retorna servicios relacionados para la vista de detalle
    // http://localhost:8080/api/servicio/relacionados/{id}
    @GetMapping("/relacionados/{id}")
    public List<Servicio> relacionados(@PathVariable Long id) {
        return servicioService.obtenerRelacionados(id, 2);
    }

    // ==========================================
    // GESTIÓN ADMINISTRATIVA
    // ==========================================

    // Retorna todos los servicios sin importar su estado
    // http://localhost:8080/api/servicio
    @GetMapping
    public List<Servicio> buscarTodos() {
        Collection<Servicio> servicios = servicioService.buscarTodos();
        return new ArrayList<>(servicios);
    }

    // Busca un servicio por su identificador
    // Incluye servicios inactivos para permitir LA edición del administrador
    // http://localhost:8080/api/servicio/find/{id}
    @GetMapping("/find/{id}")
    public Servicio buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorIdIncluyendoInactivos(id);
    }

    // Registra un nuevo servicio en el catálogo
    // http://localhost:8080/api/servicio
    @PostMapping
    public Servicio crear(@RequestBody Servicio servicio) {
        return servicioService.guardar(servicio);
    }

    // Modifica los datos de un servicio existente
    // http://localhost:8080/api/servicio
    @PutMapping
    public Servicio actualizar(@RequestBody Servicio servicio) {
        return servicioService.guardar(servicio);
    }

    // Alterna el estado activo o inactivo de un servicio
    // http://localhost:8080/api/servicio/estado/{id}
    @PutMapping("/estado/{id}")
    public Servicio cambiarEstado(@PathVariable Long id) {
        Servicio servicio = servicioService.cambiarEstado(id);
        if (servicio == null) {
            throw new RecursoNoEncontradoException("Servicio no encontrado");
        }
        return servicio;
    }
}