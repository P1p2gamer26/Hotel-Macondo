package com.hotel.macondo.service;

import java.util.Collection;

import com.hotel.macondo.entities.Habitacion;
import com.hotel.macondo.entities.Servicio;
import com.hotel.macondo.entities.Testimonio;

// Un solo servicio para el contenido del hotel (habitaciones, servicios y
// testimonios) porque los tres son datos de solo lectura que siempre se piden
// juntos en la landing; separarlos en tres interfaces no aportaba nada.
public interface HotelService {

    public Collection<Habitacion> buscarHabitaciones();

    public Habitacion buscarHabitacionPorId(Integer id);

    public Habitacion buscarHabitacionPorNombre(String nombre);

    public Collection<Habitacion> buscarHabitacionesPorPersonas(int personas);

    public Collection<Servicio> buscarServicios();

    public Servicio buscarServicioPorId(Integer id);

    public Collection<Testimonio> buscarTestimonios();

}
