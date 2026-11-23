package com.hotel.macondo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.hotel.macondo.dto.ChatActionType;
import com.hotel.macondo.dto.ChatResponse;

class ChatNavigationTest {

    private ChatServiceImpl chatService;
    private RestClient ollamaClient;
    private TipoHabitacionService tipoHabitacionService;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = mock(RestClient.Builder.class, RETURNS_SELF);
        ollamaClient = mock(RestClient.class);
        tipoHabitacionService = mock(TipoHabitacionService.class);
        reservaService = mock(ReservaService.class);
        when(builder.build()).thenReturn(ollamaClient);
        chatService = new ChatServiceImpl(
                builder,
                "http://localhost:11434",
                "llama3.2",
                mock(ServicioService.class),
                tipoHabitacionService,
                reservaService);
    }

    @Test
    void dondeReservarIncluyeAccionDeHabitaciones() {
        ChatResponse respuesta = chatService.responder("¿Dónde puedo reservar?", List.of());

        assertEquals(List.of(ChatActionType.RESERVAR), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("Habitaciones"));
    }

    @Test
    void typoAlPedirHacerReservaIncluyeAccionDeHabitaciones() {
        ChatResponse respuesta = chatService.responder("Quiero realizar la reseva", List.of());

        assertEquals(List.of(ChatActionType.RESERVAR), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("flujo oficial"));
        verifyNoInteractions(ollamaClient);
    }

    @Test
    void solicitudDePagoNoPasaPorElModeloNiPideDatosBancarios() {
        ChatResponse respuesta = chatService.responder(
                "Quiero realizar la reserva y pagar con tarjeta",
                List.of());

        assertEquals(
                List.of(ChatActionType.RESERVAR, ChatActionType.PAGO_SEGURO),
                respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("no compartas"));
        assertTrue(respuesta.respuesta().contains("formularios oficiales"));
        verifyNoInteractions(ollamaClient, tipoHabitacionService, reservaService);
    }

    @Test
    void reservaConFechaAmbiguaNoInventaDisponibilidadNiLlamaAlModelo() {
        ChatResponse respuesta = chatService.responder(
                "Quiero una reserva de 4 días, empieza el 23 de noviembre, habitación Estándar para 2 personas",
                List.of());

        assertEquals(List.of(ChatActionType.RESERVAR), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("no puedo inferir el año"));
        assertFalse(respuesta.respuesta().contains("disponible"));
        verifyNoInteractions(ollamaClient, tipoHabitacionService, reservaService);
    }

    @Test
    void dondeRegistrarseIncluyeAccionDeRegistro() {
        ChatResponse respuesta = chatService.responder("¿Dónde me registro?", List.of());

        assertEquals(List.of(ChatActionType.REGISTRO), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("Registro"));
    }

    @Test
    void comoCancelarIncluyeAccionDeMisReservas() {
        ChatResponse respuesta = chatService.responder("¿Cómo puedo cancelar mi reserva?", List.of());

        assertEquals(List.of(ChatActionType.CANCELAR_RESERVA), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("Mis reservas"));
    }

    @Test
    void comoHacerCheckoutIncluyeAccionDeCheckout() {
        ChatResponse respuesta = chatService.responder("¿Cómo puedo hacer checkout?", List.of());

        assertEquals(List.of(ChatActionType.CHECKOUT), respuesta.acciones());
        assertTrue(respuesta.respuesta().contains("personal del hotel"));
    }
}
