package com.hotel.macondo.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.macondo.controller.rest.HabitacionRestController;
import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.TipoHabitacion;
import com.hotel.macondo.service.HabitacionService;

@WebMvcTest(controllers = HabitacionRestController.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class HabitacionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private HabitacionService habitacionService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        public void HabitacionController_crear_Habitacion() throws Exception {

                // Arrange: Se crea la habitacion con su tipo y se simula la persistencia
                TipoHabitacion tipo = TipoHabitacion.builder()
                                .id(1L)
                                .nombre("Castaño Fundacional")
                                .precioNoche(new BigDecimal("280000"))
                                .capacidadPersonas(2)
                                .imagen("habitacion1.jpg")
                                .build();

                Habitacion habitacionEntrada = Habitacion.builder()
                                .numero("101")
                                .nombre("Habitación 101")
                                .piso(1)
                                .capacidad(2)
                                .precio(new BigDecimal("280000"))
                                .estado("DISPONIBLE")
                                .tipoHabitacion(tipo)
                                .build();

                Habitacion habitacionGuardada = Habitacion.builder()
                                .id(1L)
                                .numero("101")
                                .nombre("Habitación 101")
                                .piso(1)
                                .capacidad(2)
                                .precio(new BigDecimal("280000"))
                                .estado("DISPONIBLE")
                                .tipoHabitacion(tipo)
                                .build();

                when(habitacionService.guardar(Mockito.any(Habitacion.class), eq(1L)))
                                .thenReturn(habitacionGuardada);

                // Act: Peticion HTTP POST simulada para enviar el JSON en el cuerpo de la
                // peticion
                ResultActions response = mockMvc.perform(
                                post("/api/habitacion")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(habitacionEntrada)));

                // Assert: Se comprueba que se creo la habitacion y que el JSON tiene los datos
                // esperados
                response.andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.numero").value("101"))
                                .andExpect(jsonPath("$.nombre").value("Habitación 101"));
        }
}