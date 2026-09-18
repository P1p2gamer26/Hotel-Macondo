package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

@Getter
@Setter
@ToString(exclude = "detalles")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Servicio {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 2000)
  private String descripcion;

  @Column(nullable = false, length = 100)
  private String categoria;

  @Column(length = 255)
  private String imagen;

  @Column(nullable = false)
  private boolean destacado;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  @Column(nullable = false)
  private boolean activo;

  @Column(length = 100)
  private String duracion;

  @Column(length = 2000)
  private String descripcionDetalle;

  @Column(length = 100)
  private String horario;

  @ElementCollection
  private List<String> incluidos = new ArrayList<>();

  @ElementCollection
  private List<String> etiquetas = new ArrayList<>();

  @OneToMany(mappedBy = "servicio")
  private List<DetalleCuenta> detalles = new ArrayList<>();

  public Servicio(
      String nombre,
      String descripcion,
      String categoria,
      String imagen,
      boolean destacado,
      BigDecimal precio,
      boolean activo,
      String duracion,
      String descripcionDetalle,
      String horario,
      List<String> incluidos,
      List<String> etiquetas) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.imagen = imagen;
    this.destacado = destacado;
    this.precio = precio;
    this.activo = activo;
    this.duracion = duracion;
    this.descripcionDetalle = descripcionDetalle;
    this.horario = horario;
    this.incluidos = incluidos == null ? new ArrayList<>() : new ArrayList<>(incluidos);
    this.etiquetas = etiquetas == null ? new ArrayList<>() : new ArrayList<>(etiquetas);
  }

}
