package dabusmc.minepacker.frontend.components;

import javafx.scene.layout.VBox;

public class MPVBox extends VBox {

    public MPVBox() {
        super();
    }

    public MPVBox(double spacing) {
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
