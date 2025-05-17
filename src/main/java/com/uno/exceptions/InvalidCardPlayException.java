package com.uno.exceptions;

/**
 * Excepción lanzada cuando se intenta jugar una carta inválida.
 * Esta es una excepción no marcada (extends RuntimeException).
 */
public class InvalidCardPlayException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error
     */
    public InvalidCardPlayException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa raíz.
     *
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public InvalidCardPlayException(String message, Throwable cause) {
        super(message, cause);
    }
}