package com.hotel.macondo.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hotel.macondo.entities.Cliente;

@DataJpaTest
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    public void ClienteRepository_save_Cliente() {

        // Arrange: Se crea un cliente con datos de prueba
        Cliente cliente = Cliente.builder()
                .nombre("Juan")
                .apellido("Buendia")
                .cedula("123456789")
                .telefono("3001234567")
                .correo("juan.buendia@macondo.com")
                .build();

        // Act: Se guarda el cliente en la base de datos utilizando el repositorio
        Cliente savedCliente = clienteRepository.save(cliente);

        // 3. Assert: Se verifica que el cliente se haya guardado correctamente y que
        // los datos sean los esperados
        Assertions.assertThat(savedCliente).isNotNull();
        Assertions.assertThat(savedCliente.getId()).isNotNull();
        Assertions.assertThat(savedCliente.getNombre()).isEqualTo("Juan");
        Assertions.assertThat(savedCliente.getCedula()).isEqualTo("123456789");
    }

    @Test
    public void ClienteRepository_findAll_devuelveTodosLosClientes() {
        // Arrange
        clienteRepository.save(Cliente.builder().nombre("Juan").apellido("Buendia").cedula("111")
                .telefono("3001").correo("a@macondo.com").build());
        clienteRepository.save(Cliente.builder().nombre("Ursula").apellido("Iguaran").cedula("222")
                .telefono("3002").correo("b@macondo.com").build());

        // Act
        List<Cliente> clientes = clienteRepository.findAll();

        // Assert
        Assertions.assertThat(clientes).hasSize(2);
    }

    @Test
    public void ClienteRepository_update_cambiaElNombre() {
        // Arrange
        Cliente guardado = clienteRepository.save(Cliente.builder().nombre("Juan").apellido("Buendia")
                .cedula("111").telefono("3001").correo("a@macondo.com").build());

        // Act
        guardado.setNombre("Aureliano");
        clienteRepository.save(guardado);
        Cliente encontrado = clienteRepository.findById(guardado.getId()).get();

        // Assert
        Assertions.assertThat(encontrado.getNombre()).isEqualTo("Aureliano");
    }

    @Test
    public void ClienteRepository_delete_yFindByIdVacio() {
        // Arrange
        Cliente guardado = clienteRepository.save(Cliente.builder().nombre("Juan").apellido("Buendia")
                .cedula("111").telefono("3001").correo("a@macondo.com").build());
        Long id = guardado.getId();

        // Act
        clienteRepository.delete(guardado);
        Optional<Cliente> resultado = clienteRepository.findById(id);

        // Assert
        Assertions.assertThat(resultado).isEmpty();
    }
}
