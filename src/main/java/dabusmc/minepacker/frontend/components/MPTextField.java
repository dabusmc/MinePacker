package dabusmc.minepacker.frontend.components;

import javafx.scene.control.TextField;

public class MPTextField extends TextField {

    public MPTextField() {

    }

    public MPTextField(String text) {
        super(text);
    }

    public void setFieldWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setFieldHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }
}
