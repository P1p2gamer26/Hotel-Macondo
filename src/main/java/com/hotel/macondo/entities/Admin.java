package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @OneToOne
  private Usuario usuario;

  public Admin(String nombre) {
    this.nombre = nombre;
  }

  public void asignarUsuario(Usuario usuario) {
    this.usuario = usuario;
    if (usuario != null && usuario.getAdmin() != this) {
      usuario.asignarAdmin(this);
    }
  }
}
