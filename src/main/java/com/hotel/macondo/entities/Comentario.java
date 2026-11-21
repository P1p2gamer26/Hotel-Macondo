package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comentario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(optional = false)
  private Cliente cliente;

  // OneToOne: solo un comentario por reserva (respaldo de la regla del servicio)
  @JsonIgnore
  @OneToOne(optional = false)
  private Reserva reserva;

  @Column(nullable = false)
  private int calificacion;

  @Column(nullable = false, length = 500)
  private String texto;

  @Column(nullable = false)
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @Column(nullable = false)
  private boolean publicado = false;
}
