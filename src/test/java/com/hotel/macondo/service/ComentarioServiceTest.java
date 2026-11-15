package com.hotel.macondo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hotel.macondo.dto.ComentarioSolicitud;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Comentario;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.FormularioErroneoException;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.repository.ComentarioRepository;
import com.hotel.macondo.repository.ReservaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ComentarioServiceTest {

  @Mock ComentarioRepository repo;
  @Mock ReservaRepository reservas;
  @InjectMocks ComentarioService service;

  Reserva reserva;

  @BeforeEach
  void init() {
    Cliente c = new Cliente("Ana", "Perez", "1", "3", "a@a.com");
    c.setId(7L);
    reserva = new Reserva();
    reserva.setId(10L);
    reserva.setEstado("FINALIZADA");
    reserva.setCliente(c);
    when(reservas.findById(10L)).thenReturn(Optional.of(reserva));
    when(repo.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void creaComentarioValido_noPublicadoYSoloNombreDePila() {
    var r = service.crear(new ComentarioSolicitud(7L, 10L, 5, "  Excelente  "));
    assertThat(r.nombre()).isEqualTo("Ana");
    assertThat(r.texto()).isEqualTo("Excelente");
    assertThat(r.publicado()).isFalse();
  }

  @Test
  void rechazaClienteQueNoEsDuenio() {
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(8L, 10L, 5, "x")))
        .isInstanceOf(PeticionImposible.class);
  }

  @Test
  void rechazaReservaNoFinalizada() {
    reserva.setEstado("ACTIVA");
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(7L, 10L, 5, "x")))
        .isInstanceOf(PeticionImposible.class);
  }

  @Test
  void rechazaSegundoComentarioDeLaReserva() {
    when(repo.existsByReservaId(10L)).thenReturn(true);
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(7L, 10L, 5, "x")))
        .isInstanceOf(PeticionImposible.class);
  }

  @Test
  void rechazaCalificacionFueraDeRangoYTextoVacio() {
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(7L, 10L, 6, "x")))
        .isInstanceOf(FormularioErroneoException.class);
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(7L, 10L, 0, "x")))
        .isInstanceOf(FormularioErroneoException.class);
    assertThatThrownBy(() -> service.crear(new ComentarioSolicitud(7L, 10L, 3, "   ")))
        .isInstanceOf(FormularioErroneoException.class);
  }

  @Test
  void publicarMarcaPublicado() {
    Comentario c = new Comentario();
    c.setCliente(reserva.getCliente());
    c.setTexto("hola");
    c.setCalificacion(4);
    when(repo.findById(1L)).thenReturn(Optional.of(c));
    assertThat(service.publicar(1L).publicado()).isTrue();
  }
}
