package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@Builder
@Getter
@Setter
@ToString(exclude = { "tipoHabitacion", "reservas" })
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Habitacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(length = 100)
  private String etiqueta;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio = BigDecimal.ZERO;

  @Column(nullable = false)
  private int capacidad;

  @Column(nullable = false, unique = true, length = 10)
  private String numero;

  @Column(nullable = false, length = 20)
  private String estado;

  private Integer piso;
  @ManyToOne
  private TipoHabitacion tipoHabitacion;

  @OneToMany(mappedBy = "habitacion")
  private List<Reserva> reservas = new ArrayList<>();

  public Habitacion(
      String nombre,
      String etiqueta,
      BigDecimal precio,
      int capacidad,
      String numero,
      String estado,
      Integer piso) {
    this.nombre = nombre;
    this.etiqueta = etiqueta;
    this.precio = precio;
    this.capacidad = capacidad;
    this.numero = numero;
    this.estado = estado;
    this.piso = piso;
  }

  public void aplicarTipo(TipoHabitacion tipo) {
    if (this.tipoHabitacion != null && this.tipoHabitacion != tipo) {
      this.tipoHabitacion.getHabitaciones().remove(this);
    }
    this.tipoHabitacion = tipo;
    if (tipo != null && !tipo.getHabitaciones().contains(this)) {
      tipo.getHabitaciones().add(this);
    }
  }

  public List<Reserva> getReservas() {
    if (this.reservas == null) {
      this.reservas = new ArrayList<>();
    }
    return this.reservas;
  }
}
