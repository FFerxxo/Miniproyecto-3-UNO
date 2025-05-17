package com.uno.exceptions;

/**
 * Excepción personalizada para manejar errores relacionados con la declaración de "UNO".
 * Esta es una excepción propia del juego.
 */
public class UnoDeclarationException extends Exception {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error
     */
    public UnoDeclarationException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa raíz.
     *
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public UnoDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }
}