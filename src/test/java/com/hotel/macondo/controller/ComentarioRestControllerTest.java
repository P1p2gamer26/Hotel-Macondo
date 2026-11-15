package com.hotel.macondo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hotel.macondo.controller.rest.ComentarioRestController;
import com.hotel.macondo.dto.ComentarioResponse;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.service.ComentarioService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ComentarioRestController.class)
@ActiveProfiles("test")
class ComentarioRestControllerTest {

  @Autowired MockMvc mvc;
  @MockBean ComentarioService service;

  static final String CUERPO = "{\"clienteId\":7,\"reservaId\":10,\"calificacion\":5,\"texto\":\"Genial\"}";

  @Test
  void postDevuelve201() throws Exception {
    when(service.crear(any())).thenReturn(new ComentarioResponse(1L, "Ana", 5, "Genial", LocalDateTime.now(), false));
    mvc.perform(post("/api/comentario").contentType(MediaType.APPLICATION_JSON).content(CUERPO))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nombre").value("Ana"));
  }

  @Test
  void postConReglaIncumplidaDevuelve400ConMensaje() throws Exception {
    when(service.crear(any())).thenThrow(new PeticionImposible("Esta reserva ya tiene un comentario."));
    mvc.perform(post("/api/comentario").contentType(MediaType.APPLICATION_JSON).content(CUERPO))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensaje").value("Esta reserva ya tiene un comentario."));
  }

  @Test
  void publicadosListaSinDatosSensibles() throws Exception {
    when(service.publicados()).thenReturn(List.of(new ComentarioResponse(1L, "Ana", 5, "Genial", LocalDateTime.now(), true)));
    mvc.perform(get("/api/comentario/publicados"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nombre").value("Ana"))
        .andExpect(jsonPath("$[0].correo").doesNotExist());
  }
}
