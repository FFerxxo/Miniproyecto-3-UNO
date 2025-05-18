package com.uno.controller;

import com.uno.exceptions.EmptyDeckException;
import com.uno.exceptions.InvalidCardPlayException;
import com.uno.model.Card;
import com.uno.adapter.CardImageAdapter;
import com.uno.model.Deck;
import com.uno.model.Player;
import com.uno.model.HumanPlayer;
import com.uno.model.ComputerPlayer;
import com.uno.model.enums.CardColor;
import com.uno.model.enums.CardType;
import com.uno.model.enums.GameState;
import com.uno.util.CardImageLoader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador MVC que gestiona la lógica de juego y enlaza el modelo con la GUI.
 */
public class GameController {

    // Elementos de la interfaz vinculados con FXML
    @FXML
    private Label statusLabel;

    @FXML
    private Button unoButton;

    @FXML
    private HBox computerHandArea;

    @FXML
    private HBox playerHandArea;

    @FXML
    private ImageView deckImageView;

    @FXML
    private ImageView topCardImageView;

    @FXML
    private Rectangle currentColorIndicator;

    @FXML
    private Label messageLabel;

    // Variables del modelo
    private Deck deck;
    private Player humanPlayer;
    private Player computerPlayer;
    private Card topCard;
    private CardColor currentColor;
    private GameState gameState;
    private Stage primaryStage;

    // Utilidades
    private CardImageLoader imageLoader;
    private List<ImageView> playerCardViews;
    private List<ImageView> computerCardViews;

    /**
     * Inicializa el controlador.
     */
    public void initialize() {
        imageLoader = new CardImageLoader();
        playerCardViews = new ArrayList<>();
        computerCardViews = new ArrayList<>();

        // Configurar evento de clic en el botón UNO
        unoButton.setDisable(true);
        unoButton.setOnAction(new UnoButtonHandler());
        deckImageView.setImage(new Image(getClass().getResourceAsStream("/images/cards/deck_of_cards.png")));

    }

    private void setSelectedColor(CardColor color) {
        if (gameState == GameState.COLOR_SELECTION) {
            currentColor = color;
            topCard.setActiveColor(color); // Actualiza también el color activo de la carta
            updateColorIndicator();
            System.out.println("Color establecido a: " + color);
        }
    }

    /**
     * Configura la ventana principal.
     *
     * @param primaryStage La ventana principal
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Inicia un nuevo juego.
     */
    public void initGame() {
        // Crear modelo del juego
        deck = new Deck();
        humanPlayer = new HumanPlayer("Jugador");
        computerPlayer = new ComputerPlayer();

        try {
            // Repartir cartas iniciales
            List<Card> humanCards = deck.dealInitialHand(5);
            List<Card> computerCards = deck.dealInitialHand(5);

            humanPlayer.addCards(humanCards);
            computerPlayer.addCards(computerCards);

            // Colocar carta inicial - asegurándonos que no sea un comodín
            do {
                topCard = deck.drawCard();

                // Si es un comodín, la devolvemos al fondo del mazo
                if (topCard.getType() == CardType.WILD || topCard.getType() == CardType.WILD_DRAW_FOUR) {
                    // Volvemos a insertar la carta en algún lugar aleatorio del mazo
                    deck.reinsertCard(topCard);
                    continue;
                }

                // Si llegamos aquí, tenemos una carta válida (no comodín)
                break;
            } while (true);

            // Descartamos la carta inicial
            deck.discard(topCard);

            // Establecemos el color actual
            currentColor = topCard.getColor();
            topCard.setActiveColor(currentColor);
            syncColorState();

            // Actualizar interfaz
            updateGameView();

            // Determinar el primer turno
            gameState = GameState.PLAYER_TURN;
            updateStatusMessage();

        } catch (EmptyDeckException e) {
            System.out.println("Error al iniciar el juego: " + e.getMessage());
        }
    }

