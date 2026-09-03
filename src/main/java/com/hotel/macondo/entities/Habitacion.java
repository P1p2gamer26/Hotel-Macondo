package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// lombok: getters, setters, toString, equals
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    private Integer id;
    private String nombre;
    private String etiqueta;
    private String descripcion;
    private long precio;
    private int capacidad;
    private String imagen;
    private String numero;
    private String estado;
    private Integer piso;
    private TipoHabitacion tipoHabitacion;

    /**
     * Crea una habitacion a partir de los atributos definidos por el dominio.
     */
    public Habitacion(String numero, String estado, Integer piso,
            TipoHabitacion tipoHabitacion) {
        this.numero = numero;
        this.estado = estado;
        this.piso = piso;
        this.tipoHabitacion = tipoHabitacion;
        this.nombre = tipoHabitacion.getNombre();
        this.descripcion = tipoHabitacion.getDescripcion();
        this.precio = tipoHabitacion.getPrecioNoche().longValue();
        this.capacidad = tipoHabitacion.getCapacidadPersonas();
    }

    /**
     * Aplica un tipo de habitacion a esta habitacion: guarda la referencia y
     * deriva la descripcion, el precio y la capacidad directamente del tipo.
     * El nombre NO se sobrescribe: es un dato comercial independiente que el
     * administrador edita por separado (dos conceptos distintos).
     */
    public void aplicarTipo(TipoHabitacion tipo) {
        if (tipo == null) {
            return;
        }
        this.tipoHabitacion = tipo;
        this.descripcion = tipo.getDescripcion();
        this.precio = tipo.getPrecioNoche() == null ? 0L
                : tipo.getPrecioNoche().longValue();
        this.capacidad = tipo.getCapacidadPersonas() == null ? 0
                : tipo.getCapacidadPersonas();
    }

    /**
     * Deshabilita temporalmente la habitacion.
     */
    public void deshabilitar() {
        estado = "NO_DISPONIBLE";
    }

    /**
     * Habilita la habitacion para recibir nuevas reservas.
     */
    public void habilitar() {
        estado = "DISPONIBLE";
    }

    /**
     * Indica si la habitacion esta habilitada para recibir reservas. El
     * significado del estado vive aqui y no en las capas que lo consultan.
     */
    public boolean estaHabilitada() {
        return "DISPONIBLE".equals(estado);
    }

    /**
     * Calcula el costo de la habitacion para una cantidad de noches.
     */
    public BigDecimal calcularCosto(long noches) {
        return tipoHabitacion == null ? BigDecimal.ZERO : tipoHabitacion.calcularCosto(noches);
    }

    /**
     * Indica si la habitacion esta habilitada para el periodo solicitado.
     *
     * La validacion de cruces entre reservas pertenece al servicio de reservas:
     * la habitacion no mantiene una referencia inversa a ellas.
     */
    public boolean estaDisponible(LocalDate fechaInicio, LocalDate fechaFin) {
        if (!estaHabilitada() || fechaInicio == null || fechaFin == null
                || !fechaInicio.isBefore(fechaFin)) {
            return false;
        }

        return true;
    }
}
