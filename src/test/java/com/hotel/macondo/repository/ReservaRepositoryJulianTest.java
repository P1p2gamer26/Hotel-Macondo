package com.hotel.macondo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ReservaRepositoryJulianTest {

  @Autowired private ReservaRepository reservaRepository;
  @Autowired private ClienteRepository clienteRepository;
  @Autowired private HabitacionRepository habitacionRepository;
  @Autowired private TipoHabitacionRepository tipoHabitacionRepository;
  @Autowired private CuentaRepository cuentaRepository;

  private Cliente cliente;
  private Habitacion habitacion101;
  private Habitacion habitacion102;

  @BeforeEach
  void ReservaRepository_prepararDatosBase_antesDeCadaPrueba_entidadesPersistidas() {
    // Arrange
    TipoHabitacion tipo = TipoHabitacion.builder()
        .nombre("Suite")
        .descripcion("Suite de prueba")
        .imagen("suite.jpg")
        .precioNoche(new BigDecimal("120.00"))
        .capacidadPersonas(4)
        .build();
    tipo = tipoHabitacionRepository.save(tipo);

    habitacion101 = Habitacion.builder()
        .nombre("Habitacion 101")
        .etiqueta("Suite")
        .precio(new BigDecimal("120.00"))
        .capacidad(4)
        .numero("101")
        .estado("DISPONIBLE")
        .piso(1)
        .build();
    habitacion101.aplicarTipo(tipo);
    habitacion101 = habitacionRepository.save(habitacion101);

    habitacion102 = Habitacion.builder()
        .nombre("Habitacion 102")
        .etiqueta("Suite")
        .precio(new BigDecimal("120.00"))
        .capacidad(4)
        .numero("102")
        .estado("DISPONIBLE")
        .piso(1)
        .build();
    habitacion102.aplicarTipo(tipo);
    habitacion102 = habitacionRepository.save(habitacion102);

    cliente = Cliente.builder()
        .nombre("Julian")
        .apellido("Pruebas")
        .cedula("123456789")
        .telefono("3001234567")
        .correo("julian.pruebas@macondo.test")
        .build();
    cliente = clienteRepository.save(cliente);
  }

  @Test
  void ReservaRepository_buscarHistoricasPorCliente_conCanceladaYActiva_devuelveSoloCancelada() {
    // Arrange
    Reserva cancelada = guardarReserva("RES-HIST-CANCELADA", "CANCELADA", habitacion101);
    guardarReserva("RES-HIST-ACTIVA", "ACTIVA", habitacion102);
    Cliente clienteSinReservas = clienteRepository.save(Cliente.builder()
        .nombre("Sin")
        .apellido("Reservas")
        .cedula("987654321")
        .telefono("3009876543")
        .correo("sin.reservas@macondo.test")
        .build());

    // Act
    List<Reserva> historicas = reservaRepository.buscarHistoricasPorCliente(cliente, LocalDate.now());
    List<Reserva> sinReservas = reservaRepository.buscarHistoricasPorCliente(clienteSinReservas, LocalDate.now());

    // Assert
    assertThat(historicas).containsExactly(cancelada);
    assertThat(sinReservas).isEmpty();
  }

  @Test
  void ReservaRepository_buscarPorHabitacion_conReservasEnDosHabitaciones_devuelveSoloLasDeHabitacionIndicada() {
    // Arrange
    Reserva primera101 = guardarReserva("RES-HAB-101-1", "ACTIVA", habitacion101);
    Reserva segunda101 = guardarReserva("RES-HAB-101-2", "CONFIRMADA", habitacion101);
    guardarReserva("RES-HAB-102-1", "ACTIVA", habitacion102);

    // Act
    List<Reserva> reservas101 = reservaRepository.buscarPorHabitacion(habitacion101.getId());
    List<Reserva> inexistentes = reservaRepository.buscarPorHabitacion(999999L);

    // Assert
    assertThat(reservas101).containsExactlyInAnyOrder(primera101, segunda101);
    assertThat(inexistentes).isEmpty();
  }

  @Test
  void ReservaRepository_buscarIdsActivosPorNumeroHabitacion_conEstadosYHabitacionesDistintas_devuelveSoloIdActivoCorrespondiente() {
    // Arrange
    Reserva activa101 = guardarReserva("RES-ID-ACTIVA-101", "ACTIVA", habitacion101);
    guardarReserva("RES-ID-CANCELADA-101", "CANCELADA", habitacion101);
    guardarReserva("RES-ID-ACTIVA-102", "ACTIVA", habitacion102);

    // Act
    List<Long> idsActivos = reservaRepository.buscarIdsActivosPorNumeroHabitacion("101");
    List<Long> idsInexistentes = reservaRepository.buscarIdsActivosPorNumeroHabitacion("999");

    // Assert
    assertThat(idsActivos).containsExactly(activa101.getId());
    assertThat(idsInexistentes).isEmpty();
  }

  @Test
  void ReservaRepository_buscarDetalleCompleto_conCuentaYDetalle_devuelveReservaConCuentaYUnDetalle() {
    // Arrange
    Reserva reserva = guardarReserva("RES-DETALLE-COMPLETO", "ACTIVA", habitacion101);
    Cuenta cuenta = new Cuenta("ABIERTA", new BigDecimal("120.00"), LocalDateTime.now());
    cuenta.asignarReserva(reserva);
    cuenta.agregarDetalle(new DetalleCuenta(1, new BigDecimal("25.00"), LocalDateTime.now()));
    cuentaRepository.saveAndFlush(cuenta);

    // Act
    var encontrada = reservaRepository.buscarDetalleCompleto("RES-DETALLE-COMPLETO");
    var inexistente = reservaRepository.buscarDetalleCompleto("RES-INEXISTENTE");

    // Assert
    assertThat(encontrada).isPresent();
    assertThat(encontrada.orElseThrow().getCuenta()).isNotNull();
    assertThat(encontrada.orElseThrow().getCuenta().getDetalles()).hasSize(1);
    assertThat(inexistente).isEmpty();
  }

  private Reserva guardarReserva(String numeroReserva, String estado, Habitacion habitacion) {
    Reserva reserva = new Reserva(
        numeroReserva,
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(3),
        2,
        estado,
        new BigDecimal("120.00"),
        new BigDecimal("240.00"));
    reserva.asignarCliente(cliente);
    reserva.asignarHabitacion(habitacion);
    return reservaRepository.saveAndFlush(reserva);
  }
}
