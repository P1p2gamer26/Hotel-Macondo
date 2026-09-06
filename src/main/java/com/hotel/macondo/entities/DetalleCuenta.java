package com.hotel.macondo.entities;

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
public class DetalleCuenta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  @Column(nullable = false)
  private LocalDateTime fechaRegistro;

  @ManyToOne
  private Cuenta cuenta;

  @ManyToOne
  private Servicio servicio;

  public DetalleCuenta(Integer cantidad, BigDecimal precio, LocalDateTime fechaRegistro) {
    this.cantidad = cantidad;
    this.precio = precio;
    this.fechaRegistro = fechaRegistro;
  }
}
