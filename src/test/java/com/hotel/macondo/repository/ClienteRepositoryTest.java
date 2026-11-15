package com.hotel.macondo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hotel.macondo.entities.Cliente;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntityManager entityManager;

    private Cliente cliente;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente("Juan", "Buendia", "123456789",
                "3001234567", "juan.buendia@macondo.test");
    }

    @Test
    public void ClienteRepository_save_Cliente() {
        // Arrange
        long cantidadInicial = clienteRepository.count();

        // Act
        Cliente guardado = clienteRepository.save(cliente);
        Long id = guardado.getId();
        entityManager.flush();
        entityManager.clear();

        // Assert
        Cliente encontrado = clienteRepository.findById(id).orElseThrow();
        assertThat(encontrado.getNombre()).isEqualTo("Juan");
        assertThat(encontrado.getCedula()).isEqualTo("123456789");
        assertThat(clienteRepository.count()).isEqualTo(cantidadInicial + 1);
    }

    @Test
    public void ClienteRepository_findById_ClienteEncontrado() {
        // Arrange
        Long id = clienteRepository.saveAndFlush(cliente).getId();
        entityManager.clear();

        // Act
        var encontrado = clienteRepository.findById(id);

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.orElseThrow().getId()).isEqualTo(id);
        assertThat(encontrado.orElseThrow().getCorreo()).isEqualTo("juan.buendia@macondo.test");
        assertThat(encontrado.orElseThrow().getTelefono()).isEqualTo("3001234567");
    }

    @Test
    public void ClienteRepository_save_ClienteActualizado() {
        // Arrange
        Cliente guardado = clienteRepository.saveAndFlush(cliente);
        Long id = guardado.getId();
        long cantidadInicial = clienteRepository.count();

        // Act
        guardado.setTelefono("3159876543");
        clienteRepository.saveAndFlush(guardado);
        entityManager.clear();

        // Assert
        Cliente actualizado = clienteRepository.findById(id).orElseThrow();
        assertThat(actualizado.getId()).isEqualTo(id);
        assertThat(actualizado.getTelefono()).isEqualTo("3159876543");
        assertThat(actualizado.getCorreo()).isEqualTo("juan.buendia@macondo.test");
        assertThat(clienteRepository.count()).isEqualTo(cantidadInicial);
    }

    @Test
    public void ClienteRepository_deleteById_ClienteEliminado() {
        // Arrange
        Long id = clienteRepository.saveAndFlush(cliente).getId();
        long cantidadInicial = clienteRepository.count();

        // Act
        clienteRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(clienteRepository.findById(id)).isEmpty();
        assertThat(clienteRepository.count()).isEqualTo(cantidadInicial - 1);
    }
}
