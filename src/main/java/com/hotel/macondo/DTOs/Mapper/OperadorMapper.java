package com.hotel.macondo.DTOs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.hotel.macondo.DTOs.Salida.OperadorDTO;
import com.hotel.macondo.entities.Operador;

@Mapper
public interface OperadorMapper {
    OperadorMapper INSTANCE = Mappers.getMapper(OperadorMapper.class);

    @Mapping(source = "usuario.correo", target = "correo")
    OperadorDTO convert(Operador operador);
}
