package com.hotel.macondo.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ReservaServiceTestNaive {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private TipoHabitacionRepository tipoHabitacionRepository;

    private Long clienteId;
    private Long tipoHabitacionId;

    @BeforeEach
    public void init() {
        // Como es una prueba sin mocks se necesitan crear los datos de prueba: el
        // cliente, el tipo y la habitacion para que la logica del servicio pueda
        // ejecutar la reserva

        TipoHabitacion tipo = tipoHabitacionRepository.save(
                TipoHabitacion.builder()
                        .nombre("Castaño")
                        .descripcion("Habitación prueba")
                        .precioNoche(new BigDecimal("200000"))
                        .capacidadPersonas(2)
                        .imagen("foto.jpg")
                        .build());
        this.tipoHabitacionId = tipo.getId();

        Habitacion hab = Habitacion.builder()
                .numero("501")
                .nombre("Habitación 501")
                .piso(5)
                .capacidad(2)
                .precio(new BigDecimal("200000"))
                .estado("DISPONIBLE")
                .build();
        hab.aplicarTipo(tipo);
        habitacionRepository.save(hab);

        Cliente cliente = clienteRepository.save(
                Cliente.builder()
                        .nombre("José Arcadio")
                        .apellido("Buendía")
                        .cedula("888222")
                        .telefono("315888222")
                        .correo("jose@macondo.com")
                        .build());
        this.clienteId = cliente.getId();
    }

    @Test
    public void ReservaService_crear_Reserva() {

        // Arrange: Se definen los datos de reserva, fechas válidas y cantidad de
        // personas dependiendo de la capacidad del tipo de habitacion
        String fechaEntrada = LocalDate.now().plusDays(10).toString();
        String fechaSalida = LocalDate.now().plusDays(13).toString();
        Integer cantidadPersonas = 2;

        // Act: Se crea la reserva, buscando el cliente, el tipo de habitación,
        // validando la disponibilidad, calculando el total y guardando
        Reserva reservaCreada = reservaService.crear(
                clienteId,
                tipoHabitacionId,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas);

        // Assert: Se comprueba que el servicio retorno la reserva creada, que tiene
        // su codigo generado y de estado inicial "confirmada" (el default)
        Assertions.assertThat(reservaCreada).isNotNull();
        Assertions.assertThat(reservaCreada.getNumeroReserva()).isNotNull();
        Assertions.assertThat(reservaCreada.getEstado()).isEqualTo("CONFIRMADA");
        Assertions.assertThat(reservaCreada.getCantidadPersonas()).isEqualTo(2);
    }
}