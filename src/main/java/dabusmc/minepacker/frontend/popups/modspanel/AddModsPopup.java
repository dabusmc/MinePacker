package dabusmc.minepacker.frontend.popups.modspanel;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.http.ModSortingOrder;
import dabusmc.minepacker.frontend.base.Popup;
import dabusmc.minepacker.frontend.base.ScreenRatio;
import dabusmc.minepacker.frontend.cards.ModCard;
import dabusmc.minepacker.frontend.components.*;
import dabusmc.minepacker.frontend.components.custom.MPChoiceBoxWithLabel;
import dabusmc.minepacker.frontend.components.custom.MPSearchBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddModsPopup extends Popup {

    private MPVBox m_Root;

    private int m_CurrentPageIndex;
    private int m_MaxPageIndex;
    private String m_SearchParameter;

    private ModSortingOrder m_SortingOrder;
    private String m_SortingOrderString;

    public AddModsPopup() {
        super("MinePacker - Add Mods");

        m_Root = new MPVBox(7.5);
        m_SearchParameter = "";

        m_SortingOrder = ModSortingOrder.Relevance;
        m_SortingOrderString = "Relevance";
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    @Override
    public void initComponents() {
        m_Root.setBoxWidth(getWidth());
        m_Root.setAlignment(Pos.TOP_CENTER);
        m_Root.setPadding(new Insets(7.5));

        // Show Search Bar and Search Settings
        MPHBox searchSettings = new MPHBox(7.5);
        searchSettings.setBoxWidth(getWidth() * 0.85);
        searchSettings.setAlignment(Pos.TOP_LEFT);

        MPSearchBar searchBar = new MPSearchBar(getWidth() * 0.5);
        searchBar.setSearchTerm(m_SearchParameter);
        searchBar.setOnSearch(term -> {
            m_SearchParameter = term;
            m_CurrentPageIndex = 0;
            reload();
        });

        ObservableList<String> options = FXCollections.observableArrayList("Relevance", "Downloads", "Follows", "Newest", "Updated");

        MPChoiceBoxWithLabel sortingMethod = new MPChoiceBoxWithLabel(getWidth() * 0.5, "Sort By", options);
        sortingMethod.setSelection(options.indexOf(m_SortingOrderString));

        sortingMethod.setOnChanged(method -> {
            m_SortingOrderString = method;
            m_SortingOrder = MinePackerRuntime.Instance.getModApi().stringToSortingOrder(method);
            m_CurrentPageIndex = 0;
            reload();
        });

        searchSettings.getChildren().addAll(searchBar, sortingMethod);

        // Get the Mods from the API
        List<ModCard> cards = searchForMods();

        // Display the Mods
        MPVBox cardsContainer = new MPVBox(7.5);
        cardsContainer.setBoxWidth(getWidth() * 0.85);
        cardsContainer.setAlignment(Pos.TOP_CENTER);
        cardsContainer.setPadding(new Insets(15.0));

        if(cards.isEmpty()) {
            Label noResultsLabel = new Label("No results found for '" + m_SearchParameter + "' :(");
            cardsContainer.getChildren().addAll(noResultsLabel);
        }

        for(ModCard card : cards) {
            card.setWidth(getWidth() * 0.85);
            card.initComponents();

            card.setOnModAdded(this::onModAdded);
            card.setOnModRemove(this::onModRemoved);

            cardsContainer.getChildren().add(card.getRoot());
        }

        MPScrollPane cardsScroller = new MPScrollPane();
        cardsScroller.setPaneWidth(getWidth() * 0.9);
        cardsScroller.setPaneHeight(getHeight() * 0.825);
        cardsScroller.setContent(cardsContainer);

        // Show the Search Numbers
        MPHBox numbersPanel = generateNumbersPanel();

        // Add Everything to root
        m_Root.getChildren().addAll(searchSettings, cardsScroller, numbersPanel);
    }

    @Override
    public ScreenRatio getDimensionRatio() {
        return ScreenRatio.One_One;
    }

    @Override
    public int getDimensionMultiplier() {
        return 80;
    }

    // TODO: When reloaded, save the scroll position
    private void onModAdded(Mod mod) {
        MinePackerRuntime.Instance.getCurrentProject().addMod(mod.getID());
        reload();
    }

    private void onModRemoved(Mod mod) {
        MinePackerRuntime.Instance.getCurrentProject().removeMod(mod.getID());
        reload();
    }

    private MPHBox generateNumbersPanel() {
        MPHBox numbersPanelRoot = new MPHBox(15.0);
        numbersPanelRoot.setBoxWidth(getWidth());
        numbersPanelRoot.setAlignment(Pos.CENTER);
        numbersPanelRoot.setPadding(new Insets(15.0));

        Button leftButton = new Button("<");
        leftButton.setOnAction(action -> {
            m_CurrentPageIndex -= 1;
            reload();
        });

        if(m_CurrentPageIndex == 0)
        {
            leftButton.setDisable(true);
        }
        else
        {
            leftButton.setDisable(false);
        }

        numbersPanelRoot.getChildren().addAll(leftButton);

        Label currentPage = new Label(Integer.toString(m_CurrentPageIndex + 1));
        numbersPanelRoot.getChildren().addAll(currentPage);

        Button rightButton = new Button(">");
        rightButton.setOnAction(action -> {
            m_CurrentPageIndex += 1;
            reload();
        });

        if(m_CurrentPageIndex == m_MaxPageIndex)
        {
            rightButton.setDisable(true);
        }
        else
        {
            rightButton.setDisable(false);
        }

        numbersPanelRoot.getChildren().addAll(rightButton);

        return numbersPanelRoot;
    }

    private List<ModCard> searchForMods() {
        JSONObject searchData = MinePackerRuntime.Instance.getModApi().search(m_SearchParameter, m_CurrentPageIndex, 10, m_SortingOrder);
        m_MaxPageIndex = Integer.parseInt(searchData.get("total_hits").toString()) / 10;

        List<ModCard> cards = new ArrayList<>();

        JSONArray modsArray = (JSONArray) searchData.get("hits");
        for(Object modObj : modsArray) {
            JSONObject modJSON = (JSONObject) modObj;
            String id = modJSON.get("project_id").toString();

            if(MinePackerRuntime.Instance.getModLibrary().containsMod(id)) {
                cards.add(new ModCard(MinePackerRuntime.Instance.getModLibrary().getMod(id)));
            } else {
                // NOTE: Recapturing the Mod JSON from a different URL means that the data we get is consistent with other parts of the program
                //       Any data that we capture from the search can then be used to simply update the data from the recaptured Mod JSON
                Mod mod = MinePackerRuntime.Instance.getModApi().getModFromID(id);
                MinePackerRuntime.Instance.getModLibrary().registerMod(id, mod);

                cards.add(new ModCard(mod));
            }
        }

        return cards;
    }
}
