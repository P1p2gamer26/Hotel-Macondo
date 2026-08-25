package com.hotel.macondo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;

@Repository
public class ClienteRepository {

    private final Map<Integer, Cliente> data = new LinkedHashMap<>();
    private int siguienteId = 3;

    /**
     * Carga clientes de demostracion junto con reservas para que el dashboard
     * privado pueda presentar datos dinamicos sin depender de una base real.
     */
    public ClienteRepository() {
        Cliente ana = new Cliente(1, "Ana", "Torres", "1001001",
                "3001112233", "ana@macondo.com");
        ana.agregarReserva(crearReservaActiva());
        ana.agregarReserva(crearReservaFinalizada());
        data.put(ana.getId(), ana);

        Cliente luis = new Cliente(2, "Luis", "Mora", "1001002",
                "3004445566", "luis@macondo.com");
        luis.agregarReserva(crearReservaParaLuis());
        data.put(luis.getId(), luis);
    }

    /** Retorna una copia de todos los clientes. */
    public Collection<Cliente> findAll() {
        return List.copyOf(data.values());
    }

    /** Busca un cliente por su identificador interno. */
    public Cliente findById(Integer id) {
        return data.get(id);
    }

    /** Busca un cliente por su documento. */
    public Cliente findByCedula(String cedula) {
        return data.values().stream()
                .filter(cliente -> cliente.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
    }

    /** Crea o actualiza un cliente en memoria. */
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(siguienteId++);
        }
        data.put(cliente.getId(), cliente);
        return cliente;
    }

    /** Elimina un cliente por identificador. */
    public void delete(Integer id) {
        data.remove(id);
    }

    /** Reserva futura de tres noches que se muestra como principal para Ana. */
    private Reserva crearReservaActiva() {
        LocalDate entrada = LocalDate.now().plusDays(22);
        LocalDate salida = entrada.plusDays(3);
        return crearReserva("MCD-2026-0915", entrada, salida, "ACTIVA",
                crearHabitacion("301", 3, "VIP Suite", 950000, 4));
    }

    /** Reserva terminada para demostrar el contador de historial. */
    private Reserva crearReservaFinalizada() {
        LocalDate salida = LocalDate.now().minusDays(20);
        return crearReserva("MCD-2026-0801", salida.minusDays(2), salida,
                "FINALIZADA", crearHabitacion("204", 2, "Executive", 580000, 3));
    }

    /** Reserva futura independiente para que la ruta /cliente/2 tambien sea util. */
    private Reserva crearReservaParaLuis() {
        LocalDate entrada = LocalDate.now().plusDays(35);
        return crearReserva("MCD-2026-0928", entrada, entrada.plusDays(2),
                "ACTIVA", crearHabitacion("102", 1, "Normal", 350000, 2));
    }

    /** Construye una reserva con una sola habitacion y calcula su total. */
    private Reserva crearReserva(String numero, LocalDate entrada, LocalDate salida,
            String estado, Habitacion habitacion) {
        Reserva reserva = new Reserva();
        reserva.setNumeroReserva(numero);
        reserva.setFechaInicio(entrada);
        reserva.setFechaFin(salida);
        reserva.setCantidadPersonas(habitacion.getCapacidad());
        reserva.setEstado(estado);
        reserva.setTotal(BigDecimal.ZERO);
        reserva.agregarHabitacion(habitacion);
        return reserva;
    }

    /** Crea el objeto de habitacion que acompana los datos demostrativos de una reserva. */
    private Habitacion crearHabitacion(String numero, int piso, String nombre,
            long precioNoche, int capacidad) {
        TipoHabitacion tipo = new TipoHabitacion(nombre,
                "Habitacion asignada a la reserva del cliente.",
                BigDecimal.valueOf(precioNoche), capacidad);
        return new Habitacion(numero, "DISPONIBLE", piso, tipo);
    }
}