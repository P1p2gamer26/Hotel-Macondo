package com.hotel.macondo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import com.hotel.macondo.service.TipoHabitacionService;

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
    HabitacionService habitacionService;

    @Autowired
    TipoHabitacionService tipoHabitacionService;

    @Autowired
    OperadorService operadorService;

    @Autowired
    ServicioService servicioService;

    @Test
    void adminMuestraElTableroConSusTotales() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Bienvenido al panel")))
                // los totales del tablero salen de los datos en memoria
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Operadores,")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("disponibles")));
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
                .param("categoria", "Bienestar")
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
        // Se reconsulta despues de cada POST: si el servicio dejara de
        // persistir el cambio, el test lo detecta.
        mockMvc.perform(post("/admin/habitaciones/{id}/estado", 1))
                .andExpect(redirectedUrl("/admin/habitaciones"));
        assertEquals("NO_DISPONIBLE", habitacionService.buscarPorId(1).getEstado());

        mockMvc.perform(post("/admin/habitaciones/{id}/estado", 1));
        assertEquals("DISPONIBLE", habitacionService.buscarPorId(1).getEstado());
    }

    @Test
    void noGuardaHabitacionSinTipo() throws Exception {
        int iniciales = habitacionService.buscarTodas().size();

        mockMvc.perform(post("/admin/habitaciones/guardar")
                .param("nombre", "Sin tipo")
                .param("numero", "999")
                .param("estado", "DISPONIBLE")
                .param("piso", "9"))
                .andExpect(redirectedUrl("/admin/habitaciones"))
                .andExpect(flash().attributeExists("errorHabitacion"));

        assertEquals(iniciales, habitacionService.buscarTodas().size());
    }

    @Test
    void muestraFormularioInlineYGuardaHabitacion() throws Exception {
        // El listado incluye el formulario inline para crear/editar
        mockMvc.perform(get("/admin/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Nueva habitacion")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("form-habitacion")));

        int iniciales = habitacionService.buscarTodas().size();
        // La habitacion se guarda asignandole un tipo: descripcion, precio y
        // capacidad se derivan del tipo, pero el nombre es independiente.
        mockMvc.perform(post("/admin/habitaciones/guardar")
                .param("nombre", "Suite Familiar")
                .param("etiqueta", "NUEVA")
                .param("imagen", "/images/HabitacionLuxury.avif")
                .param("numero", "501")
                .param("estado", "DISPONIBLE")
                .param("piso", "5")
                .param("tipoId", "2"))
                .andExpect(redirectedUrl("/admin/habitaciones"));

        assertEquals(iniciales + 1, habitacionService.buscarTodas().size());
        Habitacion nueva = habitacionService.buscarTodas().stream()
                .filter(habitacion -> "501".equals(habitacion.getNumero()))
                .findFirst().orElseThrow();
        assertEquals("501", nueva.getNumero());
        // Nombre independiente del tipo.
        assertEquals("Suite Familiar", nueva.getNombre());
        // Datos derivados del tipo seleccionado.
        assertEquals(2, nueva.getTipoHabitacion().getId());
        assertEquals(580000, nueva.getPrecio());
        assertEquals(3, nueva.getCapacidad());
    }

    @Test
    void editarTipoPropagaLosCambiosALasHabitacionesAsociadas() throws Exception {
        mockMvc.perform(post("/admin/tipos_habitacion")
                .param("id", "1")
                .param("nombre", "Familiar")
                .param("descripcion", "Espacio redisenado para familias")
                .param("precioNoche", "420000")
                .param("capacidadPersonas", "4"))
                .andExpect(redirectedUrl("/admin/tipos_habitacion"));

        Habitacion habitacion = habitacionService.buscarPorId(1);
        // El nombre es independiente del tipo y no se sobrescribe.
        assertEquals("Normal", habitacion.getNombre());
        // Los datos comerciales del tipo si se propagan.
        assertEquals("Espacio redisenado para familias", habitacion.getDescripcion());
        assertEquals(420000, habitacion.getPrecio());
        assertEquals(4, habitacion.getCapacidad());
        assertEquals("Familiar", habitacion.getTipoHabitacion().getNombre());
    }

    @Test
    void noEliminaTipoQueTieneHabitacionesAsignadas() throws Exception {
        // El tipo 1 (Normal) esta asignado a la habitacion 1.
        int tipos = tipoHabitacionService.buscarTodos().size();

        mockMvc.perform(post("/admin/tipos_habitacion/{id}/eliminar", 1))
                .andExpect(redirectedUrl("/admin/tipos_habitacion"))
                .andExpect(flash().attributeExists("errorTipo"));

        assertEquals(tipos, tipoHabitacionService.buscarTodos().size());
        assertNotNull(tipoHabitacionService.buscarPorId(1));
    }

    @Test
    void eliminaTipoSinHabitacionesAsignadas() throws Exception {
        int iniciales = tipoHabitacionService.buscarTodos().size();
        // Crea un tipo nuevo sin habitaciones asociadas.
        mockMvc.perform(post("/admin/tipos_habitacion")
                .param("nombre", "Loft")
                .param("descripcion", "Estudio minimalista")
                .param("precioNoche", "300000")
                .param("capacidadPersonas", "2"))
                .andExpect(redirectedUrl("/admin/tipos_habitacion"));

        Integer nuevoId = tipoHabitacionService.buscarTodos().stream()
                .filter(tipo -> "Loft".equals(tipo.getNombre()))
                .findFirst().orElseThrow().getId();

        mockMvc.perform(post("/admin/tipos_habitacion/{id}/eliminar", nuevoId))
                .andExpect(redirectedUrl("/admin/tipos_habitacion"));

        assertEquals(iniciales, tipoHabitacionService.buscarTodos().size());
        assertNull(tipoHabitacionService.buscarPorId(nuevoId));
    }
}
