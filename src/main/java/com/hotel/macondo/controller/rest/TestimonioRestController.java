package com.hotel.macondo.controller.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.service.TestimonioService;

@RestController
@RequestMapping("/api/testimonio")
@CrossOrigin(origins = "http://localhost:4200")
public class TestimonioRestController {

    @Autowired
    private TestimonioService testimonioService;

    // ==========================================
    // TESTIMONIOS DE LA LANDING PAGE
    // ==========================================

    // Retorna el listado completo de testimonios para la landing page
    // http://localhost:8080/api/testimonio
    @GetMapping
    public Collection<Testimonio> buscarTodos() {
        return testimonioService.buscarTodos();
    }
}