package com.hotel.macondo.entities;

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
import lombok.Builder;

@Builder
@Getter
@Setter
@ToString(exclude = { "contrasena", "admin", "cliente", "operador" })
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

  // relacion 1 a 1 controlada por logica de servicio, para evistar que un borrado
  // directo en usuario destruya el cliente
  @OneToOne
  private Cliente cliente;

  @OneToOne(mappedBy = "usuario")
  private Operador operador;

  @OneToOne(mappedBy = "usuario")
  private Admin admin;

  public Usuario(String correo, String contrasena, Rol rol) {
    this.correo = correo;
    this.contrasena = contrasena;
    this.rol = rol;
  }

  public void asignarCliente(Cliente cliente) {
    this.cliente = cliente;
    if (cliente != null && cliente.getUsuario() != this) {
      cliente.asignarUsuario(this);
    }
  }

  public void asignarOperador(Operador operador) {
    this.operador = operador;
    if (operador != null && operador.getUsuario() != this) {
      operador.asignarUsuario(this);
    }
  }

  public void asignarAdmin(Admin admin) {
    this.admin = admin;
    if (admin != null && admin.getUsuario() != this) {
      admin.asignarUsuario(this);
    }
  }
}
