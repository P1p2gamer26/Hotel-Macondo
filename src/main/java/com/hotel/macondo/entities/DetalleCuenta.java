package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCuenta {

    private Integer id;
    private Integer cantidad;
    private BigDecimal precio;
    private LocalDateTime fechaRegistro;
    private List<Servicio> servicios = new ArrayList<>();

    /**
     * Conserva la construccion de un detalle individual como un caso de un solo
     * servicio dentro de la coleccion del detalle.
     */
    public DetalleCuenta(Integer id, Integer cantidad, BigDecimal precio,
            LocalDateTime fechaRegistro, Servicio servicio) {
        this(id, cantidad, precio, fechaRegistro,
                servicio == null ? List.of() : List.of(servicio));
    }

    /**
     * Calcula el valor total de este item de cuenta.
     */
    public BigDecimal calcularSubtotal() {
        if ((precio == null && servicios.isEmpty()) || cantidad == null || cantidad <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal precioServicios = servicios.stream()
                .filter(servicio -> servicio != null && servicio.getPrecio() != null)
                .map(Servicio::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal precioUnitario = precioServicios.compareTo(BigDecimal.ZERO) > 0
                ? precioServicios
                : (precio == null ? BigDecimal.ZERO : precio);
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
