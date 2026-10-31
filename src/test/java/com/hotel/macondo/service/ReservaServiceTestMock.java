package com.hotel.macondo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.PagoRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ReservaServiceTestMock {

    // Un montón de repositorios porque reserva usa de todo
    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    public void ReservaService_cancelar_ReservaCancelada() {

        // Arrange: Se crea una reserva en estado "CONFIRMADA" con fecha de inicio
        // a futuro (porque es requisito de validarReservaFutura) y una cuenta abierta
        String numeroReserva = "MCD-TEST-CANCEL";

        Reserva reservaMock = new Reserva(
                numeroReserva,
                LocalDate.now().plusDays(10), // Fecha a futuro para pasar la validacion
                LocalDate.now().plusDays(15),
                2,
                "CONFIRMADA",
                new BigDecimal("200000"),
                new BigDecimal("1000000"));

        Cuenta cuentaMock = new Cuenta("ABIERTA", new BigDecimal("1000000"), LocalDateTime.now());
        reservaMock.setCuenta(cuentaMock);

        // Se simula la busqueda por numero de reserva
        when(reservaRepository.findByNumeroReserva(numeroReserva))
                .thenReturn(Optional.of(reservaMock));

        // Se simula que el guardado devuelve la misma entidad que recibe
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Se usa el metodo de cancelar por numero de reserva
        Reserva resultado = reservaService.cancelar(numeroReserva);

        // Assert: Se verifica que el estado de la reserva cambio a "CANCELADA" y que la
        // cuenta asociada paso a total cero y estado cancelado.
        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getEstado()).isEqualTo("CANCELADA");
        Assertions.assertThat(resultado.getCuenta().getEstado()).isEqualTo("CANCELADA");
        Assertions.assertThat(resultado.getCuenta().getTotal()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(reservaRepository).findByNumeroReserva(numeroReserva);
        verify(reservaRepository).save(any(Reserva.class));
        verify(cuentaRepository).save(any(Cuenta.class));
    }
}