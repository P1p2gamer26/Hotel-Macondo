package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Servicio;

@Repository
public class ServicioRepository {

    private Map<Integer, Servicio> data = new LinkedHashMap<>();

    /** Carga los servicios de prueba del catalogo. */
    public ServicioRepository() {
        data.put(1, new Servicio(1, "Spa & Bienestar",
                "Rituales caribeños, masajes frente al mar y un silencio que cura. El cuerpo descansa y el tiempo se detiene.",
                "/images/Spa.avif", false));
        data.put(2, new Servicio(2, "Restaurante Gourmet",
                "Sabores del Caribe colombiano elevados por nuestros chefs en un ambiente donde cada plato es una obra de arte.",
                "/images/Restaurante.avif", true));
        data.put(3, new Servicio(3, "Piscina Infinity",
                "Un borde infinito que se funde con el horizonte. Nadar aquí es nadar dentro del cielo.",
                "/images/Piscina.avif", false));
        data.put(4, new Servicio(4, "Playa Privada",
                "Arena blanca solo para nuestros huéspedes, con hamacas, sombrillas y cocteles a cualquier hora.",
                "/images/PlayaPriv.avif", false));
        data.put(5, new Servicio(5, "Tours Guiados",
                "Recorridos por la Sierra, los manglares y los pueblos donde nació el realismo mágico.",
                "/images/Guiado.avif", false));
        data.put(6, new Servicio(6, "Eventos & Bodas",
                "Celebraciones frente al mar con montaje, banquete y una noche que nadie olvidará jamás.",
                "/images/Eventos.avif", false));
    }

    /** Retorna todos los servicios del catalogo. */
    public Collection<Servicio> findAll() {
        return data.values();
    }

    /** Busca un servicio por identificador. */
    public Servicio findById(Integer id) {
        return data.get(id);
    }

}
