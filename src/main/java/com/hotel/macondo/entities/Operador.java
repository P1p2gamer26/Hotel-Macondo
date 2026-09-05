package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Operador {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false)
  private Boolean activo;

  @JsonIgnore
  @OneToOne
  private Usuario usuario;

  public Operador(String nombre, Boolean activo) {
    this.nombre = nombre;
    this.activo = activo;
  }


  public void asignarUsuario(Usuario usuario) {
    this.usuario = usuario;
    if (usuario != null && usuario.getOperador() != this) usuario.asignarOperador(this);
  }

  public boolean cancelarReserva(Reserva reserva) {
    return Boolean.TRUE.equals(activo) && reserva != null && reserva.cancelar();
  }

  public boolean agregarServicio(Cuenta cuenta, Servicio servicio, int cantidad) {
    return Boolean.TRUE.equals(activo)
        && cuenta != null
        && cuenta.agregarItem(servicio, cantidad) != null;
  }

  public boolean realizarCheckout(Reserva reserva, Cuenta cuenta) {
    return Boolean.TRUE.equals(activo)
        && reserva != null
        && cuenta != null
        && cuenta.estaSaldada()
        && reserva.finalizar(cuenta.estaSaldada());
  }
}
