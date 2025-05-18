package com.uno.adapter;

import com.uno.model.Card;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Adapta una {@link Card} a un {@link ImageView}.
 */
public final class CardImageAdapter {

    private CardImageAdapter() { }

    /**
     * Crea un {@code ImageView} para la carta indicada.
     *
     * @param card carta que se va a mostrar
     * @return imagen de la carta lista para la GUI
     */
    public static ImageView adapt(Card card) {
        String path = "/images/cards/" + card.getImageFileName();
        Image img   = new Image(CardImageAdapter.class.getResourceAsStream(path));
        ImageView v = new ImageView(img);
        v.setFitWidth(80);
        v.setPreserveRatio(true);
        return v;
    }
}
