package com.hotel.macondo.errors;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}