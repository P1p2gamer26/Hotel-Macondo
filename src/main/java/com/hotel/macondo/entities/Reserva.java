package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private List<Habitacion> habitaciones = new ArrayList<>();

    /**
     * Agrega una habitacion a la reserva y actualiza el total de la estadia.
     */
    public void agregarHabitacion(Habitacion habitacion) {
        if (habitacion != null && !habitaciones.contains(habitacion)) {
            habitaciones.add(habitacion);
            recalcularTotal();
        }
    }

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
        cantidadPersonas = personas;
        long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        total = habitaciones.isEmpty()
                ? nuevoTipo.calcularCosto(noches)
                : calcularTotalHabitaciones(noches);
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
     * Finaliza la reserva cuando el servicio de checkout confirma que no hay saldo.
     */
    public boolean finalizar(boolean cuentaSaldada) {
        if (cuentaSaldada) {
            estado = "FINALIZADA";
            return true;
        }
        return false;
    }

    /**
     * Indica si la reserva fue cancelada.
     */
    public boolean estaCancelada() {
        return "CANCELADA".equals(estado);
    }

    /**
     * Indica si la reserva sigue vigente: no fue cancelada y su fecha de fin
     * no ha pasado todavia.
     */
    public boolean estaVigente() {
        return !estaCancelada() && fechaFin != null
                && !fechaFin.isBefore(LocalDate.now());
    }

    /**
     * Indica si la reserva pertenece al historial: fue cancelada o su fecha
     * de fin ya paso.
     */
    public boolean esHistorica() {
        return estaCancelada() || (fechaFin != null && fechaFin.isBefore(LocalDate.now()));
    }

    /**
     * Indica si la reserva esta en un estado operativo abierto.
     */
    public boolean estaEnCurso() {
        return estadoEsUnoDe("ACTIVA", "CONFIRMADA", "PENDIENTE");
    }

    /**
     * Indica si la reserva esta en un estado operativo cerrado.
     */
    public boolean estaCerrada() {
        return estadoEsUnoDe("FINALIZADA", "CANCELADA");
    }

    private boolean estadoEsUnoDe(String... estados) {
        return estado != null && List.of(estados).contains(estado.toUpperCase());
    }

    private void recalcularTotal() {
        if (fechaInicio == null || fechaFin == null
                || !fechaInicio.isBefore(fechaFin)) {
            total = BigDecimal.ZERO;
            return;
        }

        long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        total = calcularTotalHabitaciones(noches);
    }

    private BigDecimal calcularTotalHabitaciones(long noches) {
        return habitaciones.stream()
                .filter(habitacion -> habitacion != null)
                .map(habitacion -> habitacion.calcularCosto(noches))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
