package com.hotel.macondo.errors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String handleRecursoNoEncontradoException(
            RecursoNoEncontradoException ex,
            Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(PeticionImposible.class)
    public String handlePeticionImposible(
            PeticionImposible ex,
            Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(FormularioErroneoException.class)
    public String handleFormularioErroneo(
            FormularioErroneoException ex,
            Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute(
                "mensaje",
                "Ocurrió un error inesperado. Intenta nuevamente más tarde.");
        return "error";
    }
}