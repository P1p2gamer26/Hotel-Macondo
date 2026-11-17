# Pruebas de seguridad inicial y DTO

## Alcance

`pruebas-seguridad.postman_collection.json` contiene 40 peticiones con comprobaciones automáticas. Usa únicamente las rutas que ya existían: no cambia el cuerpo de creación de operadores ni requiere cambios de Angular.

La configuración es la primera etapa de la clase: contraseñas BCrypt, CORS y filtros iniciales. Las rutas siguen públicas con `permitAll()`. Obtener un rol en el login no equivale a autorizar endpoints. JWT, restricciones por rol y sesión quedan para la siguiente etapa.

## Ejecutar desde Postman

1. Arrancar el backend con el perfil `test`: `mvnw.cmd -Dspring-boot.run.profiles=test spring-boot:run`.
2. Importar `pruebas-seguridad.postman_collection.json` desde este directorio.
3. En Postman web, seleccionar Desktop Agent para acceder a localhost.
4. Mantener la variable de colección `baseUrl` en `http://localhost:8080`.
5. Abrir el Runner y ejecutar la colección completa en orden, una iteración.
6. Revisar los estados HTTP y las comprobaciones en los resultados.

La primera petición genera correo, cédula y contraseñas de prueba con un sufijo único. El registro guarda el ID devuelto para las peticiones posteriores. La suite elimina solamente el cliente y el operador creados por esa ejecución. Debe utilizarse en la base de pruebas; si se interrumpe antes del final, esos registros pueden permanecer hasta reiniciar el perfil `test`.

Los logins iniciales de administrador y operador utilizan las credenciales que ya tenía el DataLoader. El cliente de la suite usa una contraseña recibida por registro, no una clave fija del servicio.

## Ejecución automática

Newman es el ejecutor de colecciones v2.1 de Postman. Desde la raíz del backend:

```powershell
npm exec --yes --package=newman -- newman run postman/pruebas-seguridad.postman_collection.json --reporters cli,json --reporter-json-export "$env:TEMP/macondo-postman-resultados.json"
```

El reporte completo se escribe fuera del repositorio porque incluye las peticiones y credenciales de los usuarios de prueba.

## Comprobaciones

- DTOs sin contraseñas ni credenciales anidadas.
- Login inicial de administrador y operador.
- Registro de cliente con contraseña recibida, correo duplicado e inválido, contraseña ausente y en blanco.
- Login correcto, clave equivocada, usuario inexistente, credenciales incompletas y JSON mal formado.
- Consulta y actualización del perfil.
- Cambio de contraseña, confirmación diferente, clave actual incorrecta y rechazo de la clave anterior.
- Creación, listado, cambio de estado y eliminación del operador con el contrato actual.
- Resúmenes y detalles de reservas, historial, pagos y catálogos.
- Preflight CORS permitido desde Angular y rechazado desde otro origen.
- Baja del cliente de prueba y rechazo del login posterior.

Postman verifica la API; `UsuarioServiceIntegracionTest` verifica además que la contraseña persistida no coincide con el texto recibido y que `PasswordEncoder.matches()` la valida.

## Sustento de clase

- 22:48–23:06: dependencia de Spring Security.
- 24:01–33:49: `SecurityFilterChain`, CSRF y frames de H2, con permisos iniciales abiertos.
- 35:36–42:06: CORS y prioridad del filtro.
- 58:41–1:09:56: bean BCrypt y creación de usuarios con contraseñas codificadas.
- `matches()` adapta el login existente para que siga funcionando con BCrypt antes del relevo de `UserDetailsService` (aproximadamente 1:12).

Referencias: [Spring Security 6.5: contraseñas](https://docs.spring.io/spring-security/reference/6.5/features/authentication/password-storage.html), [Newman](https://learning.postman.com/docs/reference/newman-cli/command-line-integration-with-newman/).
