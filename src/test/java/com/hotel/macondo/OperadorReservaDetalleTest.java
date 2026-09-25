package com.hotel.macondo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Verifica que el detalle de reserva del operador rescate los datos de la BD. */
@SpringBootTest
@AutoConfigureMockMvc
class OperadorReservaDetalleTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void detalleMuestraClienteHabitacionYServicios() throws Exception {
    // MHC-2025-001 la crea el DataLoader con cliente, habitacion, un servicio y un pago
    mockMvc
        .perform(get("/operador/reservas/MHC-2025-001"))
        .andExpect(status().isOk())
        .andExpect(view().name("operador/detalle_reserva"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("MHC-2025-001")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Servicios contratados")))
        .andExpect(content().string(org.hamcrest.Matchers.not(
            org.hamcrest.Matchers.containsString("Sin servicios contratados"))))
        .andExpect(content().string(org.hamcrest.Matchers.not(
            org.hamcrest.Matchers.containsString("Sin pagos registrados"))));
  }

  @Test
  void reservaInexistenteVaALaPaginaDeError() throws Exception {
    mockMvc
        .perform(get("/operador/reservas/NO-EXISTE"))
        .andExpect(view().name("error"));
  }

  @Test
  void listadoMuestraMetricasReales() throws Exception {
    mockMvc
        .perform(get("/operador/reservas"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("/operador/reservas/MHC-2025-001")));
  }
}
