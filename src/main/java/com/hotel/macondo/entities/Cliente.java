package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"reservas", "usuario"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cliente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String apellido;

  @Column(nullable = false, unique = true, length = 20)
  private String cedula;

  @Column(nullable = false, length = 20)
  private String telefono;

  @Column(nullable = false, unique = true, length = 255)
  private String correo;

  @JsonIgnore
  @OneToMany(mappedBy = "cliente")
  private List<Reserva> reservas = new ArrayList<>();

  @JsonIgnore
  @OneToOne(mappedBy = "cliente")
  private Usuario usuario;

  public Cliente(String nombre, String apellido, String cedula, String telefono, String correo) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.cedula = cedula;
    this.telefono = telefono;
    this.correo = correo;
  }

  public void actualizarInformacion(
      String nombre, String apellido, String telefono, String correo) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.telefono = telefono;
    this.correo = correo;
  }

  public void agregarReserva(Reserva reserva) {
    if (reserva != null && !reservas.contains(reserva)) {
      reservas.add(reserva);
      reserva.asignarCliente(this);
    }
  }

  public void asignarUsuario(Usuario usuario) {
    this.usuario = usuario;
    if (usuario != null && usuario.getCliente() != this) usuario.asignarCliente(this);
  }

  public List<Reserva> verReservasActivas() {
    return reservas.stream().filter(Reserva::estaVigente).toList();
  }

  public List<Reserva> verHistorialReservas() {
    return reservas.stream().filter(Reserva::esHistorica).toList();
  }
}
