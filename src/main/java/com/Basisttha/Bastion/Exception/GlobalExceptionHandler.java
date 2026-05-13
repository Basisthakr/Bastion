package com.Basisttha.Bastion.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)//400
    public ResponseEntity<Map<String, String>> HandleRuntime(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("Error: ", e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)//404
    public ResponseEntity<Map<String, String>> handleNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Error: ", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)//500
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body(Map.of("Error: ", "Something went wrong"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error
                -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
