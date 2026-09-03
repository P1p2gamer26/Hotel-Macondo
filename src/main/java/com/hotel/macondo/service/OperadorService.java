package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Operador;

public interface OperadorService {

    /** Retorna todos los operadores registrados. */
    Collection<Operador> buscarTodos();

    /** Busca un operador por identificador. */
    Operador buscarPorId(Integer id);

    /** Crea o actualiza un operador. */
    Operador guardar(Operador operador);

    /** Cuenta los operadores que estan activos. */
    long contarActivos();

    /**
     * Invierte el estado (activo/inactivo) de un operador. Retorna null si el
     * operador no existe.
     */
    Operador cambiarEstado(Integer id);

    /** Elimina un operador por identificador. */
    void eliminar(Integer id);
}
