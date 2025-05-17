package com.uno.threads;

import com.uno.exceptions.EmptyDeckException;
import com.uno.model.Game;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Hilo que maneja el temporizador para la declaración de UNO.
 * Cuando un jugador queda con una sola carta, este hilo inicia
 * y espera un tiempo aleatorio para que se declare UNO.
 */
public class UnoTimerThread implements Runnable {
    private final Game game;
    private final Callable<Void> onTimerCompleted;
    private final Random random;
    private volatile boolean running;

    /**
     * Constructor del hilo temporizador de UNO.
     *
     * @param game Referencia al juego
     * @param onTimerCompleted Callback que se ejecuta cuando el tiempo se agota
     */
    public UnoTimerThread(Game game, Callable<Void> onTimerCompleted) {
        this.game = game;
        this.onTimerCompleted = onTimerCompleted;
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
            // Esperamos un tiempo aleatorio entre 2 y 4 segundos
            Thread.sleep(random.nextInt(2000) + 2000);

            if (!running) return;

            // Si el jugador humano no ha declarado UNO, la computadora intenta atraparlo
            boolean caughtPlayer = game.computerCallCatchUno();

            // Si el jugador no ha declarado UNO, se aplicará una penalización
            if (!caughtPlayer) {
                game.checkUnoPenalty();
            }

            // Notificamos que el timer ha terminado
            if (onTimerCompleted != null) {
                onTimerCompleted.call();
            }
        } catch (EmptyDeckException e) {
            System.err.println("Error durante la verificación de UNO: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Hilo de temporizador UNO interrumpido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en el hilo de temporizador UNO: " + e.getMessage());
        }
    }
}