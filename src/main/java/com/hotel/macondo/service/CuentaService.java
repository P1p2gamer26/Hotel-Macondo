package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.dto.ConsumoServicioSolicitud;

public interface CuentaService {

    /** Busca una cuenta por identificador. */
    Cuenta buscarPorId(Long id);

    /** Agrega un servicio a una cuenta existente. */
    DetalleCuenta agregarServicio(Long cuentaId, Servicio servicio, int cantidad);

    /** Agrega varios servicios a una cuenta como una sola operación. */
    void agregarServicios(Long cuentaId, List<ConsumoServicioSolicitud> servicios);

    /** Elimina un consumo de una cuenta abierta. */
    boolean eliminarDetalle(Long cuentaId, Long detalleId);

    /** Registra el pago del saldo pendiente de una cuenta. */
    Pago pagar(Long cuentaId, BigDecimal monto, String metodoPago);

    /** Total de la cuenta menos los pagos ya registrados. */
    BigDecimal calcularSaldo(Cuenta cuenta);
}
