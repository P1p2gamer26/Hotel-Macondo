package com.hotel.macondo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"cuenta", "servicios"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DetalleCuenta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  @Column(nullable = false)
  private LocalDateTime fechaRegistro;

  @JsonIgnore
  @ManyToOne
  private Cuenta cuenta;

  @ManyToMany
  private List<Servicio> servicios = new ArrayList<>();

  public DetalleCuenta(Integer cantidad, BigDecimal precio, LocalDateTime fechaRegistro) {
    this.cantidad = cantidad;
    this.precio = precio;
    this.fechaRegistro = fechaRegistro;
  }

  public void asignarCuenta(Cuenta cuenta) {
    this.cuenta = cuenta;
    if (cuenta != null && !cuenta.getDetalles().contains(this)) cuenta.getDetalles().add(this);
  }

  public void asignarServicios(List<Servicio> servicios) {
    this.servicios.clear();
    if (servicios != null)
      servicios.stream()
          .filter(s -> s != null)
          .forEach(
              s -> {
                this.servicios.add(s);
                if (!s.getDetallesCuenta().contains(this)) s.getDetallesCuenta().add(this);
              });
  }

  public BigDecimal calcularSubtotal() {
    if ((precio == null && servicios.isEmpty()) || cantidad == null || cantidad <= 0)
      return BigDecimal.ZERO;
    BigDecimal precioServicios =
        servicios.stream()
            .filter(s -> s.getPrecio() != null)
            .map(Servicio::getPrecio)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal precioUnitario =
        precioServicios.compareTo(BigDecimal.ZERO) > 0
            ? precioServicios
            : (precio == null ? BigDecimal.ZERO : precio);
    return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
  }
}
