package com.parking.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("RuntimeException returns 400 with JSON format")
    void runtimeExceptionReturns400() {
        ResponseEntity<ErrorResponse> response = handler.handleRuntime(new RuntimeException("Prueba error"));

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Prueba error", Objects.requireNonNull(response.getBody()).getError());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("InvalidCredentialsException returns 401 with JSON format")
    void invalidCredentialsReturns401() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(
                new InvalidCredentialsException("Credenciales inválidas"));

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Credenciales inválidas", Objects.requireNonNull(response.getBody()).getError());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException returns 400 with field errors")
    void validationReturns400WithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "campo1", "mensaje de error"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error de validación", Objects.requireNonNull(response.getBody()).getError());
        assertNotNull(response.getBody().getErrors());
        assertEquals(1, response.getBody().getErrors().size());
        assertEquals("campo1", response.getBody().getErrors().get(0).getField());
        assertEquals("mensaje de error", response.getBody().getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Generic Exception returns 500")
    void genericExceptionReturns500() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new Exception("Algo salió mal"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error interno del servidor", Objects.requireNonNull(response.getBody()).getError());
        assertEquals(500, response.getBody().getStatus());
    }
}
