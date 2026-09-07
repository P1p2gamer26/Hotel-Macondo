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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@ToString(exclude = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Operador {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false)
  private Boolean activo;

  // El perfil de operador muere con su usuario.
  @OneToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Usuario usuario;

  public Operador(String nombre, Boolean activo) {
    this.nombre = nombre;
    this.activo = activo;
  }

  public void asignarUsuario(Usuario usuario) {
    this.usuario = usuario;
    if (usuario != null && usuario.getOperador() != this) {
      usuario.asignarOperador(this);
    }
  }
}
