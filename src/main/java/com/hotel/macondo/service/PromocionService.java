package com.hotel.macondo.service;

import com.hotel.macondo.DTOs.Salida.PromocionAplicadaResponse;
import com.hotel.macondo.entities.Promocion;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.repository.PromocionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PromocionService {
  private final PromocionRepository repository;

  public PromocionService(PromocionRepository repository) { this.repository = repository; }

  @Transactional(readOnly = true)
  public List<Promocion> listarAdmin() { return repository.findAllByOrderByIdDesc(); }

  @Transactional(readOnly = true)
  public List<Promocion> listarPublicas() {
    LocalDate hoy = LocalDate.now();
    return repository.findAllByOrderByIdDesc().stream()
        .filter(p -> p.isActiva() && !hoy.isBefore(p.getFechaInicio()) && !hoy.isAfter(p.getFechaFin()))
        .toList();
  }

  public Promocion guardar(Promocion p) {
    if (p.getCodigo() == null || p.getCodigo().isBlank() || p.getNombre() == null || p.getNombre().isBlank()
        || p.getTipoDescuento() == null || p.getValorDescuento() == null
        || p.getFechaInicio() == null || p.getFechaFin() == null
        || p.getFechaFin().isBefore(p.getFechaInicio())
        || p.getNochesMinimas() == null || p.getNochesMinimas() < 1
        || p.getValorDescuento().signum() <= 0
        || (p.getTipoDescuento() == Promocion.TipoDescuento.PORCENTAJE
            && p.getValorDescuento().compareTo(BigDecimal.valueOf(100)) > 0)) {
      throw new FormularioErroneoException("Revisa los datos de la promoción.");
    }
    p.setCodigo(p.getCodigo().trim().toUpperCase());
    repository.findByCodigoIgnoreCase(p.getCodigo()).ifPresent(existente -> {
      if (p.getId() == null || !existente.getId().equals(p.getId()))
        throw new FormularioErroneoException("Ya existe una promoción con ese código.");
    });
    return repository.save(p);
  }

  public void eliminar(Long id) { repository.deleteById(id); }

  @Transactional(readOnly = true)
  public PromocionAplicadaResponse calcular(String codigo, LocalDate entrada, LocalDate salida, BigDecimal precioNoche) {
    if (codigo == null || codigo.isBlank()) return new PromocionAplicadaResponse(null, null, subtotal(precioNoche, entrada, salida), BigDecimal.ZERO, subtotal(precioNoche, entrada, salida));
    Promocion p = repository.findByCodigoIgnoreCase(codigo.trim())
        .orElseThrow(() -> new FormularioErroneoException("El código promocional no existe."));
    LocalDate hoy = LocalDate.now();
    if (!p.isActiva() || hoy.isBefore(p.getFechaInicio()) || hoy.isAfter(p.getFechaFin()))
      throw new FormularioErroneoException("Esta promoción no está vigente.");
    long noches = java.time.temporal.ChronoUnit.DAYS.between(entrada, salida);
    if (noches < p.getNochesMinimas())
      throw new FormularioErroneoException("Esta promoción requiere mínimo " + p.getNochesMinimas() + " noches.");
    BigDecimal base = subtotal(precioNoche, entrada, salida);
    BigDecimal descuento = p.getTipoDescuento() == Promocion.TipoDescuento.PORCENTAJE
        ? base.multiply(p.getValorDescuento()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        : p.getValorDescuento().min(base);
    return new PromocionAplicadaResponse(p.getCodigo(), p.getNombre(), base, descuento, base.subtract(descuento));
  }

  private BigDecimal subtotal(BigDecimal precio, LocalDate entrada, LocalDate salida) {
    if (entrada == null || salida == null || precio == null || !salida.isAfter(entrada)) return BigDecimal.ZERO;
    return precio.multiply(BigDecimal.valueOf(java.time.temporal.ChronoUnit.DAYS.between(entrada, salida))).setScale(2, RoundingMode.HALF_UP);
  }
}
