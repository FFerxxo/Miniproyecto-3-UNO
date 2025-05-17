package com.uno.threads;

import com.uno.exceptions.EmptyDeckException;
import com.uno.model.Game;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Hilo que maneja el turno del jugador computadora.
 * Añade un retardo para simular que la computadora está "pensando".
 */
public class ComputerPlayerThread implements Runnable {
    private final Game game;
    private final Callable<Void> onTurnCompleted;
    private final Random random;
    private volatile boolean running;

    /**
     * Constructor del hilo del jugador computadora.
     *
     * @param game Referencia al juego
     * @param onTurnCompleted Callback que se ejecuta cuando la computadora termina su turno
     */
    public ComputerPlayerThread(Game game, Callable<Void> onTurnCompleted) {
        this.game = game;
        this.onTurnCompleted = onTurnCompleted;
        this.random = new Random();
        this.running = true;
    }

    /**
     * Detiene el hilo.
     */
    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            // Simulamos que la computadora está "pensando"
            Thread.sleep(random.nextInt(2000) + 2000); // Entre 2 y 4 segundos

            if (!running) return;

            // La computadora realiza su jugada
            game.computerTurn();

            // Notificamos que el turno ha terminado
            if (onTurnCompleted != null) {
                onTurnCompleted.call();
            }
        } catch (EmptyDeckException e) {
            System.err.println("Error durante el turno de la computadora: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Hilo de la computadora interrumpido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en el hilo de la computadora: " + e.getMessage());
        }
    }
}