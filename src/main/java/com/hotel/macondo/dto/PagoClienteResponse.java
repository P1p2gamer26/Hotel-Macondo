package com.hotel.macondo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO DE SALIDA (RESPONSE) - CLIENTE
 *
 * Proyecta un pago con el número de reserva al que pertenece para el
 * historial de pagos del cliente sin exponer las relaciones JPA.
 */
public record PagoClienteResponse(
                Long id,
                String numeroReserva,
                BigDecimal monto,
                String metodoPago,
                LocalDateTime fechaPago,
                String estado) {
}
