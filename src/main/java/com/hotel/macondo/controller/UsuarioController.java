package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Usuario;
import com.hotel.macondo.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    /**
     * Retorna los clientes guardados temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Usuario> listar() {
        return service.buscarTodos();
    }
}
