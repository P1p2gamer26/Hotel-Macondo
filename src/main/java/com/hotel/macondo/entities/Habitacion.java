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
     * Sincroniza el tipo embebido con los datos comerciales de la habitacion.
     */
    public void sincronizarTipo() {
        if (tipoHabitacion == null) {
            tipoHabitacion = new TipoHabitacion();
        }
        tipoHabitacion.setNombre(nombre);
        tipoHabitacion.setDescripcion(descripcion);
        tipoHabitacion.setPrecioNoche(BigDecimal.valueOf(precio));
        tipoHabitacion.setCapacidadPersonas(capacidad);
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
        if (!"DISPONIBLE".equals(estado) || fechaInicio == null || fechaFin == null
                || !fechaInicio.isBefore(fechaFin)) {
            return false;
        }

        return true;
    }
}
