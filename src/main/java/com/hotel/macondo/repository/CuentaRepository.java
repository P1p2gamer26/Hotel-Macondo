package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Cuenta;

@Repository
public class CuentaRepository {

    private final Map<Integer, Cuenta> data = new LinkedHashMap<>();
    private int siguienteId = 2;

    /**
     * Carga una cuenta vacia para verificar el flujo temporal.
     */
    public CuentaRepository() {
        data.put(1, new Cuenta(1, null));
    }

    /**
     * Retorna una copia de todas las cuentas.
     */
    public Collection<Cuenta> findAll() {
        return List.copyOf(data.values());
    }

    /**
     * Busca una cuenta por identificador.
     */
    public Cuenta findById(Integer id) {
        return data.get(id);
    }

    /**
     * Crea o actualiza una cuenta en memoria.
     */
    public Cuenta save(Cuenta cuenta) {
        if (cuenta.getId() == null) {
            cuenta.setId(siguienteId++);
        }
        data.put(cuenta.getId(), cuenta);
        return cuenta;
    }

    /**
     * Elimina una cuenta por identificador.
     */
    public void delete(Integer id) {
        data.remove(id);
    }
}
