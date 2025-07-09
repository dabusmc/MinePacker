package dabusmc.minepacker.frontend.components.custom;

import dabusmc.minepacker.frontend.cards.ModCard;
import dabusmc.minepacker.frontend.components.MPButtonImage;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.function.Consumer;

public class MPSearchBar extends MPHBox {

    private static final String SEARCH_IMAGE_FILE = "/dabusmc/minepacker/images/search.png";

    private MPTextField m_SearchArea;
    private MPButtonImage m_SearchButton;

    private String m_SearchTerm;
    private Consumer<String> m_OnSearch;

    public MPSearchBar(double width) {
        super(15.0);

        m_OnSearch = null;

        setBoxWidth(width);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        m_SearchArea = new MPTextField();
        m_SearchArea.setPromptText("Search...");
        m_SearchArea.setText(m_SearchTerm);
        m_SearchArea.setFieldWidth(width * 0.5);

        InputStream iconData = ModCard.class.getResourceAsStream(SEARCH_IMAGE_FILE);
        Image icon = new Image(iconData);

        m_SearchButton = new MPButtonImage(icon, 10, 10);
        m_SearchButton.setOnAction(action -> {
            m_SearchTerm = m_SearchArea.getText();
            if(m_OnSearch != null) {
                m_OnSearch.accept(m_SearchTerm);
            }
        });

        getChildren().addAll(m_SearchArea, m_SearchButton);
    }

    public String getSearchTerm() {
        return m_SearchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        m_SearchTerm = searchTerm;
        m_SearchArea.setText(m_SearchTerm);
    }

    public void setOnSearch(Consumer<String> onSearch) {
        m_OnSearch = onSearch;
    }

}
