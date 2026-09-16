package com.hotel.macondo.service;

import java.util.Collection;
import java.util.List;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.RecursoNoEncontradoException;

public interface HabitacionService {

    /** Retorna todas las habitaciones disponibles en el catalogo. */
    Collection<Habitacion> buscarTodas();

    /**
     * Busca una habitacion por su identificador.
     *
     * @throws RecursoNoEncontradoException si no existe una habitacion con ese id
     */
    Habitacion buscarPorId(Long id);

    /** Busca una habitacion por su etiqueta operativa. */
    Habitacion buscarPorNombre(String nombre);

    /** Filtra las habitaciones que admiten la cantidad de personas indicada. */
    Collection<Habitacion> buscarPorPersonas(int personas);

    /** Cuenta todas las habitaciones registradas. */
    long contarTodas();

    /** Cuenta las habitaciones que estan disponibles para reservar. */
    long contarDisponibles();

    /** Localiza la primera unidad fisica disponible del tipo solicitado. */
    Habitacion buscarDisponiblePorTipo(Long idTipo);

    /**
     * Crea o actualiza una habitacion asignandole un tipo de habitacion por
     * su identificador. El tipo es la fuente de verdad: de el se derivan el
     * precio y la capacidad de la habitacion.
     *
     * Toda habitacion debe tener un tipo valido: si el identificador es nulo
     * o no corresponde a ningun tipo, no se guarda nada y retorna null.
     * 
     * @throws FormularioErroneoException si el tipo es nulo o no existe, o si el nombre de la habitacion es invalido
     */
    Habitacion guardar(Habitacion habitacion, Long idTipo);

    /**
     * Invierte la disponibilidad de una habitacion: la pone en mantenimiento
     * o la devuelve al catalogo. Retorna null si la habitacion no existe.
     */
    Habitacion cambiarEstado(Long id);

    /**
     * Propaga tarifa y capacidad a las habitaciones que referencian el tipo.
     */
    void actualizarHabitacionesPorTipo(TipoHabitacion tipo);

    /**
     * Indica si existe al menos una habitacion asignada al tipo indicado.
     */
    boolean existeHabitacionConTipo(Long idTipo);

    /**
     * Reservas que usan esta habitacion. Si la lista no esta vacia la
     * habitacion no se puede eliminar; sirve para decirle al administrador
     * a quien tendria que reubicar primero.
     */
    List<Reserva> reservasAsociadas(Long id);

    /**
     * Elimina una habitacion por identificador. No la elimina si todavia
     * tiene reservas asociadas, porque esas reservas quedarian sin
     * habitacion. Retorna false cuando el borrado se rechaza.
     * 
     * @throws RecursoNoEncontradoException si no existe una habitacion con ese id
     * @throws PeticionImposible si la habitacion tiene reservas asociadas
     */
    void eliminar(Long id);
}
