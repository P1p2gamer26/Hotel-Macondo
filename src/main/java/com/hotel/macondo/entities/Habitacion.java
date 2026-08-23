package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// lombok: getters, setters, toString, equals
@Data
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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas = new ArrayList<>();

    /**
     * Conserva el constructor usado por los datos de la interfaz actual.
     */
    public Habitacion(Integer id, String nombre, String etiqueta, String descripcion,
            long precio, int capacidad, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
        this.precio = precio;
        this.capacidad = capacidad;
        this.imagen = imagen;
        this.numero = String.valueOf(id);
        this.estado = "DISPONIBLE";
        this.piso = 1;
        this.tipoHabitacion = new TipoHabitacion(nombre, descripcion,
                BigDecimal.valueOf(precio), capacidad);
    }

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
     * Indica si no existe una reserva que se cruce con el periodo solicitado.
     */
    public boolean estaDisponible(LocalDate fechaInicio, LocalDate fechaFin) {
        if (!"DISPONIBLE".equals(estado) || fechaInicio == null || fechaFin == null
                || !fechaInicio.isBefore(fechaFin)) {
            return false;
        }

        return reservas.stream()
                .filter(reserva -> !"CANCELADA".equals(reserva.getEstado()))
                .noneMatch(reserva -> fechaInicio.isBefore(reserva.getFechaFin())
                        && fechaFin.isAfter(reserva.getFechaInicio()));
    }

    /**
     * Registra una reserva dentro del calendario de la habitacion.
     */
    public void agregarReserva(Reserva reserva) {
        if (reserva != null && !reservas.contains(reserva)) {
            reservas.add(reserva);
        }
    }
}
