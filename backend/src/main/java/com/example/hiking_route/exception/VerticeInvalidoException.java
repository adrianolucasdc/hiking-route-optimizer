package com.example.hiking_route.exception;

/**
 * Exceção lançada quando os vértices de origem ou destino são inválidos
 */
public class VerticeInvalidoException extends GrafoException {
    
    public VerticeInvalidoException(String message) {
        super(message);
    }
    
    public VerticeInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
