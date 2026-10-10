package com.hotel.macondo.errors;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo de errores para los controladores REST.
 * Responde JSON {"mensaje": "..."} en vez de la pagina "error" de Thymeleaf,
 * para que Angular pueda mostrar el mensaje. Tiene prioridad sobre GlobalExceptionHandler.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    /** Body JSON mal formado o con tipos invalidos: 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen el formato correcto.");
    }

    /** Errores de validacion de formularios: 400. */
    @ExceptionHandler(FormularioErroneoException.class)
    public ResponseEntity<Map<String, String>> handleFormularioErroneo(FormularioErroneoException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Operaciones que no se pueden realizar (ej. eliminar con relaciones): 400. */
    @ExceptionHandler(PeticionImposible.class)
    public ResponseEntity<Map<String, String>> handlePeticionImposible(PeticionImposible ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Recurso que no existe en la base de datos: 404. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Cualquier otro error: 500 con un mensaje generico. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        // Se registra el error real en consola; al front solo le llega el mensaje generico
        log.error("Error inesperado en la API REST", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /** Arma la respuesta; Map.of no acepta null, por eso el mensaje por defecto. */
    private ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
        String texto = mensaje != null ? mensaje : "Ocurrió un error inesperado. Intenta nuevamente más tarde.";
        return ResponseEntity.status(estado).body(Map.of("mensaje", texto));
    }
}
