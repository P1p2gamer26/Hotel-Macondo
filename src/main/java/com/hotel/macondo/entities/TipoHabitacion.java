package com.hotel.macondo.entities;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoHabitacion {

    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioNoche;
    private Integer capacidadPersonas;

    /**
     * Constructor auxiliar para crear instancias antes de asignar ID.
     */
    public TipoHabitacion(String nombre, String descripcion, BigDecimal precioNoche, Integer capacidadPersonas) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioNoche = precioNoche;
        this.capacidadPersonas = capacidadPersonas;
    }

    /**
     * Calcula el costo total para una cantidad determinada de noches.
     */
    public BigDecimal calcularCosto(long noches) {
        if (precioNoche == null || noches <= 0) {
            return BigDecimal.ZERO;
        }
        return precioNoche.multiply(BigDecimal.valueOf(noches));
    }
}