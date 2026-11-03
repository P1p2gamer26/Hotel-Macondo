package com.hotel.macondo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private EntityManager entityManager;

    private Cliente cliente;
    private Habitacion habitacion101;
    private Habitacion habitacion102;
    private LocalDate hoy;

    @BeforeEach
    public void setUp() {
        hoy = LocalDate.now();
        TipoHabitacion tipo = tipoHabitacionRepository.save(
                new TipoHabitacion("Suite de prueba", "Suite", "suite.jpg",
                        new BigDecimal("120"), 4));
        habitacion101 = new Habitacion("Habitacion 101", "Suite",
                tipo.getPrecioNoche(), 4, "101", "DISPONIBLE", 1);
        habitacion101.aplicarTipo(tipo);
        habitacion101 = habitacionRepository.save(habitacion101);
        habitacion102 = new Habitacion("Habitacion 102", "Suite",
                tipo.getPrecioNoche(), 4, "102", "DISPONIBLE", 1);
        habitacion102.aplicarTipo(tipo);
        habitacion102 = habitacionRepository.save(habitacion102);
        cliente = clienteRepository.save(new Cliente("Cliente", "Prueba", "123456789",
                "3001234567", "cliente@macondo.test"));
    }

    @Test
    public void ReservaRepository_buscarVigentesPorCliente_ReservasVigentesOrdenadas() {
        // Arrange
        guardarReserva("RES-ACTIVA", "ACTIVA", cliente, habitacion101,
                hoy.plusDays(5), hoy.plusDays(7));
        guardarReserva("RES-CONFIRMADA", "CONFIRMADA", cliente, habitacion101,
                hoy.plusDays(1), hoy.plusDays(3));
        guardarReserva("RES-PENDIENTE", "PENDIENTE", cliente, habitacion102,
                hoy.plusDays(3), hoy.plusDays(5));
        guardarReserva("RES-CANCELADA", "CANCELADA", habitacion101);
        guardarReserva("RES-FINALIZADA", "FINALIZADA", habitacion102);
        guardarReserva("RES-VENCIDA", "ACTIVA", cliente, habitacion101,
                hoy.minusDays(4), hoy.minusDays(2));
        Cliente otroCliente = clienteRepository.save(new Cliente("Otro", "Cliente",
                "987654321", "3009876543", "otro@macondo.test"));
        guardarReserva("RES-AJENA", "ACTIVA", otroCliente, habitacion102,
                hoy.plusDays(1), hoy.plusDays(3));
        entityManager.clear();

        // Act
        var vigentes = reservaRepository.buscarVigentesPorCliente(cliente, hoy);

        // Assert
        assertThat(vigentes).extracting(Reserva::getNumeroReserva)
                .containsExactly("RES-CONFIRMADA", "RES-PENDIENTE", "RES-ACTIVA");
    }

    @Test
    public void ReservaRepository_buscarHistoricasPorCliente_ReservasHistoricasOrdenadas() {
        // Arrange
        guardarReserva("RES-CANCELADA", "CANCELADA", cliente, habitacion101,
                hoy.plusDays(5), hoy.plusDays(7));
        guardarReserva("RES-FINALIZADA", "FINALIZADA", cliente, habitacion101,
                hoy.minusDays(3), hoy.minusDays(1));
        guardarReserva("RES-VENCIDA", "ACTIVA", cliente, habitacion102,
                hoy.minusDays(7), hoy.minusDays(4));
        guardarReserva("RES-ACTIVA-FUTURA", "ACTIVA", habitacion102);
        Cliente sinReservas = clienteRepository.save(new Cliente("Sin", "Reservas",
                "987654321", "3009876543", "sin@macondo.test"));
        entityManager.clear();

        // Act
        var historicas = reservaRepository.buscarHistoricasPorCliente(cliente, hoy);
        var vacias = reservaRepository.buscarHistoricasPorCliente(sinReservas, hoy);

        // Assert
        assertThat(historicas).extracting(Reserva::getNumeroReserva)
                .containsExactly("RES-CANCELADA", "RES-FINALIZADA", "RES-VENCIDA");
        assertThat(vacias).isEmpty();
    }

    @Test
    public void ReservaRepository_buscarPorHabitacion_ReservasDeLaHabitacionOrdenadas() {
        // Arrange
        Reserva posterior = guardarReserva("RES-101-POSTERIOR", "CONFIRMADA",
                cliente, habitacion101, hoy.plusDays(5), hoy.plusDays(7));
        Reserva anterior = guardarReserva("RES-101-ANTERIOR", "ACTIVA",
                cliente, habitacion101, hoy.plusDays(1), hoy.plusDays(3));
        guardarReserva("RES-102", "ACTIVA", habitacion102);
        Long idHabitacion = habitacion101.getId();
        entityManager.clear();

        // Act
        var reservas = reservaRepository.buscarPorHabitacion(idHabitacion);
        var inexistentes = reservaRepository.buscarPorHabitacion(Long.MAX_VALUE);

        // Assert
        assertThat(reservas).extracting(Reserva::getId)
                .containsExactly(anterior.getId(), posterior.getId());
        assertThat(inexistentes).isEmpty();
    }

    @Test
    public void ReservaRepository_buscarIdsActivosPorNumeroHabitacion_IdDeReservaActiva() {
        // Arrange
        Reserva activa = guardarReserva("RES-ACTIVA-101", "ACTIVA", habitacion101);
        guardarReserva("RES-CANCELADA-101", "CANCELADA", habitacion101);
        guardarReserva("RES-CONFIRMADA-101", "CONFIRMADA", habitacion101);
        guardarReserva("RES-ACTIVA-102", "ACTIVA", habitacion102);
        entityManager.clear();

        // Act
        var ids = reservaRepository.buscarIdsActivosPorNumeroHabitacion("101");
        var inexistentes = reservaRepository.buscarIdsActivosPorNumeroHabitacion("999");

        // Assert
        assertThat(ids).containsExactly(activa.getId());
        assertThat(inexistentes).isEmpty();
    }

    @Test
    public void ReservaRepository_buscarDetalleCompleto_ReservaConRelaciones() {
        // Arrange
        Reserva reserva = guardarReserva("RES-DETALLE", "ACTIVA", habitacion101);
        Cuenta cuenta = new Cuenta("ABIERTA", new BigDecimal("240"), LocalDateTime.now());
        cuenta.asignarReserva(reserva);
        cuenta.agregarDetalle(new DetalleCuenta(1, new BigDecimal("25"), LocalDateTime.now()));
        cuentaRepository.saveAndFlush(cuenta);
        Long reservaId = reserva.getId();
        Long clienteId = cliente.getId();
        Long habitacionId = habitacion101.getId();
        Long tipoId = habitacion101.getTipoHabitacion().getId();
        Long cuentaId = cuenta.getId();
        entityManager.clear();

        // Act
        var encontrada = reservaRepository.buscarDetalleCompleto("RES-DETALLE");
        var inexistente = reservaRepository.buscarDetalleCompleto("RES-INEXISTENTE");

        // Assert
        assertThat(encontrada).isPresent();
        Reserva detalle = encontrada.orElseThrow();
        assertThat(detalle.getId()).isEqualTo(reservaId);
        assertThat(detalle.getCliente().getId()).isEqualTo(clienteId);
        assertThat(detalle.getHabitacion().getId()).isEqualTo(habitacionId);
        assertThat(detalle.getHabitacion().getTipoHabitacion().getId()).isEqualTo(tipoId);
        assertThat(detalle.getCuenta().getId()).isEqualTo(cuentaId);
        assertThat(detalle.getCuenta().getDetalles()).hasSize(1);
        assertThat(detalle.getCuenta().getDetalles().get(0).getPrecio())
                .isEqualByComparingTo("25");
        assertThat(inexistente).isEmpty();
    }

    private Reserva guardarReserva(String numero, String estado, Habitacion habitacion) {
        return guardarReserva(numero, estado, cliente, habitacion,
                hoy.plusDays(1), hoy.plusDays(3));
    }

    private Reserva guardarReserva(String numero, String estado, Cliente titular,
            Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        BigDecimal precio = habitacion.getPrecio();
        BigDecimal total = precio.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(entrada, salida)));
        Reserva reserva = new Reserva(numero, entrada, salida, 2, estado, precio, total);
        reserva.asignarCliente(titular);
        reserva.asignarHabitacion(habitacion);
        return reservaRepository.saveAndFlush(reserva);
    }
}
