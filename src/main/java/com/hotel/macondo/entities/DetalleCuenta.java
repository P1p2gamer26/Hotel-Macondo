package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCuenta {

    private Integer id;
    private Integer cantidad;
    private BigDecimal precio;
    private LocalDateTime fechaRegistro;
    private Servicio servicio;

    /**
     * Calcula el valor total de este item de cuenta.
     */
    public BigDecimal calcularSubtotal() {
        if (precio == null || cantidad == null || cantidad <= 0) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }
}
