package com.hotel.macondo.DTOs.Salida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Información de estadía y cuenta visible únicamente para el titular. */
public record ReservaDetalleClienteResponse(
    Long id,
    String numeroReserva,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Integer cantidadPersonas,
    String estado,
    BigDecimal precioNoche,
    BigDecimal total,
    String habitacion,
    String tipoHabitacion,
    CuentaDetalle cuenta) {

  public record CuentaDetalle(
      Long id,
      String estado,
      BigDecimal total,
      BigDecimal saldo,
      LocalDateTime fechaApertura,
      List<ConsumoDetalle> consumos,
      List<PagoDetalle> pagos) {}

  public record ConsumoDetalle(
      Long id, Integer cantidad, BigDecimal precio, LocalDateTime fechaRegistro, String servicio) {}

  public record PagoDetalle(
      Long id, BigDecimal monto, String metodoPago, LocalDateTime fechaPago, String estado) {}
}
