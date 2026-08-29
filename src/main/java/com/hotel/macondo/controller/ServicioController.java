package com.hotel.macondo.controller;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.service.ServicioService;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService service;

    /** Construye el catalogo publico de experiencias del hotel. */
    @GetMapping
    public String listar(Model model) {
        // El catalogo publico solo incluye servicios activos y se presenta por
        // identificador.
        Collection<Servicio> catalogo = service.buscarTodos().stream()
                .filter(Servicio::isActivo)
                .sorted(Comparator.comparing(Servicio::getId))
                .toList();
        model.addAttribute("servicios", catalogo);
        // Las categorias se derivan de los datos del catalogo para el filtro visual.
        model.addAttribute("categorias",
                catalogo.stream()
                        .map(Servicio::getCategoria)
                        .distinct()
                        .toList());
        return "servicio/servicios";
    }

    /** Construye el detalle publico de un servicio activo. */
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Integer id, Model model) {
        Servicio servicio = service.buscarPorId(id);
        if (servicio == null || !servicio.isActivo()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Servicio no encontrado");
        }

        // La vista de detalle recibe servicios alternativos para continuar la
        // exploracion.
        List<Servicio> relacionados = service.buscarTodos().stream()
                .filter(Servicio::isActivo)
                .filter(item -> !item.getId().equals(id))
                .limit(2)
                .toList();
        model.addAttribute("servicio", servicio);
        model.addAttribute("relacionados", relacionados);
        return "servicio/detalle_servicio";
    }

}
