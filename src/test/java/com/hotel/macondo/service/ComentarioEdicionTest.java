package com.hotel.macondo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotel.macondo.dto.ComentarioEdicion;
import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.entities.Comentario;
import com.hotel.macondo.entities.Reserva;
import com.hotel.macondo.errors.PeticionImposible;
import com.hotel.macondo.repository.ComentarioRepository;
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
class ComentarioEdicionTest {

  @Mock ComentarioRepository repo;
  @InjectMocks ComentarioService service;

  Comentario comentario;

  @BeforeEach
  void init() {
    Cliente ana = new Cliente("Ana", "Perez", "1", "3", "a@a.com");
    ana.setId(7L);
    Reserva reserva = new Reserva();
    reserva.setId(10L);
    reserva.setCliente(ana);
    comentario = new Comentario();
    comentario.setCliente(ana);
    comentario.setReserva(reserva);
    comentario.setCalificacion(3);
    comentario.setTexto("Bien");
    comentario.setPublicado(true);
    when(repo.findById(1L)).thenReturn(Optional.of(comentario));
    when(repo.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void duenioEditaYVuelveAPendiente() {
    var r = service.editar(1L, 7L, new ComentarioEdicion(5, "Excelente"));
    assertThat(r.calificacion()).isEqualTo(5);
    assertThat(r.texto()).isEqualTo("Excelente");
    assertThat(r.publicado()).isFalse();
    assertThat(r.reservaId()).isEqualTo(10L);
  }

  @Test
  void otroClienteNoPuedeEditar() {
    assertThatThrownBy(() -> service.editar(1L, 8L, new ComentarioEdicion(1, "x")))
        .isInstanceOf(PeticionImposible.class);
  }

  @Test
  void duenioElimina() {
    service.eliminar(1L, 7L);
    verify(repo).delete(comentario);
  }

  @Test
  void otroClienteNoPuedeEliminar() {
    assertThatThrownBy(() -> service.eliminar(1L, 8L)).isInstanceOf(PeticionImposible.class);
  }
}
