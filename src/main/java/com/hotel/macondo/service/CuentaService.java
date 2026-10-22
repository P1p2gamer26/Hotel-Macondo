package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.util.Collection;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;

public interface CuentaService {

    /** Busca una cuenta por identificador. */
    Cuenta buscarPorId(Long id);

    /** Agrega un servicio a una cuenta existente. */
    DetalleCuenta agregarServicio(Long cuentaId, Servicio servicio, int cantidad);

    /** Elimina un consumo de una cuenta abierta. */
    boolean eliminarDetalle(Long cuentaId, Long detalleId);

    /** Registra el pago total de una cuenta. */
    Pago pagar(Long cuentaId, BigDecimal monto, String metodoPago);
}
