package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Cliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

  List<Cliente> findAllByOrderByIdAsc();

  Optional<Cliente> findByCedula(String cedula);
}
