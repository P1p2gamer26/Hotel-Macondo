package com.hotel.macondo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;

@DataJpaTest
@RunWith(SpringRunner.class)
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    private Cliente clienteDePrueba;

    @Before
    public void setUp() {
        // Para las pruebas se necesita tipo, habitación, cliente y reservas

        // Guardar tipo de habitacion
        TipoHabitacion tipo = tipoHabitacionRepository.save(
                TipoHabitacion.builder()
                        .nombre("Castaño Fundacional")
                        .descripcion("Habitación doble estándar")
                        .precioNoche(new BigDecimal("280000"))
                        .capacidadPersonas(2)
                        .imagen("habitacion1.jpg")
                        .build());

        // Guardar habitacion asociada al tipo
        Habitacion habitacion = Habitacion.builder()
                .numero("101")
                .nombre("Sombra del Castaño 101")
                .piso(1)
                .capacidad(2)
                .precio(new BigDecimal("280000"))
                .estado("DISPONIBLE")
                .build();
        habitacion.aplicarTipo(tipo);
        habitacion = habitacionRepository.save(habitacion);

        // Guardar Cliente
        clienteDePrueba = clienteRepository.save(
                Cliente.builder()
                        .nombre("Úrsula")
                        .apellido("Iguarán")
                        .cedula("101")
                        .telefono("300101")
                        .correo("ursula@macondo.com")
                        .build());

        // Guardar una Reserva ACTIVA y futura para este cliente
        Reserva reservaActiva = new Reserva(
                "MCD-TEST-VIGENTE",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5),
                2,
                "ACTIVA",
                new BigDecimal("280000"),
                new BigDecimal("840000"));
        reservaActiva.asignarCliente(clienteDePrueba);
        reservaActiva.asignarHabitacion(habitacion);
        reservaRepository.save(reservaActiva);

        // Guardar una Reserva CANCELADA para ver que el filtro la ignore (
        // buscarVigentesPorCliente)
        Reserva reservaCancelada = new Reserva(
                "MCD-TEST-CANCELADA",
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(8),
                2,
                "CANCELADA",
                new BigDecimal("280000"),
                new BigDecimal("560000"));
        reservaCancelada.asignarCliente(clienteDePrueba);
        reservaCancelada.asignarHabitacion(habitacion);
        reservaRepository.save(reservaCancelada);
    }

    @Test
    public void ReservaRepository_buscarVigentesPorCliente_retornaListaConReservasActivas() {

        // Arrange: Se define la fecha actual para evaluar la vigencia
        LocalDate fechaActual = LocalDate.now();

        // Act: Se invoca el metodo buscarVigentesPorCliente para obtener las reservas
        // vigentes del cliente de prueba
        List<Reserva> vigentes = reservaRepository.buscarVigentesPorCliente(clienteDePrueba, fechaActual);

        // Assert: Se valida que la lista no sea nula, no esté vacia, contenga
        // exactamente 1 elemento (omitiendo la cancelada) y traiga el código correcto
        Assertions.assertThat(vigentes).isNotNull();
        Assertions.assertThat(vigentes).isNotEmpty();
        Assertions.assertThat(vigentes.size()).isEqualTo(1);
        Assertions.assertThat(vigentes.get(0).getNumeroReserva()).isEqualTo("MCD-TEST-VIGENTE");
        Assertions.assertThat(vigentes.get(0).getEstado()).isEqualTo("ACTIVA");
    }
}