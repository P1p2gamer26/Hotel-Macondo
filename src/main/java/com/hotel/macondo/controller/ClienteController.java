package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    /**
     * Retorna los clientes guardados temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Cliente> listar() {
        return service.buscarTodos();
    }
}
