package dabusmc.minepacker.frontend.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class MPButton extends Button {

    public MPButton() {
        super();
    }

    public MPButton(String text) {
        super(text);
    }

    public void setBtnWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setBtnHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }

}
