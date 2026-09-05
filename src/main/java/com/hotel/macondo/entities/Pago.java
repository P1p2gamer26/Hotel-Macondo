package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "cuenta")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pago {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal monto;

  @Column(nullable = false, length = 50)
  private String metodoPago;

  @Column(nullable = false)
  private LocalDateTime fechaPago;

  @Column(nullable = false, length = 20)
  private String estado;

  @JsonIgnore
  @ManyToOne
  private Cuenta cuenta;

  public Pago(BigDecimal monto, String metodoPago, LocalDateTime fechaPago, String estado) {
    this.monto = monto;
    this.metodoPago = metodoPago;
    this.fechaPago = fechaPago;
    this.estado = estado;
  }

  public void asignarCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
    if (cuenta != null && !cuenta.getPagos().contains(this)) cuenta.getPagos().add(this);
  }

  public void confirmar() {
    estado = "CONFIRMADO";
  }

  public void rechazar() {
    estado = "RECHAZADO";
  }
}
