package com.hotel.macondo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.errors.RecursoNoEncontradoException;
import com.hotel.macondo.repository.ClienteRepository;
import com.hotel.macondo.repository.CuentaRepository;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.PagoRepository;
import com.hotel.macondo.repository.ReservaRepository;
import com.hotel.macondo.repository.TipoHabitacionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceMockTest {

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
    public void ReservaService_cancelar_ReservaYCuentaCanceladas() {
        // Arrange
        Reserva reserva = prepararReserva();
        Cuenta cuenta = reserva.getCuenta();
        when(reservaRepository.findByNumeroReserva(reserva.getNumeroReserva()))
                .thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Reserva resultado = reservaService.cancelar(reserva.getNumeroReserva());

        // Assert
        assertThat(resultado.getEstado()).isEqualTo("CANCELADA");
        assertThat(resultado.getCuenta().getEstado()).isEqualTo("CANCELADA");
        assertThat(resultado.getCuenta().getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(reservaRepository).findByNumeroReserva(reserva.getNumeroReserva());
        verify(reservaRepository).save(reserva);
        verify(cuentaRepository).save(cuenta);
    }

    @Test
    public void ReservaService_crear_ReservaConfirmadaConCuenta() {
        // Arrange
        Cliente cliente = prepararCliente();
        TipoHabitacion tipo = prepararTipo();
        Habitacion habitacion = prepararHabitacion(tipo);
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(tipoHabitacionRepository.findById(tipo.getId())).thenReturn(Optional.of(tipo));
        when(habitacionRepository.buscarDisponibles(tipo.getId(), entrada, salida, 2,
                List.of("ACTIVA", "CONFIRMADA", "PENDIENTE"), null))
                .thenReturn(List.of(habitacion));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva guardada = invocation.getArgument(0);
            guardada.setId(4L);
            return guardada;
        });
        ArgumentCaptor<Cuenta> cuentaCaptor = ArgumentCaptor.forClass(Cuenta.class);

        // Act
        Reserva resultado = reservaService.crear(cliente.getId(), tipo.getId(),
                entrada.toString(), salida.toString(), 2);

        // Assert
        assertThat(resultado.getId()).isEqualTo(4L);
        assertThat(resultado.getNumeroReserva()).startsWith("MCD-");
        assertThat(resultado.getEstado()).isEqualTo("CONFIRMADA");
        assertThat(resultado.getCliente()).isSameAs(cliente);
        assertThat(resultado.getHabitacion()).isSameAs(habitacion);
        assertThat(resultado.getCantidadPersonas()).isEqualTo(2);
        assertThat(resultado.getPrecioNoche()).isEqualByComparingTo("200000");
        assertThat(resultado.getTotal()).isEqualByComparingTo("600000");
        verify(reservaRepository).save(resultado);
        verify(cuentaRepository).save(cuentaCaptor.capture());
        Cuenta cuenta = cuentaCaptor.getValue();
        assertThat(cuenta.getEstado()).isEqualTo("ABIERTA");
        assertThat(cuenta.getTotal()).isEqualByComparingTo("600000");
        assertThat(cuenta.getReserva()).isSameAs(resultado);
        assertThat(resultado.getCuenta()).isSameAs(cuenta);
    }

    @Test
    public void ReservaService_modificar_ReservaYCuentaActualizadas() {
        // Arrange
        Reserva reserva = prepararReserva();
        Cuenta cuenta = reserva.getCuenta();
        cuenta.agregarDetalle(new DetalleCuenta(2, new BigDecimal("25000"), LocalDateTime.now()));
        TipoHabitacion tipo = reserva.getHabitacion().getTipoHabitacion();
        LocalDate entrada = reserva.getFechaInicio().plusDays(1);
        LocalDate salida = reserva.getFechaFin().plusDays(2);
        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        when(tipoHabitacionRepository.findById(tipo.getId())).thenReturn(Optional.of(tipo));
        when(habitacionRepository.buscarDisponibles(tipo.getId(), entrada, salida, 2,
                List.of("ACTIVA", "CONFIRMADA", "PENDIENTE"), reserva.getId()))
                .thenReturn(List.of(reserva.getHabitacion()));
        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Reserva resultado = reservaService.modificar(reserva.getCliente().getId(),
                reserva.getId(), tipo.getId(), entrada.toString(), salida.toString(), 2);

        // Assert
        assertThat(resultado.getId()).isEqualTo(4L);
        assertThat(resultado.getFechaInicio()).isEqualTo(entrada);
        assertThat(resultado.getFechaFin()).isEqualTo(salida);
        assertThat(resultado.getTotal()).isEqualByComparingTo("800000");
        assertThat(cuenta.getTotal()).isEqualByComparingTo("850000");
        assertThat(cuenta.getDetalles()).hasSize(1);
        verify(habitacionRepository).buscarDisponibles(tipo.getId(), entrada, salida, 2,
                List.of("ACTIVA", "CONFIRMADA", "PENDIENTE"), reserva.getId());
        verify(cuentaRepository).save(cuenta);
        verify(reservaRepository).save(reserva);
    }

    @Test
    public void ReservaService_crearConClienteInexistente_ReservaRechazada() {
        // Arrange
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        assertThatThrownBy(() -> reservaService.crear(
                999L, 2L, entrada.toString(), salida.toString(), 2))
                // Assert
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No se encontró cliente con id 999");
        verify(reservaRepository, never()).save(any(Reserva.class));
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    public void ReservaService_crearSinDisponibilidad_ReservaRechazada() {
        // Arrange
        Cliente cliente = prepararCliente();
        TipoHabitacion tipo = prepararTipo();
        LocalDate entrada = LocalDate.now().plusDays(10);
        LocalDate salida = entrada.plusDays(3);
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(tipoHabitacionRepository.findById(tipo.getId())).thenReturn(Optional.of(tipo));
        when(habitacionRepository.buscarDisponibles(tipo.getId(), entrada, salida, 2,
                List.of("ACTIVA", "CONFIRMADA", "PENDIENTE"), null))
                .thenReturn(List.of());

        // Act
        assertThatThrownBy(() -> reservaService.crear(cliente.getId(),
                tipo.getId(), entrada.toString(), salida.toString(), 2))
                // Assert
                .isInstanceOf(PeticionImposible.class)
                .hasMessage("No hay habitaciones disponibles para las fechas seleccionadas.");
        verify(reservaRepository, never()).save(any(Reserva.class));
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    private Cliente prepararCliente() {
        Cliente cliente = new Cliente("Cliente", "Prueba", "123456789",
                "3001234567", "cliente@macondo.test");
        cliente.setId(1L);
        return cliente;
    }

    private TipoHabitacion prepararTipo() {
        TipoHabitacion tipo = new TipoHabitacion("Tipo de prueba", "Habitacion de prueba",
                "foto.jpg", new BigDecimal("200000"), 2);
        tipo.setId(2L);
        return tipo;
    }

    private Habitacion prepararHabitacion(TipoHabitacion tipo) {
        Habitacion habitacion = new Habitacion("Habitacion 501", "PRUEBA",
                tipo.getPrecioNoche(), 2, "501", "DISPONIBLE", 5);
        habitacion.setId(3L);
        habitacion.aplicarTipo(tipo);
        return habitacion;
    }

    private Reserva prepararReserva() {
        LocalDate entrada = LocalDate.now().plusDays(10);
        Reserva reserva = new Reserva("MCD-TEST-CANCEL", entrada, entrada.plusDays(3),
                2, "CONFIRMADA", new BigDecimal("200000"), new BigDecimal("600000"));
        reserva.setId(4L);
        reserva.asignarCliente(prepararCliente());
        reserva.asignarHabitacion(prepararHabitacion(prepararTipo()));
        Cuenta cuenta = new Cuenta("ABIERTA", reserva.getTotal(), LocalDateTime.now());
        cuenta.setId(5L);
        cuenta.asignarReserva(reserva);
        return reserva;
    }
}
