package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@ToString(exclude = {"contrasena", "cliente", "operador"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String correo;

  @Column(nullable = false, length = 255)
  private String contrasena;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Rol rol;

  @JsonIgnore
  @OneToOne
  private Cliente cliente;

  @JsonIgnore
  @OneToOne(mappedBy = "usuario")
  private Operador operador;

  public Usuario(String correo, String contrasena, Rol rol) {
    this.correo = correo;
    this.contrasena = contrasena;
    this.rol = rol;
  }

  public void asignarCliente(Cliente cliente) {
    this.cliente = cliente;
    if (cliente != null && cliente.getUsuario() != this) cliente.asignarUsuario(this);
  }

  public void asignarOperador(Operador operador) {
    this.operador = operador;
    if (operador != null && operador.getUsuario() != this) operador.asignarUsuario(this);
  }

  public boolean iniciarSesion(String correo, String contrasena) {
    return this.correo != null
        && this.correo.equalsIgnoreCase(correo)
        && this.contrasena != null
        && this.contrasena.equals(contrasena);
  }

  public boolean tieneRol(Rol rolSolicitado) {
    return rol != null && rol == rolSolicitado;
  }

  public void actualizarCorreo(String correo) {
    this.correo = correo;
  }
}
