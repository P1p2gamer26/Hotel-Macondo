package com.hotel.macondo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO DE SALIDA (RESPONSE) - OPERADOR
 *
 * Proyecta el resumen de las reservas del hotel para la tabla y
 * listados principales del panel de administración del Operador.
 *
 * Se usa porque transporta solo los identificadores, fechas, estados y
 * montos para renderizar la tabla sin transferir colecciones completas de
 * objetos. También evita errores de serialización infinita a JSON producidas
 * por las relaciones JPA bidireccionales entre Reserva, Cliente y Habitación.
 */

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