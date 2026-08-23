package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.service.OperadorService;

@Controller
@RequestMapping("/operadores")
public class OperadorController {

    private final OperadorService service;

    /**
     * Conecta el controlador con la interfaz de operadores.
     */
    @Autowired
    public OperadorController(OperadorService service) {
        this.service = service;
    }

    /**
     * Retorna los operadores guardados temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Operador> listar() {
        return service.buscarTodos();
    }
}
