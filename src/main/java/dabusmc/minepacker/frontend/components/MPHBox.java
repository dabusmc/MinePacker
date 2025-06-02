package dabusmc.minepacker.frontend.components;

import javafx.scene.layout.HBox;

public class MPHBox extends HBox {

    public MPHBox() {
        super();
    }

    public MPHBox(double spacing) {
        super(spacing);
    }

    public void setBoxWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setBoxHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }

}
