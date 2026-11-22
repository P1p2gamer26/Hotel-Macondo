package com.hotel.macondo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hotel.macondo.controller.rest.ComentarioRestController;
import com.hotel.macondo.dto.ComentarioResponse;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.UserEntity;
import com.hotel.macondo.repository.UsuarioRepository;
import com.hotel.macondo.security.ClienteAuthorization;
import com.hotel.macondo.security.JWTGenerator;
import com.hotel.macondo.security.JwtAuthEntryPoint;
import com.hotel.macondo.security.SecurityConfig;
import com.hotel.macondo.security.UserDetailsServiceImpl;
import com.hotel.macondo.service.ComentarioService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ComentarioRestController.class)
@Import({SecurityConfig.class, JWTGenerator.class, JwtAuthEntryPoint.class, ClienteAuthorization.class})
@ActiveProfiles("test")
class ComentarioRestControllerTest {

  @Autowired MockMvc mvc;
  @Autowired JWTGenerator jwtGenerator;
  @MockBean ComentarioService service;
  @MockBean UserDetailsServiceImpl userDetailsService;
  @MockBean UsuarioRepository usuarioRepository;

  static final String CUERPO = "{\"clienteId\":7,\"reservaId\":10,\"calificacion\":5,\"texto\":\"Genial\"}";

  @Test
  void postDevuelve201() throws Exception {
    when(service.crear(any())).thenReturn(new ComentarioResponse(1L, "Ana", 5, "Genial", LocalDateTime.now(), false));
    mvc.perform(post("/api/comentario").header(HttpHeaders.AUTHORIZATION, sesionCliente())
        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.nombre").value("Ana"));
  }

  @Test
  void postConReglaIncumplidaDevuelve400ConMensaje() throws Exception {
    when(service.crear(any())).thenThrow(new PeticionImposible("Esta reserva ya tiene un comentario."));
    mvc.perform(post("/api/comentario").header(HttpHeaders.AUTHORIZATION, sesionCliente())
        .contentType(MediaType.APPLICATION_JSON).content(CUERPO))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensaje").value("Esta reserva ya tiene un comentario."));
  }

  @Test
  void crearSinTokenDevuelve401() throws Exception {
    mvc.perform(post("/api/comentario").contentType(MediaType.APPLICATION_JSON).content(CUERPO))
        .andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Test
  void clienteNoPuedeUsarUnIdentificadorAjeno() throws Exception {
    String autorizacion = sesionCliente();
    mvc.perform(post("/api/comentario").header(HttpHeaders.AUTHORIZATION, autorizacion)
        .contentType(MediaType.APPLICATION_JSON).content(CUERPO.replace("\"clienteId\":7", "\"clienteId\":8")))
        .andExpect(status().isForbidden());
    mvc.perform(get("/api/comentario/cliente/8").header(HttpHeaders.AUTHORIZATION, autorizacion))
        .andExpect(status().isForbidden());
    mvc.perform(put("/api/comentario/1").header(HttpHeaders.AUTHORIZATION, autorizacion)
        .param("clienteId", "8").contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Editado\"}"))
        .andExpect(status().isForbidden());
    mvc.perform(delete("/api/comentario/1").header(HttpHeaders.AUTHORIZATION, autorizacion)
        .param("clienteId", "8"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @Test
  void borrarComoPropietarioDevuelve204() throws Exception {
    mvc.perform(delete("/api/comentario/1").header(HttpHeaders.AUTHORIZATION, sesionCliente())
        .param("clienteId", "7"))
        .andExpect(status().isNoContent());
  }

  @Test
  void soloAdministradorPuedePublicar() throws Exception {
    mvc.perform(put("/api/comentario/1/publicar")
        .header(HttpHeaders.AUTHORIZATION, sesion("cliente@macondo.test", "CLIENTE")))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);

    when(service.publicar(1L))
        .thenReturn(new ComentarioResponse(1L, "Ana", 5, "Genial", LocalDateTime.now(), true));
    mvc.perform(put("/api/comentario/1/publicar")
        .header(HttpHeaders.AUTHORIZATION, sesion("admin@macondo.test", "ADMINISTRADOR")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.publicado").value(true));
  }

  private String sesionCliente() {
    String correo = "cliente@macondo.test";
    Cliente cliente = new Cliente();
    cliente.setId(7L);
    UserEntity usuario = new UserEntity();
    usuario.setCliente(cliente);
    when(usuarioRepository.findByCorreoIgnoreCase(correo)).thenReturn(Optional.of(usuario));
    return sesion(correo, "CLIENTE");
  }

  private String sesion(String correo, String autoridad) {
    var usuario = User.withUsername(correo).password("sin-uso").authorities(autoridad).build();
    when(userDetailsService.loadUserByUsername(correo)).thenReturn(usuario);
    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    return "Bearer " + jwtGenerator.generateToken(authentication);
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
