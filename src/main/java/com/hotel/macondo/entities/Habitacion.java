package com.hotel.macondo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// lombok: getters, setters, toString, equals
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Habitacion {

    private Integer id;
    private String nombre;
    private String etiqueta;
    private String descripcion;
    private long precio;
    private int capacidad;
    private String imagen;

}
