package dabusmc.minepacker.frontend.page.projectpanels;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.cards.ModCard;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPScrollPane;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.popups.modspanel.AddModsPopup;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ModsPanel extends Panel {

    private MPVBox m_Root;

    public ModsPanel(float screenSpaceRatio, Orientation direction) {
        super(screenSpaceRatio, direction);
        m_Root = new MPVBox(5.0);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.TOP_CENTER);
        m_Root.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        addTitle("Mods");

        MPSeparator titleSeparator = new MPSeparator();
        titleSeparator.setOrientation(Orientation.HORIZONTAL);

        MPScrollPane modView = generateModView();

        MPSeparator bottomPanelSeparator = new MPSeparator();
        bottomPanelSeparator.setOrientation(Orientation.HORIZONTAL);

        MPVBox bottomPanel = generateBottomPanel();

//        Region spacer = new Region();
//        MPVBox.setVgrow(spacer, Priority.NEVER);
//        spacer.setMinHeight(getHeight() - (getHeight() * 0.2875));

        m_Root.getChildren().addAll(titleSeparator, modView, bottomPanelSeparator, bottomPanel);
    }

    private MPScrollPane generateModView() {
        MPVBox cardsContainer = new MPVBox(7.5);
        cardsContainer.setBoxWidth(getWidth() * 0.9);
        cardsContainer.setAlignment(Pos.TOP_CENTER);
        cardsContainer.setPadding(new Insets(15.0));

        List<ModCard> cards = getMods();
        for(ModCard card : cards) {
            card.setWidth(getWidth() * 0.9);
            card.initComponents();

            card.setOnModRemove(this::onModRemoved);

            cardsContainer.getChildren().add(card.getRoot());
        }

        MPScrollPane cardsScroller = new MPScrollPane();
        cardsScroller.setPaneWidth(getWidth() * 0.9);
        cardsScroller.setPaneHeight(getHeight() * 0.76);
        cardsScroller.setContent(cardsContainer);

        return cardsScroller;
    }

    private MPVBox generateBottomPanel() {
        MPVBox bottomPanelRoot = new MPVBox(7.5);
        bottomPanelRoot.setBoxWidth(getWidth());
        bottomPanelRoot.setBoxHeight(getHeight() * 0.075);
        bottomPanelRoot.setAlignment(Pos.CENTER);
        bottomPanelRoot.setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        MPButton addModsButton = new MPButton("Add Mods");
        addModsButton.setBtnWidth(getWidth() * 0.35);
        addModsButton.setOnAction(action -> addMods());

        bottomPanelRoot.getChildren().add(addModsButton);

        return bottomPanelRoot;
    }

    private void addMods() {
        AddModsPopup popup = new AddModsPopup();
        popup.setOnClose(() -> {
            reload();
            return 0;
        });
        popup.display();
    }

    private List<ModCard> getMods() {
        List<ModCard> cards = new ArrayList<>();

        for(String modID : MinePackerRuntime.s_Instance.getCurrentProject().getModIDs()) {
            Mod mod = MinePackerRuntime.s_Instance.getModLibrary().getMod(modID);
            cards.add(new ModCard(mod));
        }

        return cards;
    }

    private void onModRemoved(Mod mod) {

    }
}
