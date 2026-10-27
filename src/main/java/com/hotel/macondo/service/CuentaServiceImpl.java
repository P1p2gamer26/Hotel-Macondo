package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.dto.ConsumoServicioSolicitud;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.DetalleCuentaRepository;
import com.hotel.macondo.repository.PagoRepository;
import com.hotel.macondo.service.ServicioService;

@Service
@Transactional
public class CuentaServiceImpl implements CuentaService {

  @Autowired
  private CuentaRepository repository;

  @Autowired
  private DetalleCuentaRepository detalleCuentaRepository;

  @Autowired
  private PagoRepository pagoRepository;

  @Autowired
  private ServicioService servicioService;

  // ==========================================
  // CONSULTAS DE CUENTA
  // ==========================================

  // Busca una cuenta por su identificador
  @Override
  public Cuenta buscarPorId(Long id) {
    return repository.findById(id).orElse(null);
  }

  // ==========================================
  // CONSUMOS ADICIONALES (OPERADOR)
  // ==========================================

  // Carga un nuevo servicio a una cuenta que permanezca abierta
  // y recalcula el monto total acumulado
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

  // Valida todos los servicios solicitados antes de guardar alguno y luego los agrega a la cuenta
  @Override
  public void agregarServicios(Long cuentaId, List<ConsumoServicioSolicitud> solicitudes) {
    if (solicitudes == null || solicitudes.isEmpty()) {
      throw new FormularioErroneoException("Debes enviar al menos un servicio.");
    }

    Cuenta cuenta = repository.findById(cuentaId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada"));
    if (!"ABIERTA".equals(cuenta.getEstado())) {
      throw new FormularioErroneoException("La cuenta no está abierta.");
    }

    List<Servicio> servicios = new ArrayList<>();
    for (ConsumoServicioSolicitud solicitud : solicitudes) {
      if (solicitud == null
          || solicitud.servicioId() == null
          || solicitud.cantidad() == null
          || solicitud.cantidad() <= 0) {
        throw new FormularioErroneoException(
            "Cada servicio debe tener un identificador y una cantidad mayor que cero.");
      }

      Servicio servicio = servicioService.buscarPorIdIncluyendoInactivos(solicitud.servicioId());
      if (!servicio.isActivo()) {
        throw new FormularioErroneoException(
            "El servicio " + solicitud.servicioId() + " no está activo.");
      }
      servicios.add(servicio);
    }

    for (int i = 0; i < solicitudes.size(); i++) {
      ConsumoServicioSolicitud solicitud = solicitudes.get(i);
      DetalleCuenta detalle = agregarServicio(cuentaId, servicios.get(i), solicitud.cantidad());
      if (detalle == null) {
        throw new FormularioErroneoException(
            "No fue posible agregar el servicio " + solicitud.servicioId() + " a la cuenta.");
      }
    }
  }

  // Retira un consumo de servicio de una cuenta abierta
  // y descuenta su valor recalculando el monto global
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

  // ==========================================
  // PAGOS Y LIQUIDACIÓN (OPERADOR)
  // ==========================================

  // Registra el pago exacto de la cuenta y la deja PAGADA, conservando
  // los consumos y el total para el historial de la reserva.
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

    cuenta.setEstado("PAGADA");
    pagoRepository.save(pago);
    repository.save(cuenta);
    return pago;
  }

  // ==========================================
  // CÁLCULOS DE COSTOS Y SUBTOTALES (OPERADOR)
  // ==========================================

  // Calcula la sumatoria del costo de alojamiento mas los consumos de servicios
  private BigDecimal calcularTotal(Cuenta cuenta) {
    BigDecimal alojamiento = cuenta.getReserva() == null || cuenta.getReserva().getTotal() == null
        ? BigDecimal.ZERO
        : cuenta.getReserva().getTotal();
    BigDecimal servicios = cuenta.getDetalles().stream()
        .map(this::calcularSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    return alojamiento.add(servicios);
  }

  // Multiplica el precio unitario del servicio por la cantidad consumida
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