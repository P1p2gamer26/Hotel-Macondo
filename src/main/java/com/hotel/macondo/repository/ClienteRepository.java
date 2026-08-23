package com.hotel.macondo.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hotel.macondo.entities.Cliente;

@Repository
public class ClienteRepository {

    private final Map<Integer, Cliente> data = new LinkedHashMap<>();
    private int siguienteId = 3;

    /**
     * Carga clientes de prueba para el almacenamiento temporal.
     */
    public ClienteRepository() {
        data.put(1, new Cliente(1, "Ana", "Torres", "1001001",
                "3001112233", "ana@macondo.com"));
        data.put(2, new Cliente(2, "Luis", "Mora", "1001002",
                "3004445566", "luis@macondo.com"));
    }

    /**
     * Retorna una copia de todos los clientes.
     */
    public Collection<Cliente> findAll() {
        return List.copyOf(data.values());
    }

    /**
     * Busca un cliente por su identificador interno.
     */
    public Cliente findById(Integer id) {
        return data.get(id);
    }

    /**
     * Busca un cliente por su documento.
     */
    public Cliente findByCedula(String cedula) {
        return data.values().stream()
                .filter(cliente -> cliente.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
    }

    /**
     * Crea o actualiza un cliente en memoria.
     */
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(siguienteId++);
        }
        data.put(cliente.getId(), cliente);
        return cliente;
    }

    /**
     * Elimina un cliente por identificador.
     */
    public void delete(Integer id) {
        data.remove(id);
    }
}
