package com.uno.test;

import com.uno.model.Card;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void sameColorIsPlayable() {
        Card redFive   = new Card(CardColor.RED, 5);
        Card redSkip   = new Card(CardColor.RED, CardType.SKIP);
        assertTrue(redSkip.canPlayOn(redFive));
    }

    @Test
    void differentColorDifferentNumberNotPlayable() {
        Card redFive   = new Card(CardColor.RED, 5);
        Card blueSeven = new Card(CardColor.BLUE, 7);
        assertFalse(blueSeven.canPlayOn(redFive));
    }
}
