package com.example.hiking_route.exception;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        errors.put("status", "ERRO_VALIDACAO");
        errors.put("message", "Dados de entrada inválidos");
        errors.put("errors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MatrizInvalidaException.class)
    public ResponseEntity<GrafoResponse> handleMatrizInvalidaException(MatrizInvalidaException ex) {
        GrafoResponse response = new GrafoResponse(
            new CaminhoResponse(null, 0, "MINIMO", false, ex.getMessage()),
            new CaminhoResponse(null, 0, "MAXIMO", false, ex.getMessage()),
            0,
            "ERRO_MATRIZ_INVALIDA"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(VerticeInvalidoException.class)
    public ResponseEntity<GrafoResponse> handleVerticeInvalidoException(VerticeInvalidoException ex) {
        GrafoResponse response = new GrafoResponse(
            new CaminhoResponse(null, 0, "MINIMO", false, ex.getMessage()),
            new CaminhoResponse(null, 0, "MAXIMO", false, ex.getMessage()),
            0,
            "ERRO_VERTICE_INVALIDO"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GrafoException.class)
    public ResponseEntity<GrafoResponse> handleGrafoException(GrafoException ex) {
        GrafoResponse response = new GrafoResponse(
            new CaminhoResponse(null, 0, "MINIMO", false, ex.getMessage()),
            new CaminhoResponse(null, 0, "MAXIMO", false, ex.getMessage()),
            0,
            "ERRO_GRAFO"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERRO_INTERNO");
        error.put("message", "Erro interno do servidor");
        error.put("details", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERRO_ARGUMENTO");
        error.put("message", "Argumento inválido fornecido");
        error.put("details", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
