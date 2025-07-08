package dabusmc.minepacker.frontend.components;

import javafx.scene.control.ProgressBar;

public class MPProgressBar extends ProgressBar {

    public MPProgressBar() {
        super();
    }

    public MPProgressBar(double startProgress) {
        super(startProgress);
    }

    public void setBarWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setBarHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }

}
