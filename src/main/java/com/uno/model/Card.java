package com.uno.model;

import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;

/**
 * Clase que representa una carta del juego UNO.
 */
public class Card {
    private final CardColor color;
    private final CardType type;
    private final int number;  // Solo relevante para cartas numéricas
    private CardColor activeColor;  // Color activo para cartas comodín

    /**
     * Constructor para cartas numéricas.
     *
     * @param color Color de la carta
     * @param number Número de la carta (0-9)
     */
    public Card(CardColor color, int number) {
        this.color = color;
        this.type = CardType.NUMBER;
        this.number = number;
        this.activeColor = color;
    }

    /**
     * Constructor para cartas especiales.
     *
     * @param color Color de la carta
     * @param type Tipo de la carta
     */
    public Card(CardColor color, CardType type) {
        this.color = color;
        this.type = type;
        this.number = -1;  // No aplica para cartas especiales
        this.activeColor = color;
    }

    /**
     * Verifica si esta carta se puede jugar sobre la carta superior.
     *
     * @param topCard Carta superior en la mesa
     * @return true si la carta es jugable, false en caso contrario
     */
    public boolean canPlayOn(Card topCard) {
        // Para depuración
        System.out.println("Verificando si " + this + " puede jugarse sobre " + topCard);
        System.out.println("Color activo de la carta superior: " + topCard.getActiveColor());

        // Las cartas comodín se pueden jugar sobre cualquier carta
        if (type.isWild()) {
            System.out.println("Carta comodín: se puede jugar");
            return true;
        }

        // Coincidencia de color
        if (color == topCard.getActiveColor()) {
            System.out.println("Coincidencia de color: se puede jugar");
            return true;
        }

        // Coincidencia de número
        if (type == CardType.NUMBER && topCard.getType() == CardType.NUMBER) {
            boolean canPlay = number == topCard.getNumber();
            if (canPlay) {
                System.out.println("Coincidencia de número: se puede jugar");
            }
            return canPlay;
        }

        // Coincidencia de tipo (SOLO para cartas especiales, no para números)
        if (type != CardType.NUMBER && type == topCard.getType()) {
            System.out.println("Coincidencia de tipo: se puede jugar");
            return true;
        }

        // Si no cumple ninguna condición
        return false;
    }

    /**
     * Establece el color activo para cartas comodín.
     *
     * @param color Nuevo color activo
     */
    public void setActiveColor(CardColor color) {
        this.activeColor = color;
        System.out.println("Color activo establecido a: " + color);
    }

    // Getters

    public CardColor getColor() {
        return color;
    }

    public CardType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public CardColor getActiveColor() {
        return activeColor;
    }

    /**
     * Obtiene una representación en string de la carta.
     *
     * @return Descripción de la carta
     */
    @Override
    public String toString() {
        if (type == CardType.NUMBER) {
            return color + " " + number;
        } else {
            return color + " " + type;
        }
    }

    /**
     * Obtiene el nombre del archivo de imagen correspondiente a esta carta.
     *
     * @return Nombre del archivo de la imagen
     */
    public String getImageFileName() {
        // Convertir el color a minúsculas para los nombres de archivo
        String colorName = color.name().toLowerCase();

        if (type == CardType.NUMBER) {
            // Formato: 0_blue.png, 1_red.png, etc.
            return number + "_" + colorName + ".png";
        } else if (type == CardType.SKIP) {
            // Formato: skip_blue.png, skip_red.png, etc.
            return "skip_" + colorName + ".png";
        } else if (type == CardType.DRAW_TWO) {
            // Formato corregido: 2_wild_draw_blue.png, 2_wild_draw_red.png, etc.
            return "2_wild_draw_" + colorName + ".png";
        } else if (type == CardType.WILD) {
            // Formato: wild.png
            return "wild.png";
        } else { // WILD_DRAW_FOUR
            // Formato: 4_wild_draw.png
            return "4_wild_draw.png";
        }
    }
}