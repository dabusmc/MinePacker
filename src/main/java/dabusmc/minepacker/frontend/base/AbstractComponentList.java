package dabusmc.minepacker.frontend.base;

import javafx.scene.layout.Pane;

public class AbstractComponentList {

    public void reload() {
        if(getRoot() != null) {
            getRoot().getChildren().clear();
        }
        initComponents();
    }

    public Pane getRoot() {
        return new Pane();
    }

    public void initComponents() {

    }

}
