package dabusmc.minepacker.frontend.cards;

import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.frontend.base.Card;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;

public class ModCard extends Card {

    private static final URL MISSING_IMAGE_FILE = PackerFile.getResource("/dabusmc/minepacker/images/missing_image.png");

    private MPVBox m_Root;

    private Mod m_Mod;

    public ModCard(Mod mod) {
        m_Root = new MPVBox(7.5);
        m_Mod = mod;
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setBoxHeight(getHeight());
        m_Root.setAlignment(Pos.TOP_CENTER);
        m_Root.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        Label name = new Label(m_Mod.getTitle());
        m_Root.getChildren().addAll(name);
    }

    @Override
    protected int getSizeMultiplier() {
        return super.getSizeMultiplier();
    }
}
