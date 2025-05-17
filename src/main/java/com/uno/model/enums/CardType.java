package com.uno.model.enums;

/**
 * Enum que representa los tipos de cartas en el juego UNO.
 */
public enum CardType {
    NUMBER,      // Cartas numéricas (0-9)
    SKIP,        // Carta de Ceder Turno
    DRAW_TWO,    // Carta +2
    WILD,        // Carta de Cambio de Color
    WILD_DRAW_FOUR; // Carta +4

    /**
     * Verifica si la carta es un comodín.
     * @return true si es un comodín, false en caso contrario
     */
    public boolean isWild() {
        return this == WILD || this == WILD_DRAW_FOUR;
    }

    /**
     * Obtiene una representación en string del tipo de carta para mostrar en la interfaz.
     * @return Cadena representando el tipo de carta
     */
    @Override
    public String toString() {
        return switch (this) {
            case NUMBER -> "Número";
            case SKIP -> "Ceder Turno";
            case DRAW_TWO -> "+2";
            case WILD -> "Cambio de Color";
            case WILD_DRAW_FOUR -> "+4";
        };
    }
}