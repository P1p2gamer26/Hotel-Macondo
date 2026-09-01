package com.hotel.macondo.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.macondo.entities.Cliente;
import com.hotel.macondo.repository.ClienteRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    /**
     * Crea el servicio con su repositorio de clientes.
     */
    @Autowired
    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Cliente> buscarTodos() {
        return repository.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Cliente buscarPorId(Integer id) {
        return repository.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente buscarPorCedula(String cedula) {
        return repository.findByCedula(cedula);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente guardar(Cliente cliente) {
        return repository.save(cliente);
    }

    /** {@inheritDoc} */
    @Override
    public void eliminar(Integer id) {
        repository.delete(id);
    }

    /** {@inheritDoc} */
    @Override
    public Cliente actualizarInformacion(Cliente cliente, Cliente informacion){
        cliente.actualizarInformacion(
                informacion.getNombre(),
                informacion.getApellido(),
                informacion.getTelefono(),
                informacion.getCorreo());
        return guardar(cliente);
    }
}
