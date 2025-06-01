package dabusmc.minepacker.frontend.components;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;

// TODO: Figure out a way for every Panel's title separator to have a gap between itself and the right edge of the screen
public class MPSeparator extends Separator {

    public MPSeparator() {
        super();
    }

    public MPSeparator(Orientation orientation) {
        super(orientation);
    }

    public void setSepWidth(double width) {
        setMaxWidth(width);
        setMinWidth(width);
        setPrefWidth(width);
    }

    public void setSepHeight(double height) {
        setMaxHeight(height);
        setMinHeight(height);
        setPrefHeight(height);
    }

}
