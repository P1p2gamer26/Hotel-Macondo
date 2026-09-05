package com.hotel.macondo.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.TipoHabitacion;

@Repository
public class TipoHabitacionRepository {

    private final Map<Long, TipoHabitacion> data = new LinkedHashMap<>();
    private long siguienteId = 5L;

    public TipoHabitacionRepository() {
        data.put(1L, crearTipo(1L, "Normal",
                "Refugio intimo con vista al jardin tropical, cama queen y aire acondicionado.",
                BigDecimal.valueOf(350000), 2));
        data.put(2L, crearTipo(2L, "Executive",
                "Espacio amplio con sala de trabajo, banera de lujo y vista al mar Caribe.",
                BigDecimal.valueOf(580000), 3));
        data.put(3L, crearTipo(3L, "VIP",
                "Suite boutique con terraza privada, jacuzzi exterior y servicio de mayordomo.",
                BigDecimal.valueOf(950000), 4));
        data.put(4L, crearTipo(4L, "Luxury",
                "Villa frente al mar con piscina privada y atencion personalizada 24 horas.",
                BigDecimal.valueOf(1800000), 6));
    }

    public Collection<TipoHabitacion> findAll() {
        return List.copyOf(data.values());
    }

    public TipoHabitacion findById(Long id) {
        return data.get(id);
    }

    public TipoHabitacion save(TipoHabitacion tipo) {
        if (tipo.getId() == null) {
            tipo.setId(siguienteId++);
        }
        data.put(tipo.getId(), tipo);
        return tipo;
    }

    public void delete(Long id) {
        data.remove(id);
    }

    private TipoHabitacion crearTipo(Long id, String nombre, String descripcion,
            BigDecimal precioNoche, Integer capacidadPersonas) {
        TipoHabitacion tipo = new TipoHabitacion(nombre, descripcion, precioNoche, capacidadPersonas);
        tipo.setId(id);
        return tipo;
    }
}
