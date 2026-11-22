package com.hotel.macondo.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class IntentoPagoWompi {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true, length = 80)
  private String referencia;
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal monto;
  @Column(nullable = false, length = 20)
  private String estado;
  @Column(length = 100)
  private String transaccionId;
  @Column(nullable = false)
  private LocalDateTime fechaCreacion;
  @ManyToOne(optional = false)
  private Cuenta cuenta;

  public IntentoPagoWompi(String referencia, BigDecimal monto, Cuenta cuenta) {
    this.referencia = referencia;
    this.monto = monto;
    this.cuenta = cuenta;
    this.estado = "PENDING";
    this.fechaCreacion = LocalDateTime.now();
  }
}
