package com.hotel.macondo.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hotel.macondo.entities.Cliente;

@DataJpaTest
@RunWith(SpringRunner.class)
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
}