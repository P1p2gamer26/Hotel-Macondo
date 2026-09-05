package com.hotel.macondo.service;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.DetalleCuentaRepository;
import com.hotel.macondo.repository.PagoRepository;
import java.math.BigDecimal;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

  private final CuentaRepository repository;
  private final DetalleCuentaRepository detalleCuentaRepository;
  private final PagoRepository pagoRepository;

  public CuentaServiceImpl(
      CuentaRepository repository,
      DetalleCuentaRepository detalleCuentaRepository,
      PagoRepository pagoRepository) {
    this.repository = repository;
    this.detalleCuentaRepository = detalleCuentaRepository;
    this.pagoRepository = pagoRepository;
  }

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
    if (cuenta == null) {
      return null;
    }

    DetalleCuenta detalle = cuenta.agregarItem(servicio, cantidad);
    if (detalle == null) {
      return null;
    }

    detalleCuentaRepository.save(detalle);
    repository.save(cuenta);
    return detalle;
  }

  /** {@inheritDoc} */
  @Override
  public Pago pagar(Long cuentaId, BigDecimal monto, String metodoPago) {
    Cuenta cuenta = repository.findById(cuentaId).orElse(null);
    if (cuenta == null) {
      return null;
    }

    Pago pago = cuenta.pagar(monto, metodoPago);
    if (pago == null) {
      return null;
    }

    detalleCuentaRepository.deleteAllByCuentaId(cuentaId);
    pagoRepository.save(pago);
    repository.save(cuenta);
    return pago;
  }
}
