package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Operador;

@Repository
public class OperadorRepository {

    private final Map<Long, Operador> data = new LinkedHashMap<>();
    private int siguienteId = 2;

    /**
     * Carga un operador de prueba para el almacenamiento temporal.
     */
    public OperadorRepository() {
        Operador operador = new Operador("Recepcion principal", true);
        operador.setId(1L);
        data.put(1L, operador);
    }

    /**
     * Retorna una copia de todos los operadores.
     */
    public Collection<Operador> findAll() {
        return List.copyOf(data.values());
    }

    /**
     * Busca un operador por identificador.
     */
    public Operador findById(Long id) {
        return data.get(id);
    }

    /**
     * Crea o actualiza un operador en memoria.
     */
    public Operador save(Operador operador) {
        if (operador.getId() == null) {
            operador.setId((long) siguienteId++);
        }
        data.put(operador.getId(), operador);
        return operador;
    }

    /**
     * Elimina un operador por identificador.
     */
    public void delete(Long id) {
        data.remove(id);
    }
}
