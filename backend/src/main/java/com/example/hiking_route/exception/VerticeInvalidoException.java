package com.example.hiking_route.exception;

public class VerticeInvalidoException extends GrafoException {
    
    public VerticeInvalidoException(String message) {
        super(message);
    }
    
    public VerticeInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
