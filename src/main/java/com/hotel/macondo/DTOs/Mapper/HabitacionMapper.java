package com.hotel.macondo.DTOs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.hotel.macondo.DTOs.Salida.HabitacionDTO;
import com.hotel.macondo.entities.Habitacion;

@Mapper
public interface HabitacionMapper {
    HabitacionMapper INSTANCE = Mappers.getMapper(HabitacionMapper.class);

    HabitacionDTO convert(Habitacion habitacion);
}