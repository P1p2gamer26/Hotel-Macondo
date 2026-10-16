package com.hotel.macondo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaOperadorResponse(
        Long id,
        String numeroReserva,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer cantidadPersonas,
        String estado,
        BigDecimal precioNoche,
        BigDecimal total,
        ClienteResumen cliente,
        HabitacionResumen habitacion) {

    public record ClienteResumen(Long id, String nombre, String apellido) {
    }

    public record HabitacionResumen(
            Long id,
            String numero,
            String nombre,
            TipoHabitacionResumen tipoHabitacion) {
    }

    public record TipoHabitacionResumen(Long id, String nombre) {
    }
}
