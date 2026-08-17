package com.hotel.macondo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Testimonio {

    private Integer id;
    private String texto;
    private String nombre;
    private String ciudad;
    private int estrellas;
    private String imagen;

}
