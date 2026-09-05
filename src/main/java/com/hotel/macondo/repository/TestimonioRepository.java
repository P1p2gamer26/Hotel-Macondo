package com.hotel.macondo.repository;

import com.hotel.macondo.entities.Testimonio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Long> {

  List<Testimonio> findAllByOrderByIdAsc();
}
