package com.uno.model;

import com.uno.exceptions.InvalidCardPlayException;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Clase que representa al jugador computadora en el juego UNO.
 */
public class ComputerPlayer extends Player {
    private final Random random;

    /**
     * Constructor para el jugador computadora.
     */
    public ComputerPlayer() {
        super("Computadora");
        this.random = new Random();
    }

    /**
     * Determina la mejor carta para jugar basándose en una estrategia simple.
     * Prioriza cartas especiales y luego cartas del mismo color.
     *
     * @param topCard Carta superior en la mesa
     * @return La carta seleccionada o null si no hay cartas jugables
     * @throws InvalidCardPlayException si la carta no se puede jugar
     */
    public Card selectBestCard(Card topCard) throws InvalidCardPlayException {
        if (!hasPlayableCard(topCard)) {
            return null;
        }

        // Prioridad: +4, +2, Skip, Wild, Número
        for (CardType priority : new CardType[]{CardType.WILD_DRAW_FOUR, CardType.DRAW_TWO,
                CardType.SKIP, CardType.WILD, CardType.NUMBER}) {
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                if (card.getType() == priority && card.canPlayOn(topCard)) {
                    return playCard(i, topCard);
                }
            }
        }

        // Si llegamos aquí, solo quedan cartas numéricas jugables
        // Jugamos la primera que encontremos
        int index = getPlayableCardIndex(topCard);
        return playCard(index, topCard);
    }

    /**
     * Selecciona el mejor color para una carta comodín.
     * Elige el color más frecuente en la mano.
     *
     * @return El color seleccionado
     */
    public CardColor selectBestColor() {
        Map<CardColor, Integer> colorCount = new HashMap<>();

        // Contamos cuántas cartas hay de cada color
        for (Card card : hand) {
            if (card.getColor() != CardColor.WILD) {
                CardColor color = card.getColor();
                colorCount.put(color, colorCount.getOrDefault(color, 0) + 1);
            }
        }

        // Si no hay cartas de color, elegimos uno al azar
        if (colorCount.isEmpty()) {
            CardColor[] colors = {CardColor.RED, CardColor.BLUE, CardColor.GREEN, CardColor.YELLOW};
            return colors[random.nextInt(colors.length)];
        }

        // Encontramos el color más frecuente
        CardColor bestColor = CardColor.RED; // Valor por defecto
        int maxCount = 0;

        for (Map.Entry<CardColor, Integer> entry : colorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestColor = entry.getKey();
            }
        }

        return bestColor;
    }

    /**
     * Simula la declaración de UNO por parte de la computadora.
     * La computadora siempre declara UNO cuando debe hacerlo.
     *
     * @return true siempre, ya que la computadora siempre declara UNO
     */
    public boolean decideToCallUno() {
        return true;
    }
}