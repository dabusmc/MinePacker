package dabusmc.minepacker.frontend.popups.modspanel;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.frontend.base.Popup;
import dabusmc.minepacker.frontend.base.ScreenRatio;
import dabusmc.minepacker.frontend.cards.ModCard;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPScrollPane;
import dabusmc.minepacker.frontend.components.MPVBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddModsPopup extends Popup {

    private MPVBox m_Root;

    private int m_CurrentPageIndex;
    private int m_MaxPageIndex;
    private String m_SearchParameter;

    public AddModsPopup() {
        super("MinePacker - Add Mods");

        m_Root = new MPVBox(7.5);
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.TOP_CENTER);
        m_Root.setPadding(new Insets(15.0));

        // Show Search Bar and Search Settings
        TextField tempSearchBar = new TextField();

        // Get the Mods from the API
        List<ModCard> cards = searchForMods();

        // Display the Mods
        MPVBox cardsContainer = new MPVBox(7.5);
        cardsContainer.setBoxWidth(getWidth() * 0.85);
        cardsContainer.setAlignment(Pos.TOP_CENTER);
        cardsContainer.setPadding(new Insets(15.0));

        for(ModCard card : cards) {
            card.setWidth(getWidth() * 0.85);
            card.initComponents();
            cardsContainer.getChildren().add(card.getRoot());
        }

        MPScrollPane cardsScroller = new MPScrollPane();
        cardsScroller.setPaneWidth(getWidth() * 0.9);
        cardsScroller.setPaneHeight(getHeight() * 0.85);
        cardsScroller.setContent(cardsContainer);

        // Show the Search Numbers
        MPHBox numbersPanel = generateNumbersPanel();

        // Add Everything to root
        m_Root.getChildren().addAll(tempSearchBar, cardsScroller, numbersPanel);
    }

    @Override
    public ScreenRatio getDimensionRatio() {
        return ScreenRatio.One_One;
    }

    @Override
    public int getDimensionMultiplier() {
        return 80;
    }

    private MPHBox generateNumbersPanel() {
        MPHBox numbersPanelRoot = new MPHBox(7.5);
        numbersPanelRoot.setBoxWidth(getWidth());
        numbersPanelRoot.setAlignment(Pos.CENTER);
        numbersPanelRoot.setPadding(new Insets(15.0));

        Label currentPage = new Label(Integer.toString(m_CurrentPageIndex + 1));
        numbersPanelRoot.getChildren().addAll(currentPage);

        return numbersPanelRoot;
    }

    private List<ModCard> searchForMods() {
        JSONObject searchData = MinePackerRuntime.s_Instance.getModApi().search(m_SearchParameter, m_CurrentPageIndex, 10);
        m_MaxPageIndex = Integer.parseInt(searchData.get("total_hits").toString()) / 10;

        List<ModCard> cards = new ArrayList<>();

        JSONArray modsArray = (JSONArray) searchData.get("hits");
        for(Object modObj : modsArray) {
            JSONObject modJSON = (JSONObject) modObj;
            String id = modJSON.get("project_id").toString();

            if(MinePackerRuntime.s_Instance.getModLibrary().containsMod(id)) {
                cards.add(new ModCard(MinePackerRuntime.s_Instance.getModLibrary().getMod(id)));
            } else {
                // NOTE: Recapturing the Mod JSON from a different URL means that the data we get is consistent with other parts of the program
                //       Any data that we capture from the search can then be used to simply update the data from the recaptured Mod JSON
                Mod mod = MinePackerRuntime.s_Instance.getModApi().getModFromID(id);
                MinePackerRuntime.s_Instance.getModLibrary().registerMod(id, mod);

                cards.add(new ModCard(mod));
            }
        }

        return cards;
    }
}