    /**
     * Actualiza la vista del juego con el estado actual.
     */
    private void updateGameView() {
        // Actualizar la carta superior
        topCardImageView.setImage(imageLoader.getCardImage(topCard));

        // Actualizar indicador de color
        updateColorIndicator();

        // Actualizar mano del jugador
        updatePlayerHand();

        // Actualizar mano del computador
        updateComputerHand();

        // Habilitar/deshabilitar botón UNO
        unoButton.setDisable(humanPlayer.getHand().size() != 1);
    }

    /**
     * Actualiza el indicador de color actual.
     */
    private void updateColorIndicator() {
        Color fillColor;
        System.out.println("DEBUG - Actualizando indicador de color a: " + currentColor);

        switch (currentColor) {
            case RED:
                fillColor = Color.RED;
                break;
            case BLUE:
                fillColor = Color.BLUE;
                break;
            case GREEN:
                fillColor = Color.GREEN;
                break;
            case YELLOW:
                fillColor = Color.YELLOW;
                break;
            default:
                fillColor = Color.WHITE;
                break;
        }

        currentColorIndicator.setFill(fillColor);
    }

    /**
     * Verifica si una carta es jugable en el estado actual del juego.
     *
     * @param card La carta a verificar
     * @return true si la carta es jugable, false en caso contrario
     */
    private boolean isCardPlayable(Card card) {
        boolean playable = card.canPlayOn(topCard);
        System.out.println("DEBUG - Verificando si " + card + " es jugable. Resultado: " + playable);
        System.out.println("DEBUG - Color actual: " + currentColor + ", Color activo de topCard: " + topCard.getActiveColor());
        return playable;
    }

    /**
     * Actualiza la visualización de la mano del jugador humano.
     */
    private void updatePlayerHand() {
        playerHandArea.getChildren().clear();
        playerCardViews.clear();

        List<Card> playerCards = humanPlayer.getHand();

        for (int i = 0; i < playerCards.size(); i++) {
            Card card = playerCards.get(i);
            ImageView cardView = CardImageAdapter.adapt(card);

            cardView.setFitWidth(80);
            cardView.setFitHeight(120);
            cardView.getStyleClass().add("card-view");

            // Guardar índice para usarlo en el evento de clic
            final int cardIndex = i;
            cardView.setOnMouseClicked(event -> handlePlayerCardClick(cardIndex));

            playerCardViews.add(cardView);
            playerHandArea.getChildren().add(cardView);
        }
    }

    /**
     * Actualiza la visualización de la mano del computador.
     */
    private void updateComputerHand() {
        computerHandArea.getChildren().clear();
        computerCardViews.clear();

        int numCards = computerPlayer.getHand().size();

        for (int i = 0; i < numCards; i++) {
            ImageView cardView = new ImageView(imageLoader.getCardBackImage());

            cardView.setFitWidth(80);
            cardView.setFitHeight(120);

            computerCardViews.add(cardView);
            computerHandArea.getChildren().add(cardView);
        }
    }

    /**
     * Actualiza el mensaje de estado del juego.
     */
    private void updateStatusMessage() {
        switch (gameState) {
            case PLAYER_TURN:
                statusLabel.setText("Tu turno");
                break;
            case COMPUTER_TURN:
                statusLabel.setText("Turno de la computadora");
                break;
            case COLOR_SELECTION:
                statusLabel.setText("Selecciona un color");
                break;
            case GAME_OVER:
                if (humanPlayer.getHand().isEmpty()) {
                    statusLabel.setText("¡Has ganado!");
                } else {
                    statusLabel.setText("Has perdido");
                }
                break;
            default:
                statusLabel.setText("");
                break;
        }
    }

