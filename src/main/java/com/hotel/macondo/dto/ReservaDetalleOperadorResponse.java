package com.hotel.macondo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO DE SALIDA (RESPONSE) - OPERADOR - DETALLE DE RESERVA
 *
 * Proyecta la información para el detalle de una reserva (cliente, habitación
 * física, desglose de cuenta, consumos de servicios y pagos).
 *
 * Se usa porque permite estructurar en una sola respuesta la información
 * completa de consumo y facturación, pero sobre todo evita provocar ciclos
 * bidireccionales durante la serialización a JSON.
 */

public record ReservaDetalleOperadorResponse(
                Long id,
                String numeroReserva,
                LocalDate fechaInicio,
                LocalDate fechaFin,
                Integer cantidadPersonas,
                String estado,
                BigDecimal precioNoche,
                BigDecimal total,
                ClienteDetalle cliente,
                HabitacionDetalle habitacion,
                CuentaDetalle cuenta) {

        public record ClienteDetalle(
                        Long id,
                        String nombre,
                        String apellido,
                        String cedula,
                        String telefono,
                        String correo) {
        }

        public record HabitacionDetalle(
                        Long id,
                        String nombre,
                        String etiqueta,
                        BigDecimal precio,
                        int capacidad,
                        String numero,
                        String estado,
                        Integer piso,
                        TipoHabitacionDetalle tipoHabitacion) {
        }

        public record TipoHabitacionDetalle(
                        Long id,
                        String nombre,
                        String descripcion,
                        String imagen,
                        BigDecimal precioNoche,
                        Integer capacidadPersonas) {
        }

        public record CuentaDetalle(
                        Long id,
                        String estado,
                        BigDecimal total,
                        LocalDateTime fechaApertura,
                        List<ConsumoDetalle> consumos,
                        List<PagoDetalle> pagos) {
        }

        public record ConsumoDetalle(
                        Long id,
                        Integer cantidad,
                        BigDecimal precio,
                        LocalDateTime fechaRegistro,
                        String servicio) {
        }

        public record PagoDetalle(
                        Long id,
                        BigDecimal monto,
                        String metodoPago,
                        LocalDateTime fechaPago,
                        String estado) {
        }
}