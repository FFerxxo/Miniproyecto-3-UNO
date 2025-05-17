package com.uno.model;

import com.uno.model.enums.CardColor;

/**
 * Clase que representa al jugador humano en el juego UNO.
 */
public class HumanPlayer extends Player {

    /**
     * Constructor para el jugador humano.
     *
     * @param name Nombre del jugador
     */
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public CardColor selectBestColor() {
        return super.selectBestColor();
    }


}