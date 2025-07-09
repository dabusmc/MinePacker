package dabusmc.minepacker.frontend.components;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class MPChoiceBox<T> extends ChoiceBox<T> {

    public MPChoiceBox() {
        super();
    }

    public MPChoiceBox(ObservableList<T> list) {
        super(list);
    }

    public void setBoxWidth(double width) {
        setMinWidth(width);
        setPrefWidth(width);
        setMaxWidth(width);
    }

    public void setBoxHeight(double height) {
        setPrefWidth(height);
        setPrefHeight(height);
        setMaxHeight(height);
    }

}
