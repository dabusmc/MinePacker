package dabusmc.minepacker.frontend.components;

import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.control.ScrollPane;

public class MPScrollPane extends ScrollPane {

    public MPScrollPane() {
        super();
    }

    public void setPaneWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setPaneHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }

}
