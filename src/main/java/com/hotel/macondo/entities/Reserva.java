package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    private String numeroReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadPersonas;
    private String estado;
    private BigDecimal total;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Habitacion habitacion;

    private TipoHabitacion tipoHabitacion;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cuenta cuenta;

    /**
     * Cancela una reserva que todavia no ha iniciado.
     */
    public boolean cancelar() {
        if (fechaInicio == null || !fechaInicio.isAfter(LocalDate.now())
                || "CANCELADA".equals(estado)) {
            return false;
        }
        estado = "CANCELADA";
        return true;
    }

    /**
     * Modifica los datos de una reserva cuando el nuevo rango es valido.
     */
    public boolean modificar(LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin,
            TipoHabitacion nuevoTipo, int personas) {
        if (nuevaFechaInicio == null || nuevaFechaFin == null
                || !nuevaFechaInicio.isBefore(nuevaFechaFin)
                || nuevoTipo == null || personas <= 0
                || personas > nuevoTipo.getCapacidadPersonas()) {
            return false;
        }

        fechaInicio = nuevaFechaInicio;
        fechaFin = nuevaFechaFin;
        tipoHabitacion = nuevoTipo;
        cantidadPersonas = personas;
        long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        total = nuevoTipo.calcularCosto(noches);
        return true;
    }

    /**
     * Activa una reserva cuando su periodo incluye la fecha actual.
     */
    public void activar() {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && fechaFin != null
                && !hoy.isBefore(fechaInicio) && hoy.isBefore(fechaFin)) {
            estado = "ACTIVA";
        }
    }

    /**
     * Finaliza la reserva cuando la cuenta asociada esta saldada.
     */
    public boolean finalizar() {
        if (cuenta != null && cuenta.estaSaldada()) {
            estado = "FINALIZADA";
            return true;
        }
        return false;
    }
}
