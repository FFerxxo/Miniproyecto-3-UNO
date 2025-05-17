package com.uno.model;

import com.uno.exceptions.EmptyDeckException;
import com.uno.exceptions.InvalidCardPlayException;
import com.uno.exceptions.UnoDeclarationException;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;
import com.uno.model.enums.GameState;

/**
 * Clase principal que maneja la lógica del juego UNO.
 */
public class Game {
    private static final int INITIAL_HAND_SIZE = 5;

    private Deck deck;
    private HumanPlayer humanPlayer;
    private ComputerPlayer computerPlayer;
    private Card topCard;
    private boolean isHumanTurn;
    private GameState gameState;
    private Player winner;
    private boolean unoButtonEnabled;
    private boolean waitingForUnoDeclaration;

    /**
     * Constructor que inicializa el juego.
     *
     * @param playerName Nombre del jugador humano
     * @throws EmptyDeckException si hay problemas al inicializar el mazo
     */
    public Game(String playerName) throws EmptyDeckException {
        deck = new Deck();
        humanPlayer = new HumanPlayer(playerName);
        computerPlayer = new ComputerPlayer();
        gameState = GameState.NOT_STARTED;
        winner = null;
        unoButtonEnabled = false;
        waitingForUnoDeclaration = false;
    }


    /**
     * Intenta que el jugador humano juegue una carta.
     *
     * @param cardIndex Índice de la carta a jugar
     * @throws InvalidCardPlayException si la carta no se puede jugar
     * @throws EmptyDeckException si hay problemas con el mazo
     */
    public void humanPlayCard(int cardIndex) throws InvalidCardPlayException, EmptyDeckException {
        if (gameState != GameState.PLAYER_TURN) {
            throw new InvalidCardPlayException("No es el turno del jugador humano");
        }

        Card playedCard = humanPlayer.playCard(cardIndex, topCard);
        deck.discard(playedCard);
        topCard = playedCard;

        // Comprobar si el jugador tiene que declarar UNO
        if (humanPlayer.getHandSize() == 1) {
            waitingForUnoDeclaration = true;
            unoButtonEnabled = true;
        }

        // Comprobar si el jugador ha ganado
        if (humanPlayer.hasWon()) {
            winner = humanPlayer;
            gameState = GameState.GAME_OVER;
            return;
        }

        // Manejar efectos de la carta jugada
        handlePlayedCardEffects(playedCard, true);
    }

    /**
     * Maneja los efectos de las cartas especiales.
     *
     * @param playedCard Carta jugada
     * @param isHumanPlaying true si el jugador humano jugó la carta, false si fue la computadora
     * @throws EmptyDeckException si hay problemas con el mazo al robar cartas
     */
    private void handlePlayedCardEffects(Card playedCard, boolean isHumanPlaying) throws EmptyDeckException {
        CardType type = playedCard.getType();

        switch (type) {
            case SKIP:
                // Quien jugó la carta repite turno
                // No cambiamos isHumanTurn
                if (isHumanPlaying) {
                    isHumanTurn = true;
                    gameState = GameState.PLAYER_TURN;
                } else {
                    isHumanTurn = true;
                    gameState = GameState.COMPUTER_TURN;
                }
                break;

            case DRAW_TWO:
                // El oponente roba 2 cartas y pierde el turno
                if (isHumanPlaying) {
                    computerPlayer.addCard(deck.drawCard());
                    computerPlayer.addCard(deck.drawCard());
                    gameState = GameState.PLAYER_TURN;
                } else {
                    humanPlayer.addCard(deck.drawCard());
                    humanPlayer.addCard(deck.drawCard());
                    gameState = GameState.COMPUTER_TURN;
                }
                break;

            case WILD:
                // El jugador debe elegir un color
                if (isHumanPlaying) {
                    gameState = GameState.COLOR_SELECTION;
                } else {
                    // La computadora elige el color automáticamente
                    CardColor bestColor = computerPlayer.selectBestColor();
                    playedCard.setActiveColor(bestColor);
                    isHumanTurn = true;
                    gameState = GameState.PLAYER_TURN;
                }
                break;

            case WILD_DRAW_FOUR:
                // El oponente roba 4 cartas y el jugador actual elige color
                if (isHumanPlaying) {
                    computerPlayer.addCard(deck.drawCard());
                    computerPlayer.addCard(deck.drawCard());
                    computerPlayer.addCard(deck.drawCard());
                    computerPlayer.addCard(deck.drawCard());
                    gameState = GameState.COLOR_SELECTION;
                } else {
                    humanPlayer.addCard(deck.drawCard());
                    humanPlayer.addCard(deck.drawCard());
                    humanPlayer.addCard(deck.drawCard());
                    humanPlayer.addCard(deck.drawCard());
                    // La computadora elige el color automáticamente
                    CardColor bestColor = computerPlayer.selectBestColor();
                    playedCard.setActiveColor(bestColor);
                    isHumanTurn = true;
                    gameState = GameState.PLAYER_TURN;
                }
                break;

            default:
                // Carta normal, cambio de turno
                isHumanTurn = !isHumanTurn;
                gameState = isHumanTurn ? GameState.PLAYER_TURN : GameState.COMPUTER_TURN;
                break;
        }
    }