    /**
     * Maneja el clic en una carta del jugador humano.
     *
     * @param cardIndex Índice de la carta en la mano
     */
    private void handlePlayerCardClick(int cardIndex) {
        if (gameState != GameState.PLAYER_TURN) {
            messageLabel.setText("No es tu turno");
            return;
        }

        try {
            Card selectedCard = humanPlayer.getHand().get(cardIndex);

            // Verificar si la carta se puede jugar
            if (!selectedCard.canPlayOn(topCard)) {
                messageLabel.setText("No puedes jugar esta carta");
                return;
            }

            // Jugar la carta
            humanPlayer.playCard(cardIndex, topCard);

            // Actualizar carta superior
            topCard = selectedCard;

            // Actualizar color actual si no es un comodín
            if (selectedCard.getColor() != CardColor.WILD) {
                currentColor = selectedCard.getColor();
                topCard.setActiveColor(currentColor);
            }
            syncColorState();

            // Verificar si el jugador ha ganado
            if (humanPlayer.getHand().isEmpty()) {
                gameState = GameState.GAME_OVER;
                messageLabel.setText("¡Has ganado!");
                updateGameView();
                updateStatusMessage();
                return; // Importante: salir del método si el juego ha terminado
            }

            // Verificar si el jugador tiene solo una carta (situación UNO)
            if (humanPlayer.getHand().size() == 1) {
                // Habilitar botón UNO
                unoButton.setDisable(false);
                messageLabel.setText("¡Tienes una carta! Presiona UNO o serás penalizado");

                // Crear un hilo que actúe como temporizador UNO
                Thread unoTimerThread = new Thread(() -> {
                    try {
                        // Esperar un tiempo aleatorio entre 2 y 4 segundos
                        Thread.sleep(2000 + new Random().nextInt(2000));

                        // Verificar si el jugador no declaró UNO
                        javafx.application.Platform.runLater(() -> {
                            if (humanPlayer.getHand().size() == 1 && !humanPlayer.hasCalledUno()) {
                                try {
                                    // Penalizar al jugador
                                    humanPlayer.addCard(deck.drawCard());
                                    messageLabel.setText("¡No declaraste UNO! Has tomado una carta de penalización");
                                    updateGameView();
                                } catch (EmptyDeckException e) {
                                    messageLabel.setText("El mazo está vacío");
                                }
                            }
                            unoButton.setDisable(true);
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                // Iniciar el hilo
                unoTimerThread.setDaemon(true);
                unoTimerThread.start();
            }

            // Aplicar efecto según el tipo de carta
            boolean computerTurn = true; // Por defecto, pasamos al turno de la computadora

            switch (selectedCard.getType()) {
                case SKIP:
                    // Si es carta de bloqueo, el humano juega de nuevo
                    messageLabel.setText("La computadora pierde su turno");
                    computerTurn = false;
                    gameState = GameState.PLAYER_TURN;
                    break;

                case DRAW_TWO:
                    // La computadora roba 2 cartas y el humano juega de nuevo
                    try {
                        computerPlayer.addCard(deck.drawCard());
                        computerPlayer.addCard(deck.drawCard());
                        messageLabel.setText("La computadora toma 2 cartas y pierde su turno");
                        computerTurn = false;
                        gameState = GameState.PLAYER_TURN;
                    } catch (EmptyDeckException e) {
                        messageLabel.setText("El mazo está vacío");
                    }
                    break;

                case WILD:
                    // Mostrar diálogo de selección de color
                    gameState = GameState.COLOR_SELECTION;
                    CardColor wildColor = showColorSelectionDialog();

                    if (wildColor != null) {
                        currentColor = wildColor;
                        topCard.setActiveColor(wildColor);
                        messageLabel.setText("Has cambiado el color a " + wildColor);
                    } else {
                        // Por defecto si el usuario cierra el diálogo
                        currentColor = CardColor.RED;
                        topCard.setActiveColor(CardColor.RED);
                        messageLabel.setText("Color cambiado a Rojo (por defecto)");
                    }

                    // Después de un comodín normal, es turno de la computadora
                    gameState = GameState.COMPUTER_TURN;
                    break;

                case WILD_DRAW_FOUR:
                    // Mostrar diálogo de selección de color y la computadora roba 4 cartas
                    gameState = GameState.COLOR_SELECTION;
                    CardColor draw4Color = showColorSelectionDialog();

                    if (draw4Color != null) {
                        currentColor = draw4Color;
                        topCard.setActiveColor(draw4Color);
                        messageLabel.setText("Has cambiado el color a " + draw4Color);
                    } else {
                        // Por defecto si el usuario cierra el diálogo
                        currentColor = CardColor.RED;
                        topCard.setActiveColor(CardColor.RED);
                        messageLabel.setText("Color cambiado a Rojo (por defecto)");
                    }

                    // La computadora roba 4 cartas y el humano juega de nuevo
                    try {
                        computerPlayer.addCard(deck.drawCard());
                        computerPlayer.addCard(deck.drawCard());
                        computerPlayer.addCard(deck.drawCard());
                        computerPlayer.addCard(deck.drawCard());
                        messageLabel.setText(messageLabel.getText() + " y la computadora toma 4 cartas y pierde su turno");
                        computerTurn = false;
                        gameState = GameState.PLAYER_TURN;
                    } catch (EmptyDeckException e) {
                        messageLabel.setText("El mazo está vacío");
                    }
                    break;

                default:
                    // Carta normal, pasa el turno a la computadora
                    gameState = GameState.COMPUTER_TURN;
                    break;
            }

            // Actualizar interfaz
            updateGameView();
            updateStatusMessage();
            updateColorIndicator();

            // Solo iniciar el turno de la computadora si corresponde
            if (computerTurn && gameState == GameState.COMPUTER_TURN) {
                // Dar tiempo para mostrar los cambios antes del turno del computador
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> computerTurn());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (InvalidCardPlayException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    /**
     * Ejecuta el turno del computador.
     */
    private void computerTurn() {
        if (gameState != GameState.COMPUTER_TURN) {
            return;
        }

        // retraso inicial para simular que la computadora está pensando
        new Thread(() -> {
            try {
                // La computadora "piensa" entre 2 y 3 segundos antes de jugar
                Thread.sleep(2000 + new Random().nextInt(1000));

                // lógica del turno en el hilo de JavaFX
                javafx.application.Platform.runLater(() -> {
                    try {
                        procesarTurnoComputadora();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void procesarTurnoComputadora() {
        try {
            boolean playedCard = false;

            // Buscar una carta que pueda jugar
            for (int i = 0; i < computerPlayer.getHand().size(); i++) {
                Card card = computerPlayer.getHand().get(i);

                // Verificar si la carta se puede jugar
                if (card.canPlayOn(topCard)) {
                    try {
                        // Intentar jugar la carta
                        computerPlayer.playCard(i, topCard);

                        // Si llegamos aquí, la carta se jugó correctamente
                        System.out.println("Computadora jugó: " + card);

                        // Actualizar carta superior
                        topCard = card;

                        // Si no es comodín, actualizar el color
                        if (card.getColor() != CardColor.WILD) {
                            currentColor = card.getColor();
                            topCard.setActiveColor(currentColor);
                        }

                        // Añade esta línea para sincronizar el estado de color
                        syncColorState();

                        // Verificar si la computadora tiene solo una carta (situación UNO)
                        if (computerPlayer.getHand().size() == 1) {
                            // Determinar aleatoriamente si la computadora declara UNO
                            boolean computerCallsUno = new Random().nextInt(100) < 70; // 70% de probabilidad

                            if (computerCallsUno) {
                                // La computadora declara UNO
                                computerPlayer.setHasCalledUno(true);
                                messageLabel.setText("¡La computadora declara UNO!");
                            } else {
                                // La computadora no declara UNO - oportunidad para el jugador
                                messageLabel.setText("La computadora tiene solo una carta...");

                                // Habilitar botón "¡Atrapar UNO!" para el jugador
                                unoButton.setText("¡Atrapar UNO!");
                                unoButton.setDisable(false);

                                // Crear un hilo que actúe como temporizador para atrapar UNO
                                Thread unoTimerThread = new Thread(() -> {
                                    try {
                                        // Esperar un tiempo aleatorio entre 2 y 4 segundos
                                        Thread.sleep(2000 + new Random().nextInt(2000));

                                        javafx.application.Platform.runLater(() -> {
                                            if (computerPlayer.getHand().size() == 1 && !computerPlayer.hasCalledUno()) {
                                                // El jugador no atrapó a la computadora a tiempo
                                                messageLabel.setText("¡La computadora se olvidó de declarar UNO pero no la atrapaste!");
                                            }
                                            unoButton.setText("¡UNO!");
                                            unoButton.setDisable(true);
                                        });
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                });

                                // Iniciar el hilo
                                new Thread(unoTimerThread).start();
                            }
                        }

                        // Verificar si el computador ha ganado
                        if (computerPlayer.getHand().isEmpty()) {
                            gameState = GameState.GAME_OVER;
                            messageLabel.setText("Has perdido");
                            updateGameView();
                            updateStatusMessage();
                            return;
                        }

                        // Manejar los efectos según el tipo de carta
                        boolean humanTurn = true; // Por defecto, pasamos al turno del humano

                        switch (card.getType()) {
                            case SKIP:
                                // Si es carta de bloqueo, la computadora juega de nuevo
                                messageLabel.setText("Pierdes tu turno");
                                humanTurn = false;
                                gameState = GameState.COMPUTER_TURN;

                                // Dar más tiempo antes del siguiente turno
                                new Thread(() -> {
                                    try {
                                        // Entre 2.5 y 3.5 segundos
                                        Thread.sleep(2500 + new Random().nextInt(1000));
                                        javafx.application.Platform.runLater(() -> computerTurn());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                                break;

                            case DRAW_TWO:
                                // El humano roba 2 cartas y la computadora juega de nuevo
                                try {
                                    humanPlayer.addCard(deck.drawCard());
                                    humanPlayer.addCard(deck.drawCard());
                                    messageLabel.setText("Tomas 2 cartas y pierdes tu turno");
                                    updateGameView(); // Actualizar para mostrar las nuevas cartas

                                    humanTurn = false;
                                    gameState = GameState.COMPUTER_TURN;

                                    // Dar más tiempo antes del siguiente turno
                                    new Thread(() -> {
                                        try {
                                            // Entre 2.5 y 3.5 segundos
                                            Thread.sleep(2500 + new Random().nextInt(1000));
                                            javafx.application.Platform.runLater(() -> computerTurn());
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
                                } catch (EmptyDeckException e) {
                                    messageLabel.setText("El mazo está vacío");
                                }
                                break;

                            case WILD:
                                // Para cartas comodín, elegir un color inteligentemente
                                currentColor = computerPlayer.selectBestColor();
                                card.setActiveColor(currentColor);
                                messageLabel.setText("La computadora eligió el color " + currentColor.toString().toLowerCase());
                                System.out.println("Computadora eligió color: " + currentColor);

                                // Después de un comodín normal, es turno del humano
                                gameState = GameState.PLAYER_TURN;
                                break;

                            case WILD_DRAW_FOUR:
                                // Para cartas +4, el humano roba 4 cartas y la computadora elige color
                                currentColor = computerPlayer.selectBestColor();
                                card.setActiveColor(currentColor);

                                try {
                                    // Mostrar mensaje antes de añadir cartas
                                    messageLabel.setText("La computadora eligió el color " + currentColor.toString().toLowerCase() + " y te ha dado un +4");

                                    // Dar tiempo para que el jugador vea el mensaje antes de recibir las cartas
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(1500);
                                            javafx.application.Platform.runLater(() -> {
                                                try {
                                                    // Ahora añadimos las cartas
                                                    humanPlayer.addCard(deck.drawCard());
                                                    humanPlayer.addCard(deck.drawCard());
                                                    humanPlayer.addCard(deck.drawCard());
                                                    humanPlayer.addCard(deck.drawCard());

                                                    messageLabel.setText("Has tomado 4 cartas y pierdes tu turno");
                                                    updateGameView();

                                                    gameState = GameState.COMPUTER_TURN;

                                                    // Dar más tiempo antes del siguiente turno
                                                    new Thread(() -> {
                                                        try {
                                                            // Entre 3 y 4 segundos después de tomar las cartas
                                                            Thread.sleep(3000 + new Random().nextInt(1000));
                                                            javafx.application.Platform.runLater(() -> computerTurn());
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }).start();
                                                } catch (EmptyDeckException e) {
                                                    messageLabel.setText("El mazo está vacío");
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();

                                    humanTurn = false;

                                } catch (Exception e) {
                                    messageLabel.setText("Error al procesar +4: " + e.getMessage());
                                }
                                break;

                            default:
                                // Carta normal, pasa el turno al humano
                                gameState = GameState.PLAYER_TURN;
                                break;
                        }

                        playedCard = true;
                        updateGameView();
                        updateStatusMessage();
                        updateColorIndicator();
                        break; // Salir del bucle una vez que se ha jugado una carta

                    } catch (InvalidCardPlayException e) {
                        // Si hay error al jugar esta carta, probar con la siguiente
                        System.out.println("Error al jugar carta: " + e.getMessage());
                        continue;
                    }
                }
            }

            // Si no pudo jugar ninguna carta, tomar una del mazo
            if (!playedCard) {
                Card drawnCard = deck.drawCard();
                computerPlayer.addCard(drawnCard);
                messageLabel.setText("La computadora está tomando una carta...");
                System.out.println("Computadora tomó una carta");

                updateGameView();

                // Dar tiempo para mostrar que la computadora está tomando una carta
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> {
                            // Verificar si la carta tomada se puede jugar
                            if (drawnCard.canPlayOn(topCard)) {
                                messageLabel.setText("La computadora puede jugar la carta que tomó");

                                // Dar tiempo antes de jugar la carta tomada
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(2000);
                                        javafx.application.Platform.runLater(() -> computerTurn());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            } else {
                                messageLabel.setText("La computadora tomó una carta y no puede jugarla");
                                gameState = GameState.PLAYER_TURN;
                                updateStatusMessage();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (EmptyDeckException e) {
            messageLabel.setText("El mazo está vacío");
        }
    }

    /**
     * Maneja el clic en el mazo de cartas.
     *
     * @param event Evento de clic del ratón
     */
    @FXML
    private void handleDeckClick(MouseEvent event) {
        if (gameState != GameState.PLAYER_TURN) {
            messageLabel.setText("No es tu turno");
            return;
        }

        try {
            // Verificar si el jugador tiene alguna carta jugable
            boolean hasPlayableCard = false;

            for (Card card : humanPlayer.getHand()) {
                if (card.canPlayOn(topCard) || card.getColor() == currentColor) {
                    hasPlayableCard = true;
                    break;
                }
            }

            if (!hasPlayableCard) {
                // Tomar una carta del mazo
                Card drawnCard = deck.drawCard();
                humanPlayer.addCard(drawnCard);
                messageLabel.setText("Has tomado una carta");

                // Actualizar interfaz
                updateGameView();

                // Verificar si la carta tomada se puede jugar
                if (drawnCard.canPlayOn(topCard) || drawnCard.getColor() == currentColor) {
                    // El jugador puede jugar la carta que tomó
                    return;
                }

                // Pasar al turno del computador
                gameState = GameState.COMPUTER_TURN;
                updateStatusMessage();

                // Dar tiempo para mostrar los cambios antes del turno del computador
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> computerTurn());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Tienes cartas jugables");
            }
        } catch (EmptyDeckException e) {
            messageLabel.setText("El mazo está vacío");
        }
    }

    /**
     * Maneja el clic en el botón UNO.
     */
    @FXML
    private void handleUnoButtonAction() {
        // Si el botón dice "¡Atrapar UNO!" es para atrapar a la computadora
        if (unoButton.getText().equals("¡Atrapar UNO!")) {
            // El jugador atrapa a la computadora sin declarar UNO
            if (computerPlayer.getHand().size() == 1 && !computerPlayer.hasCalledUno()) {
                try {
                    // Penalizar a la computadora
                    computerPlayer.addCard(deck.drawCard());
                    messageLabel.setText("¡Atrapaste a la computadora! Toma una carta de penalización");
                    updateGameView();
                } catch (EmptyDeckException e) {
                    messageLabel.setText("El mazo está vacío");
                }
            } else {
                messageLabel.setText("No puedes atrapar a la computadora ahora");
            }

            // Restablecer el botón UNO
            unoButton.setText("¡UNO!");
            unoButton.setDisable(true);
        } else {
            // Comportamiento normal: declarar UNO para el jugador humano
            if (humanPlayer.getHand().size() == 1) {
                humanPlayer.setHasCalledUno(true);
                messageLabel.setText("¡UNO!");
                unoButton.setDisable(true);
            } else {
                messageLabel.setText("Solo puedes declarar UNO cuando te queda una carta");
            }
        }
    }

    /**
     * Sincroniza completamente el estado del color actual.
     * Asegura que tanto la variable currentColor como el color activo de topCard estén alineados.
     */
    private void syncColorState() {
        // Asegúrate de que el color actual coincida con el color activo de la carta superior
        currentColor = topCard.getActiveColor();
        System.out.println("DEBUG - Estado de color sincronizado. Color actual: " + currentColor);
        updateColorIndicator();
    }

    /**
     * Muestra el diálogo de selección de color.
     *
     * @return El color seleccionado
     */
    /**
     * Muestra el diálogo de selección de color.
     *
     * @return El color seleccionado
     */
    private CardColor showColorSelectionDialog() {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uno/view/ColorSelectionView.fxml"));
            Parent dialogRoot = loader.load();

            // Crear un nuevo escenario
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Selecciona un color");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setResizable(false);

            // Aplicar estilos
            Scene scene = new Scene(dialogRoot);
            scene.getStylesheets().add(getClass().getResource("/com/uno/view/css/styles.css").toExternalForm());
            dialogStage.setScene(scene);

            // Configurar el controlador
            ColorSelectionController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            // Mostrar el diálogo y esperar
            dialogStage.showAndWait();

            // Obtener el color seleccionado
            CardColor selectedColor = controller.getSelectedColor();
            System.out.println("Color seleccionado: " + selectedColor);

            // IMPORTANTE: Asegúrate de actualizar correctamente el color activo de la carta
            if (selectedColor != null) {
                topCard.setActiveColor(selectedColor);
                currentColor = selectedColor;
                updateColorIndicator();
            } else {
                // Valor por defecto si no se seleccionó un color
                CardColor defaultColor = CardColor.RED;
                topCard.setActiveColor(defaultColor);
                currentColor = defaultColor;
                updateColorIndicator();
            }

            return selectedColor;

        } catch (IOException e) {
            e.printStackTrace();
            CardColor defaultColor = CardColor.RED;
            topCard.setActiveColor(defaultColor);
            currentColor = defaultColor;
            updateColorIndicator();
            return defaultColor;
        }
    }

    /**
     * Manejador del botón UNO.
     */
    private class UnoButtonHandler implements javafx.event.EventHandler<javafx.event.ActionEvent> {
        @Override
        public void handle(javafx.event.ActionEvent e) {
            handleUnoButtonAction();
        }
    }

}