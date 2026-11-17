# Resultado de la verificación

Fecha: 16 de noviembre de 2026. Rama: `feat/security-user-creation`.

## Postman

Colección: `pruebas-seguridad.postman_collection.json`.

Ejecutor: Newman 6.2.3, contra `http://localhost:8080`, con el perfil `test` y la implementación de esta rama.

| Medida | Resultado |
| --- | ---: |
| Iteraciones | 1 |
| Peticiones | 40 |
| Comprobaciones | 152 |
| Fallos de peticiones | 0 |
| Fallos de comprobaciones | 0 |
| Duración aproximada | 5,1 segundos |

Se verificaron registro con contraseña recibida, DTOs sin credenciales, errores de registro/login, cambio de clave y rechazo de la anterior, CRUD del operador con su contrato actual, consultas de reservas y catálogos, CORS y limpieza de registros de prueba.

El `403` de CORS corresponde al rechazo de un origen no permitido, no a una restricción por rol. La etapa inicial mantiene las rutas públicas; JWT y autorización por roles no forman parte de esta verificación.

Reporte completo de esta ejecución, fuera del repositorio: `C:/Users/Usuario/AppData/Local/Temp/macondo-postman-4cdb160601fb41878cafa69cc5879d0d/resultados.json`.

## Backend

Las pruebas se ejecutaron con JDK 21 y compilación objetivo Java 17, en una copia temporal con su propia base H2. Los dos escenarios Selenium E2E se compilaron, pero no se ejecutaron en navegador.

| Clase | Pruebas | Fallos / errores |
| --- | ---: | ---: |
| HabitacionControllerTest | 1 | 0 |
| OperadorRestControllerTest | 5 | 0 |
| ClienteRepositoryTest | 4 | 0 |
| ReservaRepositoryTest | 5 | 0 |
| ReservaServiceIntegracionTest | 5 | 0 |
| ReservaServiceMockTest | 5 | 0 |
| UsuarioServiceIntegracionTest | 4 | 0 |
| **Total** | **29** | **0** |

La primera ejecución detectó que la prueba de habitación serializaba una entidad cuyo `tipoHabitacion` es de solo entrada; se corrigió el JSON con ObjectMapper y se volvió a ejecutar esa prueba con éxito. Los resultados de la tabla consolidan las últimas ejecuciones de las siete clases.

Las cuatro pruebas nuevas comprueban la contraseña recibida almacenada con BCrypt, la relación usuario-cliente y su rol, el cambio de contraseña, los usuarios iniciales y el rechazo de registros sin contraseña.

Reportes Maven: `C:/Users/Usuario/AppData/Local/Temp/macondo-security-verificacion-d90b052716ee4c079d5318902fa15fc3/target/surefire-reports`.
