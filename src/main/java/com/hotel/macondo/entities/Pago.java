package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    private Integer id;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private String estado;

    /**
     * Marca el pago como confirmado.
     */
    public void confirmar() {
        estado = "CONFIRMADO";
    }
}
