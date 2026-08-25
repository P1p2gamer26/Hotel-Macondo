package com.hotel.macondo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Reserva;

@Repository
public class ReservaRepository {

    private final Map<String, Reserva> data = new LinkedHashMap<>();

    public ReservaRepository() {
        // Datos de prueba iniciales para simular la base de datos
        data.put("MHC-2025-001", new Reserva("MHC-2025-001", LocalDate.now(), LocalDate.now().plusDays(3), 2, "ACTIVA",
                new BigDecimal("2850000"), new ArrayList<>()));
        data.put("MHC-2025-002", new Reserva("MHC-2025-002", LocalDate.now().plusDays(5), LocalDate.now().plusDays(8),
                2, "CONFIRMADA", new BigDecimal("1740000"), new ArrayList<>()));
        data.put("MHC-2024-089", new Reserva("MHC-2024-089", LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(7), 1, "FINALIZADA", new BigDecimal("1050000"), new ArrayList<>()));
        data.put("MHC-2023-211", new Reserva("MHC-2023-211", LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(25), 4, "CANCELADA", new BigDecimal("5700000"), new ArrayList<>()));
    }

    public Collection<Reserva> findAll() {
        return data.values();
    }

    public Reserva findById(String id) {
        return data.get(id);
    }

    public Reserva save(Reserva reserva) {
        data.put(reserva.getNumeroReserva(), reserva);
        return reserva;
    }

    public void delete(String id) {
        data.remove(id);
    }
}