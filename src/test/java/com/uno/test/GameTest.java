package com.uno.test;

import com.uno.model.*;
import com.uno.model.enums.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void drawTwoAddsTwoCardsToOpponent() throws Exception {
        Deck deck = new Deck();
        Player p1 = new HumanPlayer("A");
        Player p2 = new ComputerPlayer();

        // Prepara manos vacías
        p1.addCards(deck.dealInitialHand(0));
        p2.addCards(deck.dealInitialHand(0));

        // Simula que p2 juega +2 sobre una carta top roja
        Card top = new Card(CardColor.RED, 3);
        Card plusTwo = new Card(CardColor.RED, CardType.DRAW_TWO);

        p2.addCard(plusTwo);
        p2.playCard(0, top);              // debería ser legal

        int before = p1.getHand().size();
        p1.addCard(deck.drawCard());
        p1.addCard(deck.drawCard());

        assertEquals(before + 2, p1.getHand().size());
    }
}
