package com.uno.model;

import com.uno.exceptions.EmptyDeckException;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;

import java.util.*;

/**
 * Clase que representa el mazo de cartas en el juego UNO.
 */
public class Deck {
    private Stack<Card> cards;
    private Stack<Card> discardPile;

    /**
     * Constructor que inicializa y baraja el mazo.
     */
    public Deck() {
        cards = new Stack<>();
        discardPile = new Stack<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Inicializa el mazo con todas las cartas según las reglas del UNO.
     */
    private void initializeDeck() {
        // Colores: rojo, verde, azul y amarillo
        CardColor[] colors = {CardColor.RED, CardColor.GREEN, CardColor.BLUE, CardColor.YELLOW};

        // Para cada color
        for (CardColor color : colors) {
            // Una carta 0
            cards.add(new Card(color, 0));

            // Dos cartas de cada número del 1 al 9
            for (int number = 1; number <= 9; number++) {
                cards.add(new Card(color, number));
                cards.add(new Card(color, number));
            }

            // Dos cartas +2
            cards.add(new Card(color, CardType.DRAW_TWO));
            cards.add(new Card(color, CardType.DRAW_TWO));

            // Una carta de ceder turno
            cards.add(new Card(color, CardType.SKIP));
        }

        // Cuatro cartas +4
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(CardColor.WILD, CardType.WILD_DRAW_FOUR));
        }

        // Cuatro cartas de cambio de color
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(CardColor.WILD, CardType.WILD));
        }
    }

    /**
     * Baraja el mazo de cartas.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Obtiene la carta superior del mazo.
     *
     * @return La carta superior
     * @throws EmptyDeckException si el mazo está vacío
     */
    public Card drawCard() throws EmptyDeckException {
        if (cards.isEmpty()) {
            reshuffleDiscardPile();
        }

        if (cards.isEmpty()) {
            throw new EmptyDeckException("No quedan cartas en el mazo y no hay cartas para reciclar");
        }

        return cards.pop();
    }

    /**
     * Coloca una carta en la pila de descarte.
     *
     * @param card La carta a descartar
     */
    public void discard(Card card) {
        discardPile.push(card);
    }

    /**
     * Recicla las cartas de la pila de descarte al mazo cuando este se queda vacío.
     * Mantiene la última carta jugada en la pila de descarte.
     */
    private void reshuffleDiscardPile() {
        if (discardPile.size() <= 1) {
            return;  // No hay suficientes cartas para reciclar
        }

        // Guardamos la carta superior
        Card topCard = discardPile.pop();

        // Transferimos el resto al mazo
        while (!discardPile.isEmpty()) {
            cards.push(discardPile.pop());
        }

        // Volvemos a poner la carta superior en la pila de descarte
        discardPile.push(topCard);

        // Barajamos el mazo
        shuffle();
    }

    /**
     * Reinserta una carta en el mazo, posiblemente en una posición aleatoria.
     *
     * @param card La carta a reinsertar
     */
    public void reinsertCard(Card card) {
        if (cards.isEmpty()) {
            cards.push(card);
        } else {
            // Inserta la carta en una posición aleatoria del mazo
            List<Card> tempCards = new ArrayList<>(cards);
            cards.clear();

            int position = new Random().nextInt(tempCards.size() + 1);

            for (int i = 0; i < tempCards.size(); i++) {
                if (i == position) {
                    cards.push(card);
                }
                cards.push(tempCards.get(i));
            }

            if (position == tempCards.size()) {
                cards.push(card);
            }
        }
    }

    /**
     * Obtiene la carta en la parte superior de la pila de descarte.
     *
     * @return La carta superior de la pila de descarte
     * @throws EmptyDeckException si la pila de descarte está vacía
     */
    public Card getTopCard() throws EmptyDeckException {
        if (discardPile.isEmpty()) {
            throw new EmptyDeckException("No hay cartas en la pila de descarte");
        }
        return discardPile.peek();
    }

    /**
     * Reparte una mano inicial a un jugador.
     *
     * @param numCards Número de cartas a repartir
     * @return Lista de cartas para la mano inicial
     * @throws EmptyDeckException si no hay suficientes cartas en el mazo
     */
    public List<Card> dealInitialHand(int numCards) throws EmptyDeckException {
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < numCards; i++) {
            hand.add(drawCard());
        }
        return hand;
    }

    /**
     * Coloca la primera carta en la pila de descarte para iniciar el juego.
     *
     * @return La carta inicial en la pila de descarte
     * @throws EmptyDeckException si el mazo está vacío
     */
    public Card placeInitialCard() throws EmptyDeckException {
        Card initialCard = drawCard();
        discard(initialCard);
        return initialCard;
    }

    /**
     * Verifica si el mazo está vacío.
     *
     * @return true si el mazo está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Reordena el mazo usando las cartas del descarte.
     * Este método se llama cuando el mazo está vacío y necesitamos más cartas.
     *
     * @throws EmptyDeckException si no hay cartas para reciclar
     */
    public void reshuffleDeck() throws EmptyDeckException {
        if (discardPile.size() <= 1) {
            throw new EmptyDeckException("No hay suficientes cartas para reciclar");
        }

        // Guardamos la carta superior
        Card topCard = discardPile.pop();

        // Transferimos el resto al mazo
        while (!discardPile.isEmpty()) {
            cards.push(discardPile.pop());
        }

        // Volvemos a poner la carta superior en la pila de descarte
        discardPile.push(topCard);

        // Barajamos el mazo
        shuffle();
    }
}