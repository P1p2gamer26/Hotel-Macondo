package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Testimonio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 2000)
  private String texto;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String ciudad;

  @Column(nullable = false)
  private int estrellas;

  @Column(length = 255)
  private String imagen;

  public Testimonio(String texto, String nombre, String ciudad, int estrellas, String imagen) {
    this.texto = texto;
    this.nombre = nombre;
    this.ciudad = ciudad;
    this.estrellas = estrellas;
    this.imagen = imagen;
  }
}
