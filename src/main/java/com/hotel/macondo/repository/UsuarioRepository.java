package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  List<Usuario> findAllByOrderByIdAsc();

  Optional<Usuario> findByCorreoIgnoreCase(String correo);

  boolean existsByCorreoIgnoreCase(String correo);

  void deleteByCorreoIgnoreCase(String correo);
}
