package com.hotel.macondo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ReservaRepository;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    // La inyección de dependencias se realiza a través del constructor
    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public List<Reserva> obtenerReservasActivas() {
        return repository.findAll().stream()
                .filter(Reserva::estaEnCurso)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Reserva> obtenerHistorial() {
        return repository.findAll().stream()
                .filter(Reserva::estaCerrada)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public Reserva buscarPorId(String id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Reserva> buscarActivasDeCliente(Cliente cliente) {
        return cliente == null ? List.of() : cliente.verReservasActivas();
    }

    /** {@inheritDoc} */
    @Override
    public List<Reserva> buscarHistorialDeCliente(Cliente cliente) {
        return cliente == null ? List.of() : cliente.verHistorialReservas();
    }
}
