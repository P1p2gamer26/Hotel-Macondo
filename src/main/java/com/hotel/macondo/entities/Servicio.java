package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Servicio {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagen;
    private boolean destacado;
    private BigDecimal precio;
    private boolean activo;
    private String categoria;
    private String duracion;
    private String descripcionDetalle;
    private String horario;
    private List<String> incluidos = new ArrayList<>();
    private List<String> etiquetas = new ArrayList<>();

    /**
     * Conserva el constructor usado por la pagina principal actual.
     */
    public Servicio(Integer id, String nombre, String descripcion, String imagen,
            boolean destacado) {
        this(id, nombre, descripcion, imagen, destacado, BigDecimal.ZERO, true);
    }

    /**
     * Crea un servicio con sus datos comerciales completos.
     */
    public Servicio(Integer id, String nombre, String descripcion, String imagen,
            boolean destacado, BigDecimal precio, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.destacado = destacado;
        this.precio = precio;
        this.activo = activo;
    }

    /**
     * Actualiza la informacion editable del servicio.
     */
    public void actualizarDatos(String nombre, String descripcion, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    /**
     * Impide que el servicio sea agregado a nuevas cuentas.
     */
    public void desactivar() {
        activo = false;
    }
}
