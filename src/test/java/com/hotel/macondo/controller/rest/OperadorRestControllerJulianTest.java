package com.hotel.macondo.controller.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ReservaService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OperadorRestController.class)
class OperadorRestControllerJulianTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private OperadorService operadorService;
  @MockBean private ReservaService reservaService;

  @Test
  void OperadorRestController_buscarTodos_conUnOperador_devuelveListaYEstadoOk() throws Exception {
    // Arrange
    when(operadorService.buscarTodos()).thenReturn(List.of(new Operador("Ana", true)));

    // Act
    var respuesta = mockMvc.perform(get("/api/operador"));

    // Assert
    respuesta.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nombre").value("Ana"))
        .andExpect(jsonPath("$[0].activo").value(true));
  }

  @Test
  void OperadorRestController_crear_conActivoNuloOJsonInvalido_activaOperadorORetornaBadRequest() throws Exception {
    // Arrange
    when(operadorService.guardar(any(Operador.class))).thenAnswer(invocation -> invocation.getArgument(0));
    ArgumentCaptor<Operador> captor = ArgumentCaptor.forClass(Operador.class);

    // Act
    var respuestaValida = mockMvc.perform(post("/api/operador")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"nombre\":\"Ana\"}"));
    var respuestaInvalida = mockMvc.perform(post("/api/operador")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{nombre:}"));

    // Assert
    respuestaValida.andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(true));
    verify(operadorService).guardar(captor.capture());
    assertThat(captor.getValue().getActivo()).isTrue();
    respuestaInvalida.andExpect(status().isBadRequest());
  }

  @Test
  void OperadorRestController_cambiarEstado_cuandoExisteONoExiste_devuelveOkONotFound() throws Exception {
    // Arrange
    when(operadorService.cambiarEstado(1L)).thenReturn(new Operador("Ana", false));
    when(operadorService.cambiarEstado(999L)).thenReturn(null);

    // Act
    var respuestaExistente = mockMvc.perform(put("/api/operador/estado/{id}", 1L));
    var respuestaInexistente = mockMvc.perform(put("/api/operador/estado/{id}", 999L));

    // Assert
    respuestaExistente.andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
    respuestaInexistente.andExpect(status().isNotFound());
  }

  @Test
  void OperadorRestController_eliminar_cuandoExisteONoExiste_eliminaORetornaNotFoundSinEliminar() throws Exception {
    // Arrange
    when(operadorService.buscarPorId(1L)).thenReturn(new Operador("Ana", true));
    when(operadorService.buscarPorId(999L)).thenReturn(null);

    // Act
    var respuestaExistente = mockMvc.perform(delete("/api/operador/delete/{id}", 1L));

    // Assert
    respuestaExistente.andExpect(status().isOk());
    verify(operadorService).eliminar(1L);
    clearInvocations(operadorService);

    // Act
    var respuestaInexistente = mockMvc.perform(delete("/api/operador/delete/{id}", 999L));

    // Assert
    respuestaInexistente.andExpect(status().isNotFound());
    verify(operadorService, never()).eliminar(eq(999L));
    verify(operadorService, never()).eliminar(any());
  }

  @Test
  void OperadorRestController_pagarCuenta_conSolicitudOSinBody_devuelvePagoOBadRequest() throws Exception {
    // Arrange
    Pago pagoEfectivo = new Pago(new BigDecimal("120.00"), "EFECTIVO", LocalDateTime.now(), "PAGADO");
    when(reservaService.pagarCuentaParaOperador("RES-1", "EFECTIVO")).thenReturn(pagoEfectivo);

    // Act
    var respuestaConSolicitud = mockMvc.perform(post("/api/operador/cuenta/reserva/{num}/pago", "RES-1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"metodoPago\":\"EFECTIVO\"}"));
    var respuestaSinBody = mockMvc.perform(post("/api/operador/cuenta/reserva/{num}/pago", "RES-2")
        .contentType(MediaType.APPLICATION_JSON));

    // Assert
    respuestaConSolicitud.andExpect(status().isOk()).andExpect(jsonPath("$.metodoPago").value("EFECTIVO"));
    respuestaSinBody.andExpect(status().isBadRequest());
    verify(reservaService).pagarCuentaParaOperador("RES-1", "EFECTIVO");
    verify(reservaService, never()).pagarCuentaParaOperador(eq("RES-2"), any());
  }
}
