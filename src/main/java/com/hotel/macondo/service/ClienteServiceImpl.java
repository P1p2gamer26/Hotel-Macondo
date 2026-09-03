package com.hotel.macondo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repository;

    private static final Locale LOCALE_COLOMBIA = new Locale("es", "CO");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter
            .ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", LOCALE_COLOMBIA);

    /** {@inheritDoc} */
    @Override
    public Collection<Cliente> buscarTodos() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Cliente buscarPorId(Integer id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente buscarPorCedula(String cedula) {
        return repository.findByCedula(cedula);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente guardar(Cliente cliente) {
        return repository.save(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente actualizarInformacion(Cliente cliente, Cliente informacion) {
        cliente.actualizarInformacion(
                informacion.getNombre(),
                informacion.getApellido(),
                informacion.getTelefono(),
                informacion.getCorreo());
        return guardar(cliente);
    }

    @Override
    public Reserva obtenerReservaActiva(Cliente cliente) {
        if (cliente == null)
            return null;
        List<Reserva> activas = cliente.verReservasActivas();
        return activas.isEmpty() ? null : activas.get(0);
    }

    @Override
    public Habitacion obtenerHabitacionActiva(Reserva reserva) {
        if (reserva == null || reserva.getHabitaciones() == null || reserva.getHabitaciones().isEmpty()) {
            return null;
        }
        return reserva.getHabitaciones().get(0);
    }

    @Override
    public long calcularNoches(Reserva reserva) {
        if (reserva == null || reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
    }

    @Override
    public int contarReservasActivas(Cliente cliente) {
        return cliente == null ? 0 : cliente.verReservasActivas().size();
    }

    @Override
    public int contarHistorialReservas(Cliente cliente) {
        return cliente == null ? 0 : cliente.verHistorialReservas().size();
    }

    @Override
    public String obtenerFechaActualFormateada() {
        String texto = FORMATO_FECHA.format(LocalDate.now());
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
