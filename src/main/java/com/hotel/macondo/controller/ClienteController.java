package com.hotel.macondo.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;
    @Autowired
    private CuentaClienteService cuentaClienteService;

    /**
     * Retorna los clientes guardados temporalmente.
     */
    @GetMapping
    @ResponseBody
    public Collection<Cliente> listar() {
        return service.buscarTodos();
    }

    /**
     * Elimina la cuenta del cliente y su usuario relacionado
     */
    @GetMapping("/delete/{id}")
    public String eliminarCuenta(@PathVariable Integer id){

        cuentaClienteService.eliminarCuenta(id);

        return "redirect:/logout";
    }
}
