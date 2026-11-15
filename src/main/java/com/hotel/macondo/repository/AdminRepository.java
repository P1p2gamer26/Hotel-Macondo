package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Admin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

  List<Admin> findAllByOrderByIdAsc();
}
