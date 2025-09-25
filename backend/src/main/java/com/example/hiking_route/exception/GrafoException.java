package com.example.hiking_route.exception;

/**
 * Exceção base para operações de grafo
 */
public class GrafoException extends RuntimeException {
    
    public GrafoException(String message) {
        super(message);
    }
    
    public GrafoException(String message, Throwable cause) {
        super(message, cause);
    }
}
