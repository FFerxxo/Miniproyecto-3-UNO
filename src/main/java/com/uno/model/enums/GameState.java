package com.uno.model.enums;

/**
 * Enum que representa los posibles estados del juego UNO.
 */
public enum GameState {
    NOT_STARTED,      // El juego no ha comenzado
    PLAYER_TURN,      // Turno del jugador humano
    COMPUTER_TURN,    // Turno de la computadora
    COLOR_SELECTION,  // Seleccionando color (después de jugar un comodín)
    UNO_DECLARATION,  // Momento para declarar UNO
    GAME_OVER         // El juego ha terminado
}