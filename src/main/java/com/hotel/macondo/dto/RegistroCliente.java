package com.hotel.macondo.dto;

import com.hotel.macondo.entities.Cliente;
import lombok.Data;
import lombok.ToString;

/**
 * DTO DE ENTRADA (REQUEST) - CLIENTE / REGISTRO
 * 
 * En este caso, se usa para capturar en una única petición HTTP (@RequestBody)
 * los datos
 * personales del Cliente junto con la contraseña de acceso enviada desde
 * Angular.
 * 
 * El protocolo HTTP y Spring Boot solo admiten un único cuerpo por petición. Y,
 * debido a que
 * la contraseña es de Usuario y no de la tabla Cliente, este DTO empaqueta
 * ambos valores
 * para evitar hacer dos peticiones HTTP separadas (una para crear el Cliente y
 * otra para crear el Usuario).
 **/

@Data
public class RegistroCliente {

    private Cliente cliente;

    // Se excluye del toString para evitar que la credencial quede expuesta en logs
    @ToString.Exclude
    private String contrasena;
}