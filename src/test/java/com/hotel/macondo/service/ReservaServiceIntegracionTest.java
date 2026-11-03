package com.hotel.macondo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ReservaServiceIntegracionTest {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    private Long clienteId;
    private Long tipoId;
    private Long habitacionId;
    private LocalDate entrada;
    private LocalDate salida;

    @BeforeEach
    public void setUp() {
        LocalDate hoy = LocalDate.now();
        entrada = hoy.plusDays(10);
        salida = hoy.plusDays(13);

        TipoHabitacion tipo = tipoHabitacionRepository.save(
                new TipoHabitacion("Tipo de prueba", "Habitacion de prueba", "foto.jpg",
                        new BigDecimal("200000"), 2));
        tipoId = tipo.getId();

        Habitacion habitacion = new Habitacion("Habitacion 501", "PRUEBA",
                tipo.getPrecioNoche(), 2, "501", "DISPONIBLE", 5);
        habitacion.aplicarTipo(tipo);
        habitacionId = habitacionRepository.save(habitacion).getId();

        clienteId = clienteRepository.save(
                new Cliente("Jose", "Buendia", "888222", "315888222",
                        "jose@macondo.test")).getId();
    }

    @Test
    public void ReservaService_crear_ReservaConfirmadaConCuenta() {
        // Arrange
        int personas = 2;

        // Act
        Reserva creada = reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), personas);

        // Assert
        Reserva guardada = reservaRepository.findById(creada.getId()).orElseThrow();
        Cuenta cuenta = cuentaRepository.findById(creada.getCuenta().getId()).orElseThrow();
        assertThat(guardada.getNumeroReserva()).startsWith("MCD-");
        assertThat(guardada.getEstado()).isEqualTo("CONFIRMADA");
        assertThat(guardada.getCantidadPersonas()).isEqualTo(personas);
        assertThat(guardada.getCliente().getId()).isEqualTo(clienteId);
        assertThat(guardada.getHabitacion().getId()).isEqualTo(habitacionId);
        assertThat(guardada.getPrecioNoche()).isEqualByComparingTo("200000");
        assertThat(guardada.getTotal()).isEqualByComparingTo("600000");
        assertThat(cuenta.getEstado()).isEqualTo("ABIERTA");
        assertThat(cuenta.getTotal()).isEqualByComparingTo("600000");
        assertThat(cuenta.getReserva().getId()).isEqualTo(guardada.getId());
        assertThat(reservaRepository.count()).isEqualTo(1);
        assertThat(cuentaRepository.count()).isEqualTo(1);
    }

    @Test
    public void ReservaService_modificar_ReservaYCuentaActualizadas() {
        // Arrange
        Reserva original = reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), 2);
        LocalDate nuevaEntrada = entrada.plusDays(1);
        LocalDate nuevaSalida = salida.plusDays(2);

        // Act
        reservaService.modificar(clienteId, original.getId(), tipoId,
                nuevaEntrada.toString(), nuevaSalida.toString(), 1);

        // Assert
        Reserva modificada = reservaRepository.findById(original.getId()).orElseThrow();
        Cuenta cuenta = cuentaRepository.findById(original.getCuenta().getId()).orElseThrow();
        assertThat(modificada.getNumeroReserva()).isEqualTo(original.getNumeroReserva());
        assertThat(modificada.getFechaInicio()).isEqualTo(nuevaEntrada);
        assertThat(modificada.getFechaFin()).isEqualTo(nuevaSalida);
        assertThat(modificada.getCantidadPersonas()).isEqualTo(1);
        assertThat(modificada.getHabitacion().getId()).isEqualTo(habitacionId);
        assertThat(modificada.getTotal()).isEqualByComparingTo("800000");
        assertThat(cuenta.getTotal()).isEqualByComparingTo("800000");
        assertThat(cuenta.getEstado()).isEqualTo("ABIERTA");
        assertThat(reservaRepository.count()).isEqualTo(1);
        assertThat(cuentaRepository.count()).isEqualTo(1);
    }

    @Test
    public void ReservaService_cancelar_ReservaYCuentaCanceladas() {
        // Arrange
        Reserva original = reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), 2);

        // Act
        reservaService.cancelar(clienteId, original.getId());

        // Assert
        Reserva cancelada = reservaRepository.findById(original.getId()).orElseThrow();
        Cuenta cuenta = cuentaRepository.findById(original.getCuenta().getId()).orElseThrow();
        assertThat(cancelada.getEstado()).isEqualTo("CANCELADA");
        assertThat(cuenta.getEstado()).isEqualTo("CANCELADA");
        assertThat(cuenta.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(reservaRepository.count()).isEqualTo(1);
        assertThat(cuentaRepository.count()).isEqualTo(1);
    }

    @Test
    public void ReservaService_crearSinDisponibilidad_ReservaRechazada() {
        // Arrange
        Reserva original = reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), 2);

        // Act
        assertThatThrownBy(() -> reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), 2))
                // Assert
                .isInstanceOf(PeticionImposible.class)
                .hasMessage("No hay habitaciones disponibles para las fechas seleccionadas.");
        assertThat(reservaRepository.count()).isEqualTo(1);
        assertThat(cuentaRepository.count()).isEqualTo(1);
        assertThat(reservaRepository.findById(original.getId()).orElseThrow().getEstado())
                .isEqualTo("CONFIRMADA");
    }

    @Test
    public void ReservaService_crearSuperandoCapacidad_ReservaRechazada() {
        // Arrange
        int personas = 3;

        // Act
        assertThatThrownBy(() -> reservaService.crear(
                clienteId, tipoId, entrada.toString(), salida.toString(), personas))
                // Assert
                .isInstanceOf(PeticionImposible.class)
                .hasMessage("El tipo de habitación no admite la cantidad de personas indicada.");
        assertThat(reservaRepository.count()).isZero();
        assertThat(cuentaRepository.count()).isZero();
        assertThat(habitacionRepository.findById(habitacionId).orElseThrow().getEstado())
                .isEqualTo("DISPONIBLE");
    }
}
