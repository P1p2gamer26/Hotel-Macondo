package com.hotel.macondo.entities;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@ToString(exclude = { "reserva", "detalles", "pagos" })
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

  // La cuenta no existe sin su reserva: al borrar la reserva se borra la cuenta.
  @OneToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Reserva reserva;

  @OneToMany(mappedBy = "cuenta")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<DetalleCuenta> detalles = new ArrayList<>();

  // Lado inverso de Pago.cuenta: sin el, borrar una cuenta dejaba pagos huerfanos.
  @OneToMany(mappedBy = "cuenta")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<Pago> pagos = new ArrayList<>();

  public Cuenta(String estado, BigDecimal total, LocalDateTime fechaApertura) {
    this.estado = estado;
    this.total = total;
    this.fechaApertura = fechaApertura;
  }

  public void asignarReserva(Reserva reserva) {
    this.reserva = reserva;
    if (reserva != null && reserva.getCuenta() != this) {
      reserva.asignarCuenta(this);
    }
  }

  public void agregarDetalle(DetalleCuenta detalle) {
    if (detalle != null && !detalles.contains(detalle)) {
      detalles.add(detalle);
      detalle.setCuenta(this);
    }
  }

  public void agregarPago(Pago pago) {
    if (pago != null && !pagos.contains(pago)) {
      pagos.add(pago);
      pago.setCuenta(this);
    }
  }
}
