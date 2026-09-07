package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.service.ClienteService;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.TipoHabitacionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * La transaccion mantiene abiertas las colecciones lazy y ademas deja la base
 * como estaba: cada prueba borra datos sembrados y hace rollback al terminar.
 */
@SpringBootTest
@Transactional
class EliminacionCascadaTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ClienteService clienteService;

    @Autowired
    HabitacionService habitacionService;

    @Autowired
    TipoHabitacionService tipoHabitacionService;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ReservaRepository reservaRepository;

    @Autowired
    HabitacionRepository habitacionRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    /**
     * Borrar un cliente arrastra sus reservas y su usuario, pero no toca las
     * habitaciones: esas son catalogo y sobreviven.
     */
    @Test
    void borrarClienteArrastraReservasYUsuario() {
        Cliente cliente = clienteRepository.findAllByOrderByIdAsc().stream()
                .filter(c -> !c.getReservas().isEmpty())
                .findFirst()
                .orElseThrow();
        Long clienteId = cliente.getId();
        String correo = cliente.getCorreo();
        List<Long> reservaIds = cliente.getReservas().stream().map(Reserva::getId).toList();
        long habitacionesAntes = habitacionRepository.count();

        assertFalse(reservaIds.isEmpty(), "el cliente de prueba debe tener reservas");
        assertNotNull(usuarioRepository.findByCorreoIgnoreCase(correo).orElse(null));

        clienteService.eliminar(clienteId);
        recargarDesdeLaBase();

        assertTrue(clienteRepository.findById(clienteId).isEmpty(), "el cliente sigue vivo");
        for (Long reservaId : reservaIds) {
            assertTrue(reservaRepository.findById(reservaId).isEmpty(),
                    "la reserva " + reservaId + " no se borro en cascada");
        }
        assertTrue(usuarioRepository.findByCorreoIgnoreCase(correo).isEmpty(),
                "el usuario del cliente no se borro en cascada");
        assertEquals(habitacionesAntes, habitacionRepository.count(),
                "borrar un cliente no puede borrar habitaciones");
    }

    /**
     * Una habitacion con reservas no se borra: la reserva quedaria sin
     * habitacion y cascadearla borraria reservas ajenas.
     */
    @Test
    void noSePuedeBorrarUnaHabitacionConReservas() {
        Habitacion habitacion = habitacionRepository.findAllByOrderByIdAsc().stream()
                .filter(h -> !h.getReservas().isEmpty())
                .findFirst()
                .orElseThrow();
        Long habitacionId = habitacion.getId();
        List<Long> reservaIds = habitacion.getReservas().stream().map(Reserva::getId).toList();

        assertFalse(habitacionService.eliminar(habitacionId),
                "borrar una habitacion reservada debe rechazarse");
        recargarDesdeLaBase();

        assertTrue(habitacionRepository.findById(habitacionId).isPresent(),
                "la habitacion no debia borrarse");
        for (Long reservaId : reservaIds) {
            assertTrue(reservaRepository.findById(reservaId).isPresent(),
                    "la reserva " + reservaId + " debia seguir intacta");
        }
    }

    /** Una habitacion libre si se puede borrar. */
    @Test
    void siSePuedeBorrarUnaHabitacionLibre() {
        Habitacion libre = habitacionRepository.findAllByOrderByIdAsc().stream()
                .filter(h -> h.getReservas().isEmpty())
                .findFirst()
                .orElseThrow();
        Long libreId = libre.getId();

        assertTrue(habitacionService.eliminar(libreId));
        recargarDesdeLaBase();

        assertTrue(habitacionRepository.findById(libreId).isEmpty());
    }

    /**
     * El ON DELETE CASCADE lo aplica la base, no Hibernate: hay que bajar el
     * DELETE y vaciar el contexto para no leer entidades ya borradas en cache.
     */
    private void recargarDesdeLaBase() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Al rechazar el borrado hay que poder decir quien ocupa la habitacion,
     * con nombre de cliente y numero de reserva.
     */
    @Test
    void informaQuienOcupaLaHabitacion() {
        Habitacion habitacion = habitacionRepository.findAllByOrderByIdAsc().stream()
                .filter(h -> !h.getReservas().isEmpty())
                .findFirst()
                .orElseThrow();

        List<Reserva> asociadas = habitacionService.reservasAsociadas(habitacion.getId());

        assertFalse(asociadas.isEmpty(), "debe listar las reservas que la ocupan");
        for (Reserva reserva : asociadas) {
            assertNotNull(reserva.getNumeroReserva());
            assertNotNull(reserva.getCliente(), "el cliente debe venir cargado");
            assertNotNull(reserva.getCliente().getNombre());
        }
    }

    /** Una habitacion libre no reporta ninguna reserva. */
    @Test
    void habitacionSinReservasNoReportaNada() {
        Habitacion libre = habitacionRepository.findAllByOrderByIdAsc().stream()
                .filter(h -> h.getReservas().isEmpty())
                .findFirst()
                .orElseThrow();

        assertTrue(habitacionService.reservasAsociadas(libre.getId()).isEmpty());
    }

    /** Un tipo con habitaciones asociadas no se puede borrar: se bloquea, no se cascadea. */
    @Test
    void noSePuedeBorrarUnTipoConHabitaciones() {
        Habitacion habitacion = habitacionRepository.findAllByOrderByIdAsc().iterator().next();
        Long tipoId = habitacion.getTipoHabitacion().getId();

        assertFalse(tipoHabitacionService.eliminar(tipoId),
                "borrar un tipo en uso debe rechazarse");
        assertNotNull(tipoHabitacionService.buscarPorId(tipoId), "el tipo no debia borrarse");
    }
}
