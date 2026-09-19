package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.DetalleCuentaRepository;
import com.hotel.macondo.repository.PagoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

  @Autowired
  private CuentaRepository repository;
  @Autowired
  private DetalleCuentaRepository detalleCuentaRepository;
  @Autowired
  private PagoRepository pagoRepository;

  /** {@inheritDoc} */
  @Override
  public Collection<Cuenta> buscarTodas() {
    return repository.findAllByOrderByIdAsc();
  }

  /** {@inheritDoc} */
  @Override
  public Cuenta buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public Cuenta guardar(Cuenta cuenta) {
    return repository.save(cuenta);
  }

  /** {@inheritDoc} */
  @Override
  public void eliminar(Long id) {
    repository.deleteById(id);
  }

  /** {@inheritDoc} */
  @Override
  public DetalleCuenta agregarServicio(
      Long cuentaId, Servicio servicio, int cantidad) {
    Cuenta cuenta = repository.findById(cuentaId).orElse(null);
    if (cuenta == null
        || !"ABIERTA".equals(cuenta.getEstado())
        || servicio == null
        || !servicio.isActivo()
        || servicio.getPrecio() == null
        || cantidad <= 0) {
      return null;
    }

    DetalleCuenta detalle = new DetalleCuenta(cantidad, servicio.getPrecio(), LocalDateTime.now());
    detalle.setServicio(servicio);
    cuenta.agregarDetalle(detalle);

    detalleCuentaRepository.save(detalle);
    cuenta.setTotal(calcularTotal(cuenta));
    repository.save(cuenta);
    return detalle;
  }

  /** {@inheritDoc} */
  @Override
  public boolean eliminarDetalle(Long cuentaId, Long detalleId) {
    Cuenta cuenta = repository.findById(cuentaId).orElse(null);
    if (cuenta == null || !"ABIERTA".equals(cuenta.getEstado())) {
      return false;
    }

    DetalleCuenta detalle = detalleCuentaRepository.findByIdAndCuentaId(detalleId, cuentaId).orElse(null);
    if (detalle == null) {
      return false;
    }

    cuenta.getDetalles().removeIf(item -> Objects.equals(item.getId(), detalleId));
    detalleCuentaRepository.delete(detalle);
    cuenta.setTotal(calcularTotal(cuenta));
    repository.save(cuenta);
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Pago pagar(Long cuentaId, BigDecimal monto, String metodoPago) {
    Cuenta cuenta = repository.findById(cuentaId).orElse(null);
    if (cuenta == null
        || !"ABIERTA".equals(cuenta.getEstado())
        || cuenta.getTotal() == null
        || cuenta.getTotal().compareTo(BigDecimal.ZERO) <= 0
        || monto == null
        || monto.compareTo(cuenta.getTotal()) != 0
        || metodoPago == null
        || metodoPago.isBlank()) {
      return null;
    }

    Pago pago = new Pago(monto, metodoPago, LocalDateTime.now(), "CONFIRMADO");
    cuenta.agregarPago(pago);

    detalleCuentaRepository.deleteAllByCuentaId(cuentaId);
    cuenta.getDetalles().clear();
    cuenta.setTotal(BigDecimal.ZERO);
    cuenta.setEstado("PAGADA");
    pagoRepository.save(pago);
    repository.save(cuenta);
    return pago;
  }

  private BigDecimal calcularTotal(Cuenta cuenta) {
    BigDecimal alojamiento = cuenta.getReserva() == null || cuenta.getReserva().getTotal() == null
        ? BigDecimal.ZERO
        : cuenta.getReserva().getTotal();
    BigDecimal servicios = cuenta.getDetalles().stream()
        .map(this::calcularSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    return alojamiento.add(servicios);
  }

  private BigDecimal calcularSubtotal(DetalleCuenta detalle) {
    if (detalle == null
        || detalle.getPrecio() == null
        || detalle.getCantidad() == null
        || detalle.getCantidad() <= 0) {
      return BigDecimal.ZERO;
    }
    return detalle.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
  }
}
