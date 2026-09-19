package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@Builder
@Getter
@Setter
@ToString(exclude = { "cliente", "habitacion", "cuenta" })
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reserva {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String numeroReserva;

  @Column(nullable = false)
  private LocalDate fechaInicio;

  @Column(nullable = false)
  private LocalDate fechaFin;

  @Column(nullable = false)
  private Integer cantidadPersonas;

  @Column(nullable = false, length = 20)
  private String estado;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioNoche;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @ManyToOne
  private Cliente cliente;

  @ManyToOne
  private Habitacion habitacion;

  @OneToOne(mappedBy = "reserva")
  private Cuenta cuenta;

  public Reserva(
      String numeroReserva,
      LocalDate fechaInicio,
      LocalDate fechaFin,
      Integer cantidadPersonas,
      String estado,
      BigDecimal precioNoche,
      BigDecimal total) {
    this.numeroReserva = numeroReserva;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.cantidadPersonas = cantidadPersonas;
    this.estado = estado;
    this.precioNoche = precioNoche;
    this.total = total;
  }

  public void asignarCliente(Cliente cliente) {
    this.cliente = cliente;
    if (cliente != null && !cliente.getReservas().contains(this)) {
      cliente.getReservas().add(this);
    }
  }

  public void asignarCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
    if (cuenta != null && cuenta.getReserva() != this) {
      cuenta.asignarReserva(this);
    }
  }

  public void asignarHabitacion(Habitacion habitacion) {
    if (this.habitacion != null && this.habitacion != habitacion) {
      this.habitacion.getReservas().remove(this);
    }
    this.habitacion = habitacion;
    if (habitacion != null && !habitacion.getReservas().contains(this)) {
      habitacion.getReservas().add(this);
    }
  }
}
