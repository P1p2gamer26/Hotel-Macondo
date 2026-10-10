package com.hotel.macondo.dto;

import com.hotel.macondo.entities.Cliente;
import lombok.Data;
import lombok.ToString;

/** Datos que envia Angular al registrarse: el cliente y su contraseña (un solo @RequestBody). */
@Data
public class RegistroCliente {

    private Cliente cliente;
    // Se excluye del toString para que la contraseña nunca quede en los logs
    @ToString.Exclude
    private String contrasena;
}