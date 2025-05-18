package com.uno.test;

import com.uno.model.Deck;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void deckStartsWith56Cards() throws Exception {   // ← añade throws
        Deck d = new Deck();
        assertEquals(56, d.dealInitialHand(56).size());
    }

    @Test
    void dealInitialHandReducesDeck() throws Exception {
        Deck d = new Deck();
        d.dealInitialHand(5);          // quedan 51 cartas

        for (int i = 0; i < 51; i++) d.drawCard();
        assertThrows(Exception.class, d::drawCard);   // la siguiente debería fallar
    }
}
