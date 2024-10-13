package com.example.gestion_curriculums0.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Defino este controlador para manejar las excepciones globalmente en la aplicación
@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejo las excepciones del tipo RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Devuelvo un mensaje con el error y el estado HTTP 400 (BAD_REQUEST)
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Manejo las excepciones del tipo DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // Verifico si la causa de la excepción es una violación de restricciones de Hibernate
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            // Devuelvo un mensaje indicando que el nombre de usuario ya existe y sugiero restablecer la contraseña
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El nombre de usuario ya existe. ¿Olvidaste tu contraseña? " +
                            "<a href='/reset-password'>Restablecer contraseña</a>");
        }
        // Si no es una violación de restricciones, devuelvo un mensaje genérico de error interno del servidor
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error al procesar tu solicitud. Por favor, inténtalo de nuevo más tarde.");
    }

    // Aquí puedo definir otros manejadores de excepciones según sea necesario
}
