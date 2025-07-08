package dabusmc.minepacker.frontend.cards;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.Card;
import dabusmc.minepacker.frontend.components.MPButtonImage;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.threaded.ImageLoaderMT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.function.Consumer;

public class ModCard extends Card {

    private static final String MISSING_IMAGE_FILE = "/dabusmc/minepacker/images/missing_image.png";
    private static final String PLUS_IMAGE_FILE = "/dabusmc/minepacker/images/plus.png";
    private static final String CROSS_IMAGE_FILE = "/dabusmc/minepacker/images/cross.png";

    private MPHBox m_Root;

    private Mod m_Mod;

    private Consumer<Mod> m_OnModAdded = null;
    private Consumer<Mod> m_OnModRemoved = null;

    public ModCard(Mod mod) {
        m_Root = new MPHBox(7.5);
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
        m_Root.setAlignment(Pos.CENTER_LEFT);
        m_Root.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        Image icon;
        if(ImageLoaderMT.Instance.hasImageReady(m_Mod.getID())) {
            icon = ImageLoaderMT.Instance.getImage(m_Mod.getID());
        } else {
            InputStream iconData = ModCard.class.getResourceAsStream(MISSING_IMAGE_FILE);
            icon = new Image(iconData);
            ImageLoaderMT.Instance.addImage(m_Mod.getID(), m_Mod.getIconURL());
            Logger.info("ModCard", m_Mod.getIconURL());
        }

        ImageView view = new ImageView(icon);
        view.setFitWidth(getHeight());
        view.setFitHeight(getHeight());

        MPVBox modInfoRoot = new MPVBox(7.5);
        modInfoRoot.setBoxWidth(getWidth() - getHeight() - 100);
        modInfoRoot.setBoxHeight(getHeight());
        modInfoRoot.setAlignment(Pos.CENTER_LEFT);
        modInfoRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        Label name = new Label(m_Mod.getTitle());
        name.setFont(new Font("System", 14));

        Label description = new Label(m_Mod.getTagline());

        modInfoRoot.getChildren().addAll(name, description);

        MPButtonImage selectionButton = new MPButtonImage();
        if(MinePackerRuntime.Instance.getCurrentProject().hasMod(m_Mod.getID())) {
            InputStream selectionData = ModCard.class.getResourceAsStream(CROSS_IMAGE_FILE);
            Image selectionIcon = new Image(selectionData);
            selectionButton.setImage(selectionIcon, 50, 50);

            selectionButton.setOnAction(action -> {
                if(m_OnModRemoved != null) {
                    m_OnModRemoved.accept(m_Mod);
                }
            });
        } else {
            InputStream selectionData = ModCard.class.getResourceAsStream(PLUS_IMAGE_FILE);
            Image selectionIcon = new Image(selectionData);
            selectionButton.setImage(selectionIcon, 50, 50);

            selectionButton.setOnAction(action -> {
                if(m_OnModAdded != null) {
                    m_OnModAdded.accept(m_Mod);
                }
            });
        }

        m_Root.getChildren().addAll(view, modInfoRoot, selectionButton);
    }

    @Override
    protected int getSizeMultiplier() {
        return super.getSizeMultiplier();
    }

    public void setOnModAdded(Consumer<Mod> consumer) {
        m_OnModAdded = consumer;
    }

    public void setOnModRemove(Consumer<Mod> consumer) {
        m_OnModRemoved = consumer;
    }
}
