package com.uno.view;


import com.uno.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación JavaFX.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML de la vista del juego
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/uno/view/GameView.fxml"));
        Parent root = loader.load();

        // Obtener el controlador y configurar la ventana principal
        GameController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        // Configurar la escena
        Scene scene = new Scene(root,1024,768);
        scene.getStylesheets().add(getClass().getResource("/com/uno/view/css/styles.css").toExternalForm());

        // Configurar el escenario
        primaryStage.setTitle("UNO - Miniprojecto #3");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        // Iniciar el juego
        controller.initGame();
    }

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}