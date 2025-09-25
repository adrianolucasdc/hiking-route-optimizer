package com.example.hiking_route.exception;

/**
 * Exceção lançada quando a matriz de adjacência é inválida
 */
public class MatrizInvalidaException extends GrafoException {
    
    public MatrizInvalidaException(String message) {
        super(message);
    }
    
    public MatrizInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
