package com.hotel.macondo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Comprueba que las pantallas publicas renderizan con los datos que ahora
 * arman los servicios, no los controladores.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaginasPublicasTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void portadaRecibeLosTiposParaElBuscador() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("habitaciones", "tiposHabitacion",
                        "servicios", "testimonios"));
    }

    @Test
    void catalogoDeServiciosSoloTraeActivosYSusCategorias() throws Exception {
        mockMvc.perform(get("/servicios"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("servicios", "categorias"));

        // Se usa el servicio 2: AdminControllerTest desactiva el 1 y el
        // contexto de Spring se comparte entre clases de prueba.
        mockMvc.perform(get("/servicios/{id}", 2))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("servicio", "relacionados"));
    }

    @Test
    void catalogoDeHabitacionesYSuDetalle() throws Exception {
        mockMvc.perform(get("/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("habitaciones"));

        mockMvc.perform(get("/habitaciones/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("habitacion"));
    }

    @Test
    void reservasDelClienteSalenDelServicioDeReservas() throws Exception {
        mockMvc.perform(get("/cliente/{id}/reservas", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cliente", "reservas"));

        mockMvc.perform(get("/cliente/{id}/historial", 1))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cliente", "historial"));

        mockMvc.perform(get("/cliente/{id}/reservas", 999))
                .andExpect(status().isNotFound());
    }
}
