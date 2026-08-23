package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.util.Collection;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;

public interface CuentaService {

    /** Retorna todas las cuentas registradas. */
    Collection<Cuenta> buscarTodas();

    /** Busca una cuenta por identificador. */
    Cuenta buscarPorId(Integer id);

    /** Crea o actualiza una cuenta. */
    Cuenta guardar(Cuenta cuenta);

    /** Elimina una cuenta por identificador. */
    void eliminar(Integer id);

    /** Agrega un servicio a una cuenta existente. */
    DetalleCuenta agregarServicio(Integer cuentaId, Servicio servicio, int cantidad);

    /** Registra el pago total de una cuenta. */
    Pago pagar(Integer cuentaId, BigDecimal monto, String metodoPago);
}
