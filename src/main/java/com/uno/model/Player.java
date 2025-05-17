package com.uno.model;

import com.uno.exceptions.InvalidCardPlayException;
import com.uno.model.enums.CardColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase abstracta que representa un jugador en el juego UNO.
 */
public abstract class Player {
    protected String name;
    protected List<Card> hand;
    protected boolean hasCalledUno;

    /**
     * Constructor para inicializar un jugador.
     *
     * @param name Nombre del jugador
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.hasCalledUno = false;
    }

    /**
     * Agrega una carta a la mano del jugador.
     *
     * @param card Carta a agregar
     */
    public void addCard(Card card) {
        hand.add(card);
        // Si agregamos una carta, ya no estamos en estado "UNO"
        if (hand.size() > 1) {
            hasCalledUno = false;
        }
    }

    /**
     * Selecciona el mejor color para una carta comodín.
     * La implementación base selecciona un color aleatorio.
     *
     * @return El color seleccionado
     */
    public CardColor selectBestColor() {
        // Implementación base: seleccionar un color aleatorio
        CardColor[] colors = {CardColor.RED, CardColor.BLUE, CardColor.GREEN, CardColor.YELLOW};
        return colors[new Random().nextInt(colors.length)];
    }


    /**
     * Añade una lista de cartas a la mano del jugador.
     *
     * @param cards Lista de cartas a añadir
     */
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
        if (hand.size() > 1) {
            hasCalledUno = false;
        }
    }

    /**
     * Juega una carta de la mano.
     *
     * @param index Índice de la carta a jugar
     * @param topCard Carta superior en la mesa
     * @return La carta jugada
     * @throws InvalidCardPlayException si la carta no se puede jugar
     */
    public Card playCard(int index, Card topCard) throws InvalidCardPlayException {
        if (index < 0 || index >= hand.size()) {
            String message = "Índice de carta fuera de rango: " + index;
            System.out.println(message);
            throw new InvalidCardPlayException(message);
        }

        Card cardToPlay = hand.get(index);
        System.out.println("Intentando jugar: " + cardToPlay);
        System.out.println("Sobre la carta: " + topCard);
        System.out.println("Color actual: " + topCard.getActiveColor());

        if (!cardToPlay.canPlayOn(topCard)) {
            String message = "No puedes jugar esta carta";
            System.out.println(message);
            throw new InvalidCardPlayException(message);
        }

        hand.remove(index);
        System.out.println("Carta jugada exitosamente");

        // Si solo queda una carta, reestablecemos hasCalledUno
        if (hand.size() == 1) {
            hasCalledUno = false;
        }

        return cardToPlay;
    }

    /**
     * Comprueba si el jugador tiene cartas jugables.
     *
     * @param topCard Carta superior en la mesa
     * @return true si tiene al menos una carta jugable, false en caso contrario
     */
    public boolean hasPlayableCard(Card topCard) {
        for (Card card : hand) {
            if (card.canPlayOn(topCard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método para que el jugador declare "UNO".
     */
    public void callUno() {
        this.hasCalledUno = true;
    }

    /**
     * Determina si el jugador ha ganado (no tiene cartas).
     *
     * @return true si ha ganado, false en caso contrario
     */
    public boolean hasWon() {
        return hand.isEmpty();
    }

    /**
     * Obtiene el índice de una carta jugable.
     *
     * @param topCard Carta superior en la mesa
     * @return Índice de una carta jugable o -1 si no hay ninguna
     */
    public int getPlayableCardIndex(Card topCard) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).canPlayOn(topCard)) {
                return i;
            }
        }
        return -1;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return new ArrayList<>(hand); // Devolvemos una copia para encapsulación
    }

    public int getHandSize() {
        return hand.size();
    }

    public boolean hasCalledUno() {
        return hasCalledUno;
    }

    public void setHasCalledUno(boolean hasCalledUno) {
        this.hasCalledUno = hasCalledUno;
    }
}