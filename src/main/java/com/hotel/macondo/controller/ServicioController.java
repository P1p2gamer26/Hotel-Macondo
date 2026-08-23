package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * Retorna el catalogo temporal de servicios.
     */
    @GetMapping
    @ResponseBody
    public Collection<Servicio> listar() {
        return service.buscarTodos();
    }
}
