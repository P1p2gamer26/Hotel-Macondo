package com.hotel.macondo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.macondo.DTOs.Entrada.PagoSolicitud;
import com.hotel.macondo.controller.rest.OperadorRestController;
import com.hotel.macondo.entities.Operador;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ReservaService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(OperadorRestController.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class OperadorRestControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private OperadorService operadorService;

        @MockBean
        private ReservaService reservaService;

        @Test
        public void OperadorRestController_buscarTodos_ListaDeOperadores() throws Exception {
                // Arrange
                when(operadorService.buscarTodos()).thenReturn(List.of(new Operador("Ana", true)));

                // Act
                ResultActions respuesta = mockMvc.perform(get("/api/operador"));

                // Assert
                respuesta.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                                .andExpect(jsonPath("$[0].activo").value(true));
        }

        @Test
        public void OperadorRestController_crear_OperadorCreadoOJsonRechazado() throws Exception {
                // Arrange
                Operador entrada = new Operador("Ana", null);
                when(operadorService.guardar(any(Operador.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                ArgumentCaptor<Operador> captor = ArgumentCaptor.forClass(Operador.class);

                // Act
                ResultActions valida = mockMvc.perform(post("/api/operador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(entrada)));
                ResultActions invalida = mockMvc.perform(post("/api/operador")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{nombre:}"));

                // Assert
                valida.andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.nombre").value("Ana"))
                                .andExpect(jsonPath("$.activo").value(true));
                verify(operadorService).guardar(captor.capture());
                assertThat(captor.getValue().getNombre()).isEqualTo("Ana");
                assertThat(captor.getValue().getActivo()).isTrue();
                invalida.andExpect(status().isBadRequest());
        }

        @Test
        public void OperadorRestController_cambiarEstado_OperadorActualizadoONoEncontrado() throws Exception {
                // Arrange
                when(operadorService.cambiarEstado(1L)).thenReturn(new Operador("Ana", false));
                when(operadorService.cambiarEstado(999L)).thenReturn(null);

                // Act
                ResultActions existente = mockMvc.perform(put("/api/operador/estado/{id}", 1L));
                ResultActions inexistente = mockMvc.perform(put("/api/operador/estado/{id}", 999L));

                // Assert
                existente.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.activo").value(false));
                inexistente.andExpect(status().isNotFound());
        }

        @Test
        public void OperadorRestController_eliminar_OperadorEliminadoONoEncontrado() throws Exception {
                // Arrange
                when(operadorService.buscarPorId(1L)).thenReturn(new Operador("Ana", true));
                when(operadorService.buscarPorId(999L)).thenReturn(null);

                // Act
                ResultActions existente = mockMvc.perform(delete("/api/operador/delete/{id}", 1L));
                ResultActions inexistente = mockMvc.perform(delete("/api/operador/delete/{id}", 999L));

                // Assert
                existente.andExpect(status().isNoContent())
                                .andExpect(content().string(""));
                verify(operadorService).eliminar(1L);
                inexistente.andExpect(status().isNotFound());
                verify(operadorService, never()).eliminar(999L);
        }

        @Test
        public void OperadorRestController_pagarCuenta_PagoRegistradoOSolicitudRechazada() throws Exception {
                // Arrange
                PagoSolicitud solicitud = new PagoSolicitud("EFECTIVO");
                Pago pago = new Pago(new BigDecimal("120.00"), "EFECTIVO", LocalDateTime.now(), "PAGADO");
                when(reservaService.pagarCuentaParaOperador("RES-1", "EFECTIVO")).thenReturn(pago);

                // Act
                ResultActions valida = mockMvc.perform(post("/api/operador/cuenta/reserva/{numero}/pago", "RES-1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(solicitud)));
                ResultActions sinCuerpo = mockMvc.perform(post("/api/operador/cuenta/reserva/{numero}/pago", "RES-2")
                                .contentType(MediaType.APPLICATION_JSON));

                // Assert
                valida.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.metodoPago").value("EFECTIVO"))
                                .andExpect(jsonPath("$.monto").value(120.00));
                sinCuerpo.andExpect(status().isBadRequest());
                verify(reservaService).pagarCuentaParaOperador("RES-1", "EFECTIVO");
                verify(reservaService, never()).pagarCuentaParaOperador(eq("RES-2"), any());
        }
}
