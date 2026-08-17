package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Testimonio;

@Repository
public class TestimonioRepository {

    private Map<Integer, Testimonio> data = new LinkedHashMap<>();

    public TestimonioRepository() {
        data.put(1, new Testimonio(1,
                "Hotel Macondo es un sueño hecho realidad. La combinación de lujo, naturaleza y la magia del Caribe colombiano me dejó sin palabras. Regresaré sin duda.",
                "Valentina Ospina", "Bogotá, Colombia", 5, "/images/IconoP1.avif"));
        data.put(2, new Testimonio(2,
                "Nunca imaginé que un hotel pudiera transmitir tanta poesía. El restaurante es excepcional y el servicio es de otro planeta. Una experiencia completamente transformadora.",
                "Martín Delgado", "Ciudad de México, México", 5, "/images/IconoP2.avif"));
        data.put(3, new Testimonio(3,
                "Vine buscando descanso y encontré magia pura. La suite VIP con terraza frente al mar y el spa con rituales caribeños fueron absolutamente perfectos.",
                "Sofía Benítez", "Madrid, España", 5, "/images/IconoP3.avif"));
    }

    public Collection<Testimonio> findAll() {
        return data.values();
    }

}
