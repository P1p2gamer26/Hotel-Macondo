package com.hotel.macondo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.macondo.entities.Rol;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.repository.RolRepository;
import com.hotel.macondo.repository.UsuarioRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OperadorCuentaIntegrationTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private RolRepository roles;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void creaLaCuentaPorCorreoYDesactivarRevocaElAcceso() throws Exception {
        String admin = bearerDePrueba("admin-alta@example.test", "ADMINISTRADOR");
        String operador = bearerDePrueba("operador-alta@example.test", "OPERADOR");
        String cuenta = """
                {"correo":"  Turno.Noche@example.test  ","nombre":"Turno de noche","contrasena":"Frase de ejemplo nocturna"}
                """;
        mvc.perform(post("/api/operador/cuentas").header(HttpHeaders.AUTHORIZATION, operador)
                        .contentType(MediaType.APPLICATION_JSON).content(cuenta))
                .andExpect(status().isForbidden());

        var creado = mvc.perform(post("/api/operador/cuentas").header(HttpHeaders.AUTHORIZATION, admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuenta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("turno.noche@example.test"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(creado).path("id").asLong();

        mvc.perform(post("/api/operador/cuentas").header(HttpHeaders.AUTHORIZATION, admin)
                        .contentType(MediaType.APPLICATION_JSON).content(cuenta))
                .andExpect(status().isBadRequest());

        String credenciales = """
                {"correo":"turno.noche@example.test","contrasena":"Frase de ejemplo nocturna"}
                """;
        var login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(credenciales))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("OPERADOR"))
                .andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + json.readTree(login).path("token").asText();
        mvc.perform(get("/api/reserva").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isOk());

        mvc.perform(put("/api/operador/estado/{id}", id).header(HttpHeaders.AUTHORIZATION, admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
        mvc.perform(get("/api/reserva").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(credenciales))
                .andExpect(status().isUnauthorized());
    }

    private String bearerDePrueba(String correo, String autoridad) throws Exception {
        Rol rol = roles.findByName(autoridad).orElseGet(() -> roles.save(new Rol(autoridad)));
        String contrasena = "Credencial de prueba de integración";
        usuarios.saveAndFlush(new UserEntity(correo, passwordEncoder.encode(contrasena), List.of(rol)));
        String datos = json.createObjectNode().put("correo", correo).put("contrasena", contrasena).toString();
        var respuesta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(datos))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(respuesta).path("token").asText();
    }
}
