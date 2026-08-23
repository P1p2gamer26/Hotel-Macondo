package com.hotel.macondo.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class Cuenta {

    private Integer id;
    private String estado = "ABIERTA";
    private BigDecimal total = BigDecimal.ZERO;
    private LocalDateTime fechaApertura = LocalDateTime.now();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    private List<DetalleCuenta> detalles = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();

    /**
     * Crea una cuenta abierta asociada a una reserva.
     */
    public Cuenta(Integer id, Reserva reserva) {
        this.id = id;
        this.reserva = reserva;
    }

    /**
     * Agrega un servicio activo y recalcula el total de la cuenta.
     */
    public DetalleCuenta agregarItem(Servicio servicio, int cantidad) {
        if (!"ABIERTA".equals(estado) || servicio == null || !servicio.isActivo()
                || servicio.getPrecio() == null || cantidad <= 0) {
            return null;
        }

        DetalleCuenta detalle = new DetalleCuenta(detalles.size() + 1, cantidad,
                servicio.getPrecio(), LocalDateTime.now(), servicio);
        detalles.add(detalle);
        recalcularTotal();
        return detalle;
    }

    /**
     * Elimina un item de la cuenta y actualiza el saldo.
     */
    public boolean eliminarItem(Integer detalleId) {
        boolean eliminado = detalles.removeIf(detalle -> Objects.equals(detalle.getId(), detalleId));
        if (eliminado) {
            recalcularTotal();
        }
        return eliminado;
    }

    /**
     * Registra el pago total de la cuenta y libera sus items pendientes.
     */
    public Pago pagar(BigDecimal monto, String metodoPago) {
        if (!"ABIERTA".equals(estado) || monto == null || monto.compareTo(total) < 0) {
            return null;
        }

        Pago pago = new Pago(pagos.size() + 1, monto, metodoPago,
                LocalDateTime.now(), "PENDIENTE");
        pago.confirmar();
        pagos.add(pago);
        detalles.clear();
        total = BigDecimal.ZERO;
        estado = "PAGADA";
        return pago;
    }

    /**
     * Indica si la cuenta no tiene valores pendientes.
     */
    public boolean estaSaldada() {
        return total != null && total.compareTo(BigDecimal.ZERO) == 0;
    }

    private void recalcularTotal() {
        total = detalles.stream()
                .map(DetalleCuenta::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
