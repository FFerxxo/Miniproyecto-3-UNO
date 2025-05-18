package com.uno.model;

import com.uno.exceptions.EmptyDeckException;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;

import java.util.*;

/** Mazo del juego UNO (56 cartas). */
public class Deck {
    private final Stack<Card> cards;
    private final Stack<Card> discardPile;

    public Deck() {
        cards = new Stack<>();
        discardPile = new Stack<>();
        initializeDeck();
        shuffle();
    }

    /** Crea 56 cartas: 12 por color (0-9, +2, Skip) + 8 comodines. */
    private void initializeDeck() {
        cards.clear();

        for (CardColor color : new CardColor[]{CardColor.RED, CardColor.GREEN, CardColor.BLUE, CardColor.YELLOW}) {
            // 0
            cards.add(new Card(color, 0));
            // 1-9 (una de cada)
            for (int n = 1; n <= 9; n++) cards.add(new Card(color, n));
            // +2
            cards.add(new Card(color, CardType.DRAW_TWO));
            // Skip
            cards.add(new Card(color, CardType.SKIP));
        }
        // 4 comodines y 4 +4
        for (int i = 0; i < 4; i++) cards.add(new Card(CardColor.WILD, CardType.WILD));
        for (int i = 0; i < 4; i++) cards.add(new Card(CardColor.WILD, CardType.WILD_DRAW_FOUR));
    }

    public void shuffle()                   { Collections.shuffle(cards); }
    public Card drawCard() throws EmptyDeckException {
        if (cards.isEmpty()) reshuffleDiscardPile();
        if (cards.isEmpty()) throw new EmptyDeckException("No quedan cartas en el mazo");
        return cards.pop();
    }
    public void discard(Card card)          { discardPile.push(card); }

    private void reshuffleDiscardPile() {
        if (discardPile.size() <= 1) return;
        Card top = discardPile.pop();
        while (!discardPile.isEmpty()) cards.push(discardPile.pop());
        discardPile.push(top);
        shuffle();
    }

    public void reinsertCard(Card card) {
        List<Card> tmp = new ArrayList<>(cards);
        int pos = new Random().nextInt(tmp.size() + 1);
        tmp.add(pos, card);
        cards.clear();
        for (int i = tmp.size() - 1; i >= 0; i--) cards.push(tmp.get(i));
    }

    public Card getTopCard() throws EmptyDeckException {
        if (discardPile.isEmpty()) throw new EmptyDeckException("Pila de descarte vacía");
        return discardPile.peek();
    }

    public List<Card> dealInitialHand(int n) throws EmptyDeckException {
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < n; i++) hand.add(drawCard());
        return hand;
    }

    public Card placeInitialCard() throws EmptyDeckException {
        Card c = drawCard();
        discard(c);
        return c;
    }

    public boolean isEmpty() { return cards.isEmpty(); }
    public void reshuffleDeck() throws EmptyDeckException { reshuffleDiscardPile(); }
}
