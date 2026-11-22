package com.hotel.macondo.DTOs.Salida;

import com.hotel.macondo.entities.Promocion;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PromocionResponse(Long id, String codigo, String nombre, String descripcion,
    Promocion.TipoDescuento tipoDescuento, BigDecimal valorDescuento,
    LocalDate fechaInicio, LocalDate fechaFin, Integer nochesMinimas, boolean activa) {
  public static PromocionResponse desde(Promocion p) {
    return new PromocionResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
        p.getTipoDescuento(), p.getValorDescuento(), p.getFechaInicio(), p.getFechaFin(),
        p.getNochesMinimas(), p.isActiva());
  }
}
