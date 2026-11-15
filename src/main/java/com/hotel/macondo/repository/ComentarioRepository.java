package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Comentario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

  boolean existsByReservaId(Long reservaId);

  List<Comentario> findByPublicadoTrueOrderByFechaCreacionDesc();

  List<Comentario> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);
}
