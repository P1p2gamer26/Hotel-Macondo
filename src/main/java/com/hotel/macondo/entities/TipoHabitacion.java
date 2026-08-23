package com.hotel.macondo.entities;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class TipoHabitacion {

    private String nombre;
    private String descripcion;
    private BigDecimal precioNoche;
    private Integer capacidadPersonas;



    /**
     * Calcula el costo de una estadia para la cantidad de noches indicada.
     */
    public BigDecimal calcularCosto(long noches) {
        if (precioNoche == null || noches <= 0) {
            return BigDecimal.ZERO;
        }
        return precioNoche.multiply(BigDecimal.valueOf(noches));
    }
}
