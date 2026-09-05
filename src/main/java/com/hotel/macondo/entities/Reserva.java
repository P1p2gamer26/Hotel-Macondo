package com.hotel.macondo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"cliente", "habitaciones", "cuenta"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reserva {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String numeroReserva;

  @Column(nullable = false)
  private LocalDate fechaInicio;

  @Column(nullable = false)
  private LocalDate fechaFin;

  @Column(nullable = false)
  private Integer cantidadPersonas;

  @Column(nullable = false, length = 20)
  private String estado;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @ManyToOne
  private Cliente cliente;

  @ManyToMany
  private List<Habitacion> habitaciones = new ArrayList<>();

  @OneToOne(mappedBy = "reserva")
  private Cuenta cuenta;

  public Reserva(
      String numeroReserva,
      LocalDate fechaInicio,
      LocalDate fechaFin,
      Integer cantidadPersonas,
      String estado,
      BigDecimal total) {
    this.numeroReserva = numeroReserva;
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
    this.cantidadPersonas = cantidadPersonas;
    this.estado = estado;
    this.total = total;
  }

  public void asignarCliente(Cliente cliente) {
    this.cliente = cliente;
    if (cliente != null && !cliente.getReservas().contains(this)) cliente.getReservas().add(this);
  }

  public void asignarCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
    if (cuenta != null && cuenta.getReserva() != this) cuenta.asignarReserva(this);
  }

  public void agregarHabitacion(Habitacion habitacion) {
    if (habitacion != null && !habitaciones.contains(habitacion)) {
      habitaciones.add(habitacion);
      if (!habitacion.getReservas().contains(this)) habitacion.getReservas().add(this);
      recalcularTotal();
    }
  }

  public boolean cancelar() {
    if (fechaInicio == null || !fechaInicio.isAfter(LocalDate.now()) || "CANCELADA".equals(estado))
      return false;
    estado = "CANCELADA";
    return true;
  }

  public boolean modificar(LocalDate inicio, LocalDate fin, TipoHabitacion tipo, int personas) {
    if (inicio == null
        || fin == null
        || !inicio.isBefore(fin)
        || tipo == null
        || personas <= 0
        || personas > tipo.getCapacidadPersonas()) return false;
    fechaInicio = inicio;
    fechaFin = fin;
    cantidadPersonas = personas;
    long noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
    total = habitaciones.isEmpty() ? tipo.calcularCosto(noches) : calcularTotalHabitaciones(noches);
    return true;
  }

  public void activar() {
    LocalDate hoy = LocalDate.now();
    if (fechaInicio != null
        && fechaFin != null
        && !hoy.isBefore(fechaInicio)
        && hoy.isBefore(fechaFin)) estado = "ACTIVA";
  }

  public boolean finalizar(boolean cuentaSaldada) {
    if (cuentaSaldada) {
      estado = "FINALIZADA";
      return true;
    }
    return false;
  }

  public boolean estaCancelada() {
    return "CANCELADA".equals(estado);
  }

  public boolean estaVigente() {
    return !estaCancelada() && fechaFin != null && !fechaFin.isBefore(LocalDate.now());
  }

  public boolean esHistorica() {
    return estaCancelada() || (fechaFin != null && fechaFin.isBefore(LocalDate.now()));
  }

  public boolean estaEnCurso() {
    return estadoEsUnoDe("ACTIVA", "CONFIRMADA", "PENDIENTE");
  }

  public boolean estaCerrada() {
    return estadoEsUnoDe("FINALIZADA", "CANCELADA");
  }

  private boolean estadoEsUnoDe(String... estados) {
    return estado != null && List.of(estados).contains(estado.toUpperCase());
  }

  private void recalcularTotal() {
    if (fechaInicio == null || fechaFin == null || !fechaInicio.isBefore(fechaFin)) {
      total = BigDecimal.ZERO;
      return;
    }
    total = calcularTotalHabitaciones(ChronoUnit.DAYS.between(fechaInicio, fechaFin));
  }

  private BigDecimal calcularTotalHabitaciones(long noches) {
    return habitaciones.stream()
        .filter(h -> h != null)
        .map(h -> h.calcularCosto(noches))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
