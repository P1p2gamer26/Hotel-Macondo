package com.hotel.macondo.DTOs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.hotel.macondo.DTOs.Salida.ClienteDTO;
import com.hotel.macondo.entities.Cliente;

@Mapper
public interface ClienteMapper {
    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteDTO convert(Cliente cliente);
}