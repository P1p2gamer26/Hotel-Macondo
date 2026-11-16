package com.hotel.macondo.repository;

import com.hotel.macondo.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UserEntity, Long> {

  List<UserEntity> findAllByOrderByIdAsc();

  Optional<UserEntity> findByCorreoIgnoreCase(String correo);

  boolean existsByCorreoIgnoreCase(String correo);

  void deleteByCorreoIgnoreCase(String correo);
}
