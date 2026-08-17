package com.hotel.macondo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Servicio {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagen;
    private boolean destacado;

}
