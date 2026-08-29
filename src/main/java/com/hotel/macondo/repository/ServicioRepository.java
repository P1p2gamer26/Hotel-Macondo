package com.hotel.macondo.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Servicio;

@Repository
public class ServicioRepository {

    private final Map<Integer, Servicio> data = new LinkedHashMap<>();

    /** Carga el catalogo inicial de servicios del hotel. */
    public ServicioRepository() {
        data.put(1, crearServicio(1, "Spa & Bienestar", "Bienestar", "60-90 min",
                "Sumérgete en una experiencia sensorial de relajación profunda con masajes terapéuticos, aromaterapia caribeña y tratamientos faciales con ingredientes de la región.",
                "Nuestro spa combina técnicas milenarias de bienestar con ingredientes naturales del Caribe: aceite de coco, flores de cayena, sales del mar y hierbas aromáticas de la Sierra Nevada. Cada sesión está diseñada para despertar los sentidos y restaurar el equilibrio del cuerpo y la mente. Disfruta de masajes relajantes, tratamientos de hidroterapia, envolturas de arcilla caribeña y aromaterapia en cabinas privadas con vista al jardín tropical.",
                "/images/Spa.avif", true, 120000,
                "Lunes a domingo: 8:00 a.m. - 8:00 p.m.",
                List.of("Masaje de 60 min", "Aromaterapia incluida", "Baño de vapor",
                        "Infusión de hierbas", "Cabina privada", "Toallas de lujo"),
                List.of("Masajes", "Hidroterapia", "Aromaterapia",
                        "Tratamientos faciales")));
        data.put(2, crearServicio(2, "Restaurante Gourmet", "Gastronomía",
                "Almuerzo & cena",
                "La cocina caribeña elevada a su máxima expresión. Platos elaborados con productos locales y técnicas contemporáneas, con vista panorámica al mar.",
                "Una experiencia culinaria donde los sabores del Caribe se encuentran con técnicas contemporáneas. Nuestro menú celebra los productos locales, la pesca del día y las recetas que han pasado de generación en generación.",
                "/images/Restaurante.avif", false, 85000,
                "Todos los días: 12:00 m. - 10:30 p.m.",
                List.of("Menú degustación", "Maridaje de vinos", "Vista al mar",
                        "Reserva garantizada"),
                List.of("Cocina caribeña", "Maridaje", "Cena", "Productos locales")));
        data.put(3, crearServicio(3, "Piscina Infinity", "Bienestar", "Acceso diario",
                "Nada hacia el horizonte infinito del Caribe desde nuestra piscina de borde abierto frente al mar.",
                "Una piscina serena frente al Caribe, con camastros, bebidas frescas y atención durante todo el día.",
                "/images/Piscina.avif", false, 70000,
                "Lunes a domingo: 7:00 a.m. - 9:00 p.m.",
                List.of("Camastro reservado", "Toallas", "Bebida de bienvenida",
                        "Servicio junto a la piscina"),
                List.of("Piscina", "Descanso", "Vista al mar")));
        data.put(4, crearServicio(4, "Playa Privada", "Bienestar", "Acceso diario",
                "Arena blanca, sombra natural y el Caribe a pocos pasos de tu habitación.",
                "Disfruta de un sector reservado de playa con servicio personalizado, zonas de descanso y actividades tranquilas frente al mar.",
                "/images/PlayaPriv.avif", false, 60000,
                "Lunes a domingo: 7:00 a.m. - 6:00 p.m.",
                List.of("Sombrilla", "Camastro", "Toalla", "Bebida de bienvenida"),
                List.of("Playa", "Descanso", "Caribe")));
        data.put(5, crearServicio(5, "Tours Guiados", "Aventura",
                "Medio día / Día completo",
                "Descubre los rincones más mágicos de la costa caribeña con nuestros guías expertos. Cartagena histórica, islas del Rosario, manglares y más.",
                "Recorre Cartagena y sus alrededores con anfitriones locales que conocen cada historia, sabor y paisaje de la región.",
                "/images/Guiado.avif", false, 95000,
                "Salidas programadas todos los días.",
                List.of("Guía bilingüe", "Transporte incluido", "Snacks y agua",
                        "Seguro de viaje"),
                List.of("Cartagena", "Islas", "Manglares", "Historia")));
        data.put(6, crearServicio(6, "Eventos Especiales", "Exclusivo", "A medida",
                "Celebra los momentos más importantes de tu vida en el escenario perfecto. Bodas, aniversarios, reuniones corporativas con decoración y catering de lujo.",
                "Creamos celebraciones a medida frente al mar, desde encuentros privados hasta bodas y eventos corporativos completos.",
                "/images/Eventos.avif", false, 3500000,
                "Programación personalizada.",
                List.of("Coordinador de eventos", "Decoración temática",
                        "Catering gourmet", "Fotografía profesional"),
                List.of("Bodas", "Celebraciones", "Eventos corporativos")));
    }

    /** Retorna todos los servicios del catalogo. */
    public Collection<Servicio> findAll() {
        return data.values();
    }

    /** Busca un servicio por identificador. */
    public Servicio findById(Integer id) {
        return data.get(id);
    }

    private Servicio crearServicio(Integer id, String nombre, String categoria,
            String duracion, String descripcion, String descripcionDetalle,
            String imagen, boolean destacado, long precio, String horario,
            List<String> incluidos, List<String> etiquetas) {
        Servicio servicio = new Servicio(id, nombre, descripcion, categoria, imagen, destacado,
                BigDecimal.valueOf(precio), true);
        servicio.setCategoria(categoria);
        servicio.setDuracion(duracion);
        servicio.setDescripcionDetalle(descripcionDetalle);
        servicio.setHorario(horario);
        servicio.setIncluidos(incluidos);
        servicio.setEtiquetas(etiquetas);
        return servicio;
    }
}
