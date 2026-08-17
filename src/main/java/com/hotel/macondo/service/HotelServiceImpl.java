package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.entities.Testimonio;
import com.hotel.macondo.repository.HabitacionRepository;
import com.hotel.macondo.repository.ServicioRepository;
import com.hotel.macondo.repository.TestimonioRepository;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    HabitacionRepository habitacionRepo;

    @Autowired
    ServicioRepository servicioRepo;

    @Autowired
    TestimonioRepository testimonioRepo;

    @Override
    public Collection<Habitacion> buscarHabitaciones() {
        return habitacionRepo.findAll();
    }

    @Override
    public Habitacion buscarHabitacionPorId(Integer id) {
        return habitacionRepo.findById(id);
    }

    @Override
    public Habitacion buscarHabitacionPorNombre(String nombre) {
        return habitacionRepo.findByNombre(nombre);
    }

    @Override
    public Collection<Habitacion> buscarHabitacionesPorPersonas(int personas) {
        return habitacionRepo.findByPersonas(personas);
    }

    @Override
    public Collection<Servicio> buscarServicios() {
        return servicioRepo.findAll();
    }

    @Override
    public Servicio buscarServicioPorId(Integer id) {
        return servicioRepo.findById(id);
    }

    @Override
    public Collection<Testimonio> buscarTestimonios() {
        return testimonioRepo.findAll();
    }

}
