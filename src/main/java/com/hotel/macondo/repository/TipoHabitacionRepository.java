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

    private final Map<Integer, TipoHabitacion> data = new LinkedHashMap<>();
    private int siguienteId = 5;

    public TipoHabitacionRepository() {
        data.put(1, new TipoHabitacion(1, "Normal",
                "Refugio intimo con vista al jardin tropical, cama queen y aire acondicionado.",
                BigDecimal.valueOf(350000), 2));
        data.put(2, new TipoHabitacion(2, "Executive",
                "Espacio amplio con sala de trabajo, banera de lujo y vista al mar Caribe.",
                BigDecimal.valueOf(580000), 3));
        data.put(3, new TipoHabitacion(3, "VIP",
                "Suite boutique con terraza privada, jacuzzi exterior y servicio de mayordomo.",
                BigDecimal.valueOf(950000), 4));
        data.put(4, new TipoHabitacion(4, "Luxury",
                "Villa frente al mar con piscina privada y atencion personalizada 24 horas.",
                BigDecimal.valueOf(1800000), 6));
    }

    public Collection<TipoHabitacion> findAll() {
        return List.copyOf(data.values());
    }

    public TipoHabitacion findById(Integer id) {
        return data.get(id);
    }

    public TipoHabitacion save(TipoHabitacion tipo) {
        if (tipo.getId() == null) {
            tipo.setId(siguienteId++);
        }
        data.put(tipo.getId(), tipo);
        return tipo;
    }

    public void delete(Integer id) {
        data.remove(id);
    }
}