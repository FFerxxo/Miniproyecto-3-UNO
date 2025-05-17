package com.uno.model.enums;

/**
 * Enum que representa los colores posibles de las cartas en el juego UNO.
 */
public enum CardColor {
    RED, BLUE, GREEN, YELLOW, WILD;

    /**
     * Obtiene una representación en string del color para mostrar en la interfaz.
     * @return Cadena representando el color
     */
    @Override
    public String toString() {
        return switch (this) {
            case RED -> "Rojo";
            case BLUE -> "Azul";
            case GREEN -> "Verde";
            case YELLOW -> "Amarillo";
            case WILD -> "Comodín";
        };
    }
}