package dabusmc.minepacker.frontend.popups;

import dabusmc.minepacker.frontend.base.Popup;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPProgressBar;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ProgressBarPopup extends Popup {

    private MPVBox m_Root;

    private boolean m_Complete;

    private MPProgressBar m_Bar;
    private Label m_Label;

    public ProgressBarPopup() {
        m_Root = new MPVBox(15.0);

    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        if(m_Complete) {
            finish();
        }

        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.CENTER);
        m_Root.setPadding(new Insets(15.0));

        m_Bar = new MPProgressBar(0.0);
        m_Bar.setBarWidth(getWidth() * 0.75);

        m_Label = new Label();

        m_Root.getChildren().addAll(m_Bar, m_Label);
    }

    @Override
    public void display() {
        Scene scene = new Scene(getRoot(), getWidth(), getHeight());
        m_Stage.setScene(scene);
        setScene(scene);
        m_Stage.showAndWait();
    }

    public MPProgressBar getBar() {
        return m_Bar;
    }

    public Label getLabel() {
        return m_Label;
    }

    public void setComplete(boolean complete) {
        m_Complete = complete;
    }
}
