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
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class ModsPanel extends Panel {

    private MPVBox m_Root;

    private AddModsPopup m_Popup;

    public ModsPanel(float screenSpaceRatio, Orientation direction) {
        super(screenSpaceRatio, direction);
        m_Root = new MPVBox(5.0);
        m_Popup = null;
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

        m_Root.getChildren().addAll(titleSeparator, modView, bottomPanelSeparator, bottomPanel);
    }

    @Override
    public void reload() {
        super.reload();
        if(m_Popup != null) {
            m_Popup.reload();
        }
    }

    private MPScrollPane generateModView() {
        MPVBox cardsContainer = new MPVBox(7.5);
        cardsContainer.setBoxWidth(getWidth() * 0.875);
        cardsContainer.setAlignment(Pos.TOP_CENTER);
        cardsContainer.setPadding(new Insets(15.0));

        List<ModCard> cards = getMods();
        for(ModCard card : cards) {
            card.setWidth(getWidth() * 0.85);
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
        m_Popup = new AddModsPopup();
        m_Popup.setOnClose(() -> {
            reload();
            m_Popup = null;
            return 0;
        });
        m_Popup.display();
    }

    private List<ModCard> getMods() {
        List<ModCard> cards = new ArrayList<>();

        for(String modID : MinePackerRuntime.Instance.getCurrentProject().getModIDs()) {
            Mod mod = MinePackerRuntime.Instance.getModLibrary().getMod(modID);
            cards.add(new ModCard(mod));
        }

        return cards;
    }

    private void onModRemoved(Mod mod) {
        MinePackerRuntime.Instance.getCurrentProject().removeMod(mod.getID());
        reload();
    }
}
