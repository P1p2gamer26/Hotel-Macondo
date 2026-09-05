package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"reserva", "detalles", "pagos"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cuenta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String estado = "ABIERTA";

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total = BigDecimal.ZERO;

  @Column(nullable = false)
  private LocalDateTime fechaApertura = LocalDateTime.now();

  @OneToOne
  private Reserva reserva;

  @JsonIgnore
  @OneToMany(mappedBy = "cuenta")
  private List<DetalleCuenta> detalles = new ArrayList<>();

  @JsonIgnore
  @OneToMany(mappedBy = "cuenta")
  private List<Pago> pagos = new ArrayList<>();

  public Cuenta(String estado, BigDecimal total, LocalDateTime fechaApertura) {
    this.estado = estado;
    this.total = total;
    this.fechaApertura = fechaApertura;
  }


  public void asignarReserva(Reserva reserva) {
    this.reserva = reserva;
    if (reserva != null && reserva.getCuenta() != this) reserva.asignarCuenta(this);
  }

  public DetalleCuenta agregarItem(Servicio servicio, int cantidad) {
    return agregarItem(servicio == null ? List.of() : List.of(servicio), cantidad);
  }

  public DetalleCuenta agregarItem(List<Servicio> servicios, int cantidad) {
    if (!"ABIERTA".equals(estado)
        || servicios == null
        || servicios.isEmpty()
        || cantidad <= 0
        || servicios.stream().anyMatch(s -> s == null || !s.isActivo() || s.getPrecio() == null))
      return null;
    BigDecimal precio =
        servicios.stream().map(Servicio::getPrecio).reduce(BigDecimal.ZERO, BigDecimal::add);
    DetalleCuenta detalle = new DetalleCuenta(cantidad, precio, LocalDateTime.now());
    detalle.asignarServicios(servicios);
    detalle.asignarCuenta(this);
    recalcularTotal();
    return detalle;
  }

  public boolean eliminarItem(Long detalleId) {
    boolean eliminado = detalles.removeIf(d -> Objects.equals(d.getId(), detalleId));
    if (eliminado) recalcularTotal();
    return eliminado;
  }

  public Pago pagar(BigDecimal monto, String metodoPago) {
    if (!"ABIERTA".equals(estado) || monto == null || monto.compareTo(total) < 0) return null;
    Pago pago = new Pago(monto, metodoPago, LocalDateTime.now(), "PENDIENTE");
    pago.asignarCuenta(this);
    detalles.clear();
    total = BigDecimal.ZERO;
    estado = "PAGADA";
    return pago;
  }

  public boolean estaSaldada() {
    return total != null && total.compareTo(BigDecimal.ZERO) == 0;
  }

  private void recalcularTotal() {
    total =
        detalles.stream()
            .map(DetalleCuenta::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
