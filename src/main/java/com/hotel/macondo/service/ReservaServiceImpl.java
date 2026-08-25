package com.hotel.macondo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ReservaRepository;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    // La inyección de dependencias se realiza a través del constructor
    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> obtenerReservasActivas() {
        return repository.findAll().stream()
                .filter(r -> List.of("ACTIVA", "CONFIRMADA", "PENDIENTE").contains(r.getEstado().toUpperCase()))
                .toList();
    }

    @Override
    public List<Reserva> obtenerHistorial() {
        return repository.findAll().stream()
                .filter(r -> List.of("FINALIZADA", "CANCELADA").contains(r.getEstado().toUpperCase()))
                .toList();
    }

    @Override
    public Reserva buscarPorId(String id) {
        return repository.findById(id);
    }
}