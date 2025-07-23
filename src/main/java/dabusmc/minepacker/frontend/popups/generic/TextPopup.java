package dabusmc.minepacker.frontend.popups.generic;

import dabusmc.minepacker.frontend.base.Popup;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class TextPopup extends Popup {

    private MPVBox m_Root;

    private String m_Text;

    public TextPopup(String text) {
        super();

        m_Root = new MPVBox(15.0);
        m_Text = text;
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.CENTER);
        m_Root.setPadding(new Insets(15.0));

        Label lbl = new Label(m_Text);
        lbl.setFont(new Font("System", 14));
        lbl.setWrapText(true);

        MPButton btn = new MPButton("Okay");
        btn.setBtnWidth(getWidth() / 2.0);
        btn.setOnAction(action -> finish());

        m_Root.getChildren().addAll(lbl, btn);
    }
}
