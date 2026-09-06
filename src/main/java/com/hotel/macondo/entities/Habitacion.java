package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

  @Column(length = 2000)
  private String descripcion;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio = BigDecimal.ZERO;

  @Column(nullable = false)
  private int capacidad;

  @Column(length = 255)
  private String imagen;

  @Column(nullable = false, unique = true, length = 10)
  private String numero;

  @Column(nullable = false, length = 20)
  private String estado;

  private Integer piso;
  @ManyToOne
  private TipoHabitacion tipoHabitacion;

  @ManyToMany(mappedBy = "habitaciones")
  private List<Reserva> reservas = new ArrayList<>();

  public Habitacion(
      String nombre,
      String etiqueta,
      String descripcion,
      BigDecimal precio,
      int capacidad,
      String imagen,
      String numero,
      String estado,
      Integer piso) {
    this.nombre = nombre;
    this.etiqueta = etiqueta;
    this.descripcion = descripcion;
    this.precio = precio;
    this.capacidad = capacidad;
    this.imagen = imagen;
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
}
