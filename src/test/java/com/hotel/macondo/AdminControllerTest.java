package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.service.HabitacionService;
import com.hotel.macondo.service.OperadorService;
import com.hotel.macondo.service.ServicioService;

/**
 * Verifica que el panel administrativo solo sea accesible desde /admin y que
 * las tres pantallas rendericen y apliquen sus acciones.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    OperadorService operadorService;

    @Autowired
    ServicioService servicioService;

    @Autowired
    HabitacionService habitacionService;

    @Test
    void adminEntraDirectoAOperadores() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/operadores"));
    }

    @Test
    void renderizaLasTresPantallas() throws Exception {
        mockMvc.perform(get("/admin/operadores"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Recepcion principal")));

        mockMvc.perform(get("/admin/servicios"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Catalogo de experiencias")));

        mockMvc.perform(get("/admin/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Precio / noche")));
    }

    @Test
    void creaYEliminaOperador() throws Exception {
        int iniciales = operadorService.buscarTodos().size();

        mockMvc.perform(post("/admin/operadores").param("nombre", "Turno noche"))
                .andExpect(redirectedUrl("/admin/operadores"));

        assertEquals(iniciales + 1, operadorService.buscarTodos().size());

        Integer nuevoId = operadorService.buscarTodos().stream()
                .filter(operador -> "Turno noche".equals(operador.getNombre()))
                .findFirst().orElseThrow().getId();
        assertNotNull(nuevoId);

        mockMvc.perform(post("/admin/operadores/{id}/estado", nuevoId));
        assertFalse(operadorService.buscarPorId(nuevoId).getActivo());

        mockMvc.perform(post("/admin/operadores/{id}/eliminar", nuevoId));
        assertEquals(iniciales, operadorService.buscarTodos().size());
    }

    @Test
    void editaServicioYAlternaSuEstado() throws Exception {
        mockMvc.perform(post("/admin/servicios/{id}", 1)
                .param("nombre", "Spa renovado")
                .param("descripcion", "Rituales caribenos")
                .param("precio", "250000"))
                .andExpect(redirectedUrl("/admin/servicios"));

        assertEquals("Spa renovado", servicioService.buscarPorId(1).getNombre());
        assertEquals(0, java.math.BigDecimal.valueOf(250000)
                .compareTo(servicioService.buscarPorId(1).getPrecio()));

        mockMvc.perform(post("/admin/servicios/{id}/estado", 1));
        assertFalse(servicioService.buscarPorId(1).isActivo());
    }

    @Test
    void alternaDisponibilidadDeHabitacion() throws Exception {
        Habitacion habitacion = habitacionService.buscarPorId(1);

        mockMvc.perform(post("/admin/habitaciones/{id}/estado", 1))
                .andExpect(redirectedUrl("/admin/habitaciones"));
        assertEquals("NO_DISPONIBLE", habitacion.getEstado());

        mockMvc.perform(post("/admin/habitaciones/{id}/estado", 1));
        assertEquals("DISPONIBLE", habitacion.getEstado());
    }
}
