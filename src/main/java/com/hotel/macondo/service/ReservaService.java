package com.hotel.macondo.service;

import java.util.List;

import com.hotel.macondo.entities.Reserva;

public interface ReservaService {

    List<Reserva> obtenerReservasActivas();

    List<Reserva> obtenerHistorial();

    Reserva buscarPorId(String id);
}