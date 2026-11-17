# Comprobación manual de contraseñas y DTO

La colección original `peticiones_postman.json` conserva las peticiones del proyecto.
La colección adicional `pruebas-seguridad.postman_collection.json` contiene peticiones HTTP sin scripts de pre-request ni de post-response.

## Uso en Postman

1. Arrancar el backend con el perfil `test` e importar la colección adicional. En Postman web seleccionar Desktop Agent.
2. Abrir cada petición y pulsar **Send**, siguiendo el orden. Comprobar manualmente **Status** y **Body**. No utilizar Runner ni Newman.
3. Tras el registro, copiar el campo `id` del cliente devuelto en la variable de colección `pruebaClienteId` y guardar su valor.
4. La variable `baseUrl` contiene `http://localhost:8080`.

Los cuerpos incluyen datos de ejemplo editables: correo `laura.postman@macondo.com`, cédula `1099000101`, contraseña inicial `ClaveElegidaMacondo!` y nueva contraseña `NuevaClaveMacondo!`. Son entradas para la prueba, no contraseñas impuestas por la aplicación. Si cambias el correo o las claves, actualiza las peticiones relacionadas. Si el cliente ya existe, usa otro correo y otra cédula de prueba.

| Petición | Estado esperado | Revisión manual |
| --- | --- | --- |
| 01. Registro | 200 | Datos del cliente, id y ausencia de contraseña. |
| 02. Login con clave recibida | 200 | Cliente correcto y DTO sin contraseña, también en objetos anidados. |
| 03. Login con clave incorrecta | 401 | Rechazo del acceso. |
| 04. Cambio de contraseña | 204 | Cuerpo vacío. |
| 05. Login con clave anterior | 401 | Rechazo de la clave anterior. |
| 06. Login con nueva clave | 200 | Acceso correcto y DTO sin contraseña. |
| 07. Eliminación opcional | 204 | Cuerpo vacío; eliminar solo el cliente creado para esta prueba. |

El registro y el login permiten comprobar el DTO de respuesta. El hash almacenado no se observa en el JSON de respuesta.
