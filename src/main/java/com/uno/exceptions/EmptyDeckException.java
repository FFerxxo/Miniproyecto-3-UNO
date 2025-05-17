package com.uno.exceptions;

/**
 * Excepción lanzada cuando se intenta robar una carta de un mazo vacío.
 * Esta es una excepción marcada (checked exception) ya que extiende de Exception.
 */
public class EmptyDeckException extends Exception {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje descriptivo del error
     */
    public EmptyDeckException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa raíz.
     *
     * @param message Mensaje descriptivo del error
     * @param cause Causa raíz de la excepción
     */
    public EmptyDeckException(String message, Throwable cause) {
        super(message, cause);
    }
}
