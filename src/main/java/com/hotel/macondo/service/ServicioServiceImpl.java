package com.hotel.macondo.service;

import java.math.BigDecimal;
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
        return obtenerCatalogoActivo().stream()
                .filter(item -> !item.getId().equals(servicioActualId))
                .limit(limite)
                .toList();
    }

    @Override
    public List<Servicio> obtenerRecomendaciones(int limite) {
        return obtenerCatalogoActivo().stream()
                .limit(limite)
                .toList();
    }

    @Override
    public long contarActivos() {
        return obtenerCatalogoActivo().size();
    }

    @Override
    public Servicio actualizarDatos(Integer id, String nombre, String categoria,
            BigDecimal precio) {
        Servicio servicio = repository.findById(id);
        if (servicio == null) {
            return null;
        }
        servicio.actualizarDatos(nombre, categoria, precio);
        return repository.save(servicio);
    }

    @Override
    public Servicio cambiarEstado(Integer id) {
        Servicio servicio = repository.findById(id);
        if (servicio == null) {
            return null;
        }
        if (servicio.isActivo()) {
            servicio.desactivar();
        } else {
            servicio.activar();
        }
        return repository.save(servicio);
    }
}
