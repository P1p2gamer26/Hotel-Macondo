package com.hotel.macondo.DTOs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.hotel.macondo.DTOs.Salida.PagoClienteResponse;
import com.hotel.macondo.DTOs.Salida.ReservaClienteDTO;
import com.hotel.macondo.DTOs.Salida.ReservaDetalleOperadorResponse;
import com.hotel.macondo.DTOs.Salida.ReservaOperadorResponse;
import com.hotel.macondo.entities.Cuenta;
import com.hotel.macondo.entities.DetalleCuenta;
import com.hotel.macondo.entities.Pago;
import com.hotel.macondo.entities.Reserva;

@Mapper
public interface ReservaMapper {
    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    // Convierte Reserva -> ReservaOperadorResponse
    ReservaOperadorResponse convertToOperadorResponse(Reserva reserva);

    // Convierte Reserva -> ReservaDetalleOperadorResponse
    ReservaDetalleOperadorResponse convertToDetalleOperadorResponse(Reserva reserva);

    // Mapea la lista "detalles" de Cuenta al campo "consumos" de CuentaDetalle
    @Mapping(target = "consumos", source = "detalles")
    ReservaDetalleOperadorResponse.CuentaDetalle convertCuenta(Cuenta cuenta);

    // Extrae solo el nombre del Servicio para ConsumoDetalle
    @Mapping(target = "servicio", source = "servicio.nombre")
    ReservaDetalleOperadorResponse.ConsumoDetalle convertConsumo(DetalleCuenta detalle);

    // Extrae solo el numeroReserva para PagoClienteResponse
    @Mapping(target = "numeroReserva", source = "cuenta.reserva.numeroReserva")
    PagoClienteResponse convertPagoCliente(Pago pago);

    // Convierte Reserva -> ReservaClienteDTO
    ReservaClienteDTO convertToClienteDTO(Reserva reserva);
}