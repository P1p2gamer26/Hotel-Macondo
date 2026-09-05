package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Testimonio;

@Repository
public class TestimonioRepository {

    private Map<Long, Testimonio> data = new LinkedHashMap<>();

    /** Carga los testimonios de prueba publicados. */
    public TestimonioRepository() {
        data.put(1L, crearTestimonio(1L,
                "Hotel Macondo es un sueño hecho realidad. La combinación de lujo, naturaleza y la magia del Caribe colombiano me dejó sin palabras. Regresaré sin duda.",
                "Valentina Ospina", "Bogotá, Colombia", 5, "/images/IconoP1.avif"));
        data.put(2L, crearTestimonio(2L,
                "Nunca imaginé que un hotel pudiera transmitir tanta poesía. El restaurante es excepcional y el servicio es de otro planeta. Una experiencia completamente transformadora.",
                "Martín Delgado", "Ciudad de México, México", 5, "/images/IconoP2.avif"));
        data.put(3L, crearTestimonio(3L,
                "Vine buscando descanso y encontré magia pura. La suite VIP con terraza frente al mar y el spa con rituales caribeños fueron absolutamente perfectos.",
                "Sofía Benítez", "Madrid, España", 5, "/images/IconoP3.avif"));
    }

    /** Retorna todos los testimonios publicados. */
    public Collection<Testimonio> findAll() {
        return data.values();
    }

    private Testimonio crearTestimonio(Long id, String texto, String nombre,
            String ciudad, int estrellas, String imagen) {
        Testimonio testimonio = new Testimonio(texto, nombre, ciudad, estrellas, imagen);
        testimonio.setId(id);
        return testimonio;
    }

}
