package com.uno.util;

import com.uno.exceptions.EmptyDeckException;
import com.uno.model.Card;
import com.uno.model.Deck;
import com.uno.model.Player;

import com.uno.model.enums.CardType;
import com.uno.model.enums.GameState;

/**
 * Clase que contiene métodos útiles para la lógica del juego UNO.
 */
public class GameLogicHelper {

    /**
     * Verifica si un jugador puede jugar alguna carta.
     *
     * @param player El jugador a verificar
     * @param topCard La carta superior en la mesa
     * @return true si puede jugar alguna carta, false si no
     */
    public static boolean canPlayAnyCard(Player player, Card topCard) {
        return player.hasPlayableCard(topCard);
    }

    /**
     * Aplica el efecto de la carta jugada.
     *
     * @param playedCard La carta jugada
     * @param currentPlayer El jugador actual
     * @param opponent El jugador oponente
     * @param deck El mazo de cartas
     * @return El nuevo estado del juego
     * @throws EmptyDeckException Si el mazo está vacío
     */
    public static GameState applyCardEffect(Card playedCard, Player currentPlayer,
                                            Player opponent, Deck deck)
            throws EmptyDeckException {

        CardType type = playedCard.getType();

        // Aplicamos diferentes efectos según el tipo de carta
        if (type == CardType.SKIP) {
            System.out.println("El jugador pierde su turno");
            return GameState.PLAYER_TURN; // El jugador actual juega de nuevo

        } else if (type == CardType.DRAW_TWO) {
            System.out.println("El oponente toma 2 cartas");
            // El oponente toma 2 cartas
            for (int i = 0; i < 2; i++) {
                Card drawnCard = deck.drawCard();
                opponent.addCard(drawnCard);
            }
            return GameState.PLAYER_TURN; // El jugador actual juega de nuevo

        } else if (type == CardType.WILD_DRAW_FOUR) {
            System.out.println("El oponente toma 4 cartas");
            // El oponente toma 4 cartas
            for (int i = 0; i < 4; i++) {
                Card drawnCard = deck.drawCard();
                opponent.addCard(drawnCard);
            }
            return GameState.COLOR_SELECTION;

        } else if (type == CardType.WILD) {
            System.out.println("Se debe seleccionar un color");
            return GameState.COLOR_SELECTION;

        } else {
            // Cartas normales numéricas
            if (currentPlayer.getHand().size() == 0) {
                return GameState.GAME_OVER;
            } else {
                return (GameState.COMPUTER_TURN); // Alternamos el turno
            }
        }
    }

    /**
     * Verifica si un jugador está en situación de UNO (tiene solo una carta).
     *
     * @param player El jugador a verificar
     * @return true si el jugador tiene solo una carta
     */
    public static boolean isUnoSituation(Player player) {
        return player.getHand().size() == 1 && !player.hasCalledUno();
    }

    /**
     * Verifica si un jugador ha ganado (no tiene cartas).
     *
     * @param player El jugador a verificar
     * @return true si el jugador no tiene cartas
     */
    public static boolean isWinner(Player player) {
        return player.getHand().isEmpty();
    }

    /**
     * Maneja la declaración de UNO de un jugador.
     *
     * @param player El jugador que declara UNO
     * @param deck El mazo de cartas
     * @return true si la declaración fue correcta, false si no
     * @throws EmptyDeckException Si el mazo está vacío
     */
    public static boolean handleUnoDeclaration(Player player, Deck deck)
            throws EmptyDeckException {
        if (player.getHand().size() == 1) {
            player.callUno();
            System.out.println("¡UNO!");
            return true;
        } else {
            System.out.println("Declaración de UNO incorrecta");
            // Penalización: tomar una carta
            player.addCard(deck.drawCard());
            return false;
        }
    }

    /**
     * Penaliza a un jugador por no declarar UNO.
     *
     * @param player El jugador a penalizar
     * @param deck El mazo de cartas
     * @throws EmptyDeckException Si el mazo está vacío
     */
    public static void penalizeForNotCallingUno(Player player, Deck deck)
            throws EmptyDeckException {
        if (player.getHand().size() == 1 && !player.hasCalledUno()) {
            System.out.println("No se declaró UNO. Penalización: tomar una carta");
            player.addCard(deck.drawCard());
        }
    }
}
