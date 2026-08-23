package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.CuentaRepository;

@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repository;

    /**
     * Crea el servicio con su repositorio de cuentas.
     */
    @Autowired
    public CuentaServiceImpl(CuentaRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Cuenta> buscarTodas() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Cuenta buscarPorId(Integer id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return repository.save(cuenta);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }

    /** {@inheritDoc} */
    @Override
    public DetalleCuenta agregarServicio(Integer cuentaId, Servicio servicio, int cantidad) {
        Cuenta cuenta = repository.findById(cuentaId);
        return cuenta == null ? null : cuenta.agregarItem(servicio, cantidad);
    }

    /** {@inheritDoc} */
    @Override
    public Pago pagar(Integer cuentaId, BigDecimal monto, String metodoPago) {
        Cuenta cuenta = repository.findById(cuentaId);
        return cuenta == null ? null : cuenta.pagar(monto, metodoPago);
    }
}
