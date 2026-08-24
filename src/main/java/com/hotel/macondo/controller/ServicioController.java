package com.hotel.macondo.controller;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private final ServicioService service;

    /**
     * Conecta el controlador con la interfaz de servicios.
     */
    @Autowired
    public ServicioController(ServicioService service) {
        this.service = service;
    }

    /** Construye el catalogo publico de experiencias del hotel. */
    @GetMapping
    public String listar(Model model) {
        Collection<Servicio> catalogo = service.buscarTodos().stream()
                .filter(Servicio::isActivo)
                .sorted(Comparator.comparing(Servicio::getId))
                .toList();
        model.addAttribute("servicios", catalogo);
        model.addAttribute("categorias",
                catalogo.stream()
                        .map(Servicio::getCategoria)
                        .distinct()
                        .collect(Collectors.toList()));
        return "servicios";
    }

    /** Construye el detalle publico de un servicio activo. */
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Integer id, Model model) {
        Servicio servicio = service.buscarPorId(id);
        if (servicio == null || !servicio.isActivo()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Servicio no encontrado");
        }

        List<Servicio> relacionados = service.buscarTodos().stream()
                .filter(Servicio::isActivo)
                .filter(item -> !item.getId().equals(id))
                .limit(2)
                .toList();
        model.addAttribute("servicio", servicio);
        model.addAttribute("relacionados", relacionados);
        return "detalle_servicio";
    }
}
