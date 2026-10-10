package com.hotel.macondo.controller.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.macondo.dto.RegistroCliente;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.CuentaClienteService;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

/**
 * Controlador REST para gestionar clientes.
 */
@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CuentaClienteService cuentaClienteService;

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return el cliente encontrado
     * @throws RecursoNoEncontradoException si no existe un cliente con ese id
     */
    @GetMapping("/find/{id}")
    public Cliente findById(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    /**
     * Registra un nuevo cliente y crea su cuenta de usuario.
     *
     * @param registro DTO con los datos del cliente y la contraseña
     * @return el cliente creado
     */
    @PostMapping("/registro")
    public Cliente registrar(@RequestBody RegistroCliente registro) {
        if (registro.getCliente() == null) {
            throw new FormularioErroneoException("Faltan los datos del cliente.");
        }
        // El id lo genera la BD: si viniera en el body, save() sobrescribiria a otro cliente
        registro.getCliente().setId(null);
        cuentaClienteService.crearCuenta(registro.getCliente(), registro.getContrasena());
        return registro.getCliente();
    }
}