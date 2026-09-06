package com.hotel.macondo.service;

import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.repository.TestimonioRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TestimonioServiceImpl implements TestimonioService {

  private final TestimonioRepository repository;

  public TestimonioServiceImpl(TestimonioRepository repository) {
    this.repository = repository;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Testimonio> buscarTodos() {
    return repository.findAllByOrderByIdAsc();
  }
}
