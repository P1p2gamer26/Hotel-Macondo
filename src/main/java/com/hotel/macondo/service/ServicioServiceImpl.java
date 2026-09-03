package com.hotel.macondo.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.repository.ServicioRepository;

@Service
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;

    @Autowired
    public ServicioServiceImpl(ServicioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Collection<Servicio> buscarTodos() {
        return repository.findAll();
    }

    @Override
    public Servicio buscarPorId(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Servicio> obtenerCatalogoActivo() {
        return repository.findAll().stream()
                .filter(Servicio::isActivo)
                .sorted(Comparator.comparing(Servicio::getId))
                .toList();
    }

    @Override
    public List<String> obtenerCategoriasDisponibles() {
        return obtenerCatalogoActivo().stream()
                .map(Servicio::getCategoria)
                .distinct()
                .toList();
    }

    @Override
    public List<Servicio> obtenerRelacionados(Integer servicioActualId, int limite) {
        return repository.findAll().stream()
                .filter(Servicio::isActivo)
                .filter(item -> !item.getId().equals(servicioActualId))
                .limit(limite)
                .toList();
    }

    @Override
    public List<Servicio> obtenerRecomendaciones(int limite) {
        return repository.findAll().stream()
                .filter(Servicio::isActivo)
                .limit(limite)
                .toList();
    }
}