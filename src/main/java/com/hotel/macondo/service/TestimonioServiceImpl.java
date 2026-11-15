package com.hotel.macondo.service;

import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.repository.TestimonioRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Transactional(readOnly = true)
public class TestimonioServiceImpl implements TestimonioService {

  @Autowired
  private TestimonioRepository repository;

  /** {@inheritDoc} */
  @Override
  public Collection<Testimonio> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }
}
