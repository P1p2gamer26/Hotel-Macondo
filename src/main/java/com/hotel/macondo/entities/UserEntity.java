package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString(exclude = { "contrasena", "admin", "cliente", "operador" })
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String correo;

  @Column(nullable = false, length = 255)
  private String contrasena;

  @Builder.Default
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private List<Rol> roles = new ArrayList<>();

  // relacion 1 a 1 controlada por logica de servicio, para evitar que un borrado
  // directo en usuario destruya el cliente
  @JsonIgnore
  @OneToOne(mappedBy = "usuario")
  private Cliente cliente;

  @JsonIgnore
  @OneToOne(mappedBy = "usuario")
  private Operador operador;

  @JsonIgnore
  @OneToOne(mappedBy = "usuario")
  private Admin admin;

  public UserEntity(String correo, String contrasena, List<Rol> roles) {
    this.correo = correo;
    this.contrasena = contrasena;
    this.roles = roles != null ? new ArrayList<>(roles) : new ArrayList<>();
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