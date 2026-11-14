package com.hotel.macondo.DTOs.Salida;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ReservaClienteDTO {
    private Long id;
    private String numeroReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadPersonas;
    private String estado;
    private BigDecimal precioNoche;
    private BigDecimal total;
    private HabitacionDTO habitacion;
}