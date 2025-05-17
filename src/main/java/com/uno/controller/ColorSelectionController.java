package com.uno.controller;



import com.uno.model.enums.CardColor;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Controlador para la vista de selección de color.
 */
public class ColorSelectionController {

    @FXML
    private Rectangle redColorRect;

    @FXML
    private Rectangle blueColorRect;

    @FXML
    private Rectangle greenColorRect;

    @FXML
    private Rectangle yellowColorRect;

    private Stage dialogStage;
    private CardColor selectedColor;

    /**
     * Establece el escenario para este diálogo.
     *
     * @param dialogStage escenario del diálogo
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Maneja el evento de selección de color.
     *
     * @param event evento de clic del ratón
     */
    @FXML
    private void handleColorSelection(MouseEvent event) {
        // Obtener el rectángulo que fue clicado
        Rectangle clickedRect = (Rectangle) event.getSource();

        // Determinar qué color se seleccionó
        if (clickedRect == redColorRect) {
            selectedColor = CardColor.RED;
            System.out.println("Color seleccionado: Rojo");
        } else if (clickedRect == blueColorRect) {
            selectedColor = CardColor.BLUE;
            System.out.println("Color seleccionado: Azul");
        } else if (clickedRect == greenColorRect) {
            selectedColor = CardColor.GREEN;
            System.out.println("Color seleccionado: Verde");
        } else if (clickedRect == yellowColorRect) {
            selectedColor = CardColor.YELLOW;
            System.out.println("Color seleccionado: Amarillo");
        }

        // Cerrar la ventana de diálogo
        dialogStage.close();
    }

    /**
     * Obtiene el color seleccionado.
     *
     * @return el color seleccionado
     */
    public CardColor getSelectedColor() {
        return selectedColor;
    }
}

