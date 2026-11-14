package com.hotel.macondo.DTOs.Salida;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HabitacionDTO {
    private Long id;
    private String nombre;
    private String etiqueta;
    private BigDecimal precio;
    private int capacidad;
    private String numero;
    private String estado;
    private Integer piso;
    private TipoHabitacionSummaryDTO tipoHabitacion;

    @Data
    public static class TipoHabitacionSummaryDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String imagen;
        private BigDecimal precioNoche;
        private Integer capacidadPersonas;
    }
}