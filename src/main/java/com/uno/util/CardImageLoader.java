package com.uno.util;

import com.uno.model.Card;
import javafx.scene.image.Image;

/**
 * Clase para cargar las imágenes de las cartas del juego UNO.
 */
public class CardImageLoader {

    /**
     * Obtiene la imagen de una carta.
     *
     * @param card La carta para la que se quiere obtener la imagen
     * @return La imagen de la carta
     */
    public Image getCardImage(Card card) {
        if (card == null) {
            return getCardBackImage();
        }

        String imagePath = "/images/cards/" + card.getImageFileName();

        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + imagePath);
            e.printStackTrace();
            return getCardBackImage();
        }
    }

    /**
     * Obtiene la imagen del reverso de la carta.
     *
     * @return Imagen del reverso de la carta
     */
    public Image getCardBackImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/cards/card_uno.png"));
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen del reverso de la carta");
            e.printStackTrace();
            // Usar una imagen existente como fallback
            try {
                return new Image(getClass().getResourceAsStream("/images/cards/deck_of_cards.png"));
            } catch (Exception ex) {
                // Si aún falla, intentar con otra imagen
                System.out.println("Error al cargar imagen de fallback");
                return null; // Retornar null como último recurso
            }
        }
    }
}