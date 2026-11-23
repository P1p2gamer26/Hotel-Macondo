package com.hotel.macondo.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import com.hotel.macondo.controller.rest.ChatController;
import com.hotel.macondo.dto.ChatResponse;
import com.hotel.macondo.service.ChatService;

@WebMvcTest(controllers = ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Test
    void conversar_devuelveRespuestaDelAsistente() throws Exception {
        when(chatService.responder("Hola", List.of()))
                .thenReturn(new ChatResponse("¡Bienvenido a Hotel Macondo!"));

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensaje\":\"Hola\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.respuesta").value("¡Bienvenido a Hotel Macondo!"));

        verify(chatService).responder("Hola", List.of());
    }

    @Test
    void conversar_rechazaMensajeVacio() throws Exception {
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensaje\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El mensaje no puede estar vacío."));

        verifyNoInteractions(chatService);
    }

    @Test
    void conversar_rechazaMensajeDemasiadoLargo() throws Exception {
        String mensaje = "a".repeat(1001);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensaje\":\"" + mensaje + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El mensaje no puede superar los 1000 caracteres."));

        verifyNoInteractions(chatService);
    }
}
