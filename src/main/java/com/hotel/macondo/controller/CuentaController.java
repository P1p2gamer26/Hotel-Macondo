package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.service.CuentaService;

@Controller
@RequestMapping("/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService service;

    /**
     * Retorna las cuentas guardadas temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Cuenta> listar() {
        return service.buscarTodas();
    }
}
