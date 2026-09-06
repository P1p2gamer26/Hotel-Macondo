package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@ToString(exclude = "habitaciones")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TipoHabitacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String nombre;

  @Column(nullable = false, length = 2000)
  private String descripcion;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioNoche;

  @Column(nullable = false)
  private Integer capacidadPersonas;

  @OneToMany(mappedBy = "tipoHabitacion")
  private List<Habitacion> habitaciones = new ArrayList<>();

  public TipoHabitacion(
      String nombre, String descripcion, BigDecimal precioNoche, Integer capacidadPersonas) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.precioNoche = precioNoche;
    this.capacidadPersonas = capacidadPersonas;
  }

  public List<Habitacion> getHabitaciones() {
    if (this.habitaciones == null) {
      this.habitaciones = new ArrayList<>();
    }
    return this.habitaciones;
  }
}