    /**
     * Establece el color seleccionado para las cartas comodín.
     *
     * @param color Color seleccionado
     */
    public void setSelectedColor(CardColor color) {
        if (gameState == GameState.COLOR_SELECTION) {
            topCard.setActiveColor(color);
            isHumanTurn = !isHumanTurn;
            gameState = isHumanTurn ? GameState.PLAYER_TURN : GameState.COMPUTER_TURN;
        }
    }

    /**
     * Realiza el turno de la computadora.
     *
     * @throws EmptyDeckException si hay problemas con el mazo
     */
    public void computerTurn() throws EmptyDeckException {
        if (gameState != GameState.COMPUTER_TURN) {
            return;
        }

        try {
            // La computadora intenta jugar una carta
            Card playedCard = computerPlayer.selectBestCard(topCard);

            if (playedCard != null) {
                // La computadora jugó una carta
                deck.discard(playedCard);
                topCard = playedCard;

                // Comprobar si la computadora tiene que declarar UNO
                if (computerPlayer.getHandSize() == 1) {
                    if (computerPlayer.decideToCallUno()) {
                        computerPlayer.callUno();
                    }
                }

                // Comprobar si la computadora ha ganado
                if (computerPlayer.hasWon()) {
                    winner = computerPlayer;
                    gameState = GameState.GAME_OVER;
                    return;
                }

                // Manejar efectos de la carta jugada
                handlePlayedCardEffects(playedCard, false);
            } else {
                // La computadora no tiene cartas jugables, roba una
                computerPlayer.addCard(deck.drawCard());
                isHumanTurn = true;
                gameState = GameState.PLAYER_TURN;
            }
        } catch (InvalidCardPlayException e) {
            // Esto no debería suceder si la lógica de selección de cartas es correcta
            System.err.println("Error en el turno de la computadora: " + e.getMessage());
            isHumanTurn = true;
            gameState = GameState.PLAYER_TURN;
        }
    }

    /**
     * Permite al jugador humano robar una carta cuando no puede jugar.
     *
     * @throws EmptyDeckException si hay problemas con el mazo
     */
    public void humanDrawCard() throws EmptyDeckException {
        if (gameState != GameState.PLAYER_TURN) {
            return;
        }

        humanPlayer.addCard(deck.drawCard());
        isHumanTurn = false;
        gameState = GameState.COMPUTER_TURN;
    }

    /**
     * Maneja la declaración de UNO por parte del jugador humano.
     *
     * @throws UnoDeclarationException si la declaración de UNO no es válida
     * @throws EmptyDeckException si hay problemas con el mazo al penalizar
     */
    public void humanCallUno() throws UnoDeclarationException, EmptyDeckException {
        if (!unoButtonEnabled) {
            throw new UnoDeclarationException("No es momento de declarar UNO");
        }

        humanPlayer.callUno();
        waitingForUnoDeclaration = false;
        unoButtonEnabled = false;
    }

    /**
     * Comprueba si el jugador olvidó declarar UNO y aplica penalización.
     *
     * @throws EmptyDeckException si hay problemas con el mazo al penalizar
     */
    public void checkUnoPenalty() throws EmptyDeckException {
        if (waitingForUnoDeclaration && !humanPlayer.hasCalledUno()) {
            // El jugador olvidó declarar UNO, aplica penalización
            humanPlayer.addCard(deck.drawCard());
            waitingForUnoDeclaration = false;
        }
        unoButtonEnabled = false;
    }

    /**
     * Ejecuta la acción de la computadora de declarar "UNO" cuando el humano se olvidó.
     *
     * @return true si la computadora atrapó al humano sin declarar UNO
     * @throws EmptyDeckException si hay problemas con el mazo al penalizar
     */
    public boolean computerCallCatchUno() throws EmptyDeckException {
        if (waitingForUnoDeclaration && !humanPlayer.hasCalledUno()) {
            // La computadora atrapa al humano
            humanPlayer.addCard(deck.drawCard());
            waitingForUnoDeclaration = false;
            unoButtonEnabled = false;
            return true;
        }
        return false;
    }

    // Getters

    public HumanPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public ComputerPlayer getComputerPlayer() {
        return computerPlayer;
    }

    public Card getTopCard() {
        return topCard;
    }

    public boolean isHumanTurn() {
        return isHumanTurn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isUnoButtonEnabled() {
        return unoButtonEnabled;
    }

    public void setUnoButtonEnabled(boolean enabled) {
        this.unoButtonEnabled = enabled;
    }
}