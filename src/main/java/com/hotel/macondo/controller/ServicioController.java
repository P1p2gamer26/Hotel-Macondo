package com.hotel.macondo.controller;

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
        model.addAttribute("servicios", service.obtenerCatalogoActivo());
        model.addAttribute("categorias", service.obtenerCategoriasDisponibles());
        return "servicio/servicios";
    }

    /** Construye el detalle publico de un servicio activo. */
    @GetMapping("/{id}")
    public String mostrarDetalle(@PathVariable Integer id, Model model) {
        Servicio servicio = service.buscarPorId(id);

        List<Servicio> relacionados = service.obtenerRelacionados(id, 2);
        model.addAttribute("servicio", servicio);
        model.addAttribute("relacionados", relacionados);
        return "servicio/detalle_servicio";
    }
}