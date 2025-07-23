package dabusmc.minepacker.frontend.popups.generic;

import dabusmc.minepacker.frontend.base.Popup;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class YesNoPopup extends Popup {

    private MPVBox m_Root;

    private String m_Text;

    private boolean m_Answer = false;

    public YesNoPopup(String text)
    {
        super();

        m_Root = new MPVBox(15.0);

        m_Text = text;
    }

    public boolean getAnswer()
    {
        return m_Answer;
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

        Label text = new Label(m_Text);
        text.setFont(new Font("System", 12));
        text.setWrapText(true);

        MPHBox buttons = new MPHBox(getWidth() / 40.0d);
        buttons.setAlignment(Pos.CENTER);

        MPButton yesButton = new MPButton("Yes");
        yesButton.setBtnWidth(getWidth() / 3.0d);
        yesButton.setOnAction(action -> {
            m_Answer = true;
            finish();
        });

        MPButton noButton = new MPButton("No");
        noButton.setBtnWidth(getWidth() / 3.0d);
        noButton.setOnAction(action -> {
            m_Answer = false;
            finish();
        });

        buttons.getChildren().addAll(yesButton, noButton);

        m_Root.getChildren().addAll(text, buttons);
    }

}
