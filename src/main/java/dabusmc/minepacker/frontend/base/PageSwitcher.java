package dabusmc.minepacker.frontend.base;

import dabusmc.minepacker.backend.logging.Logger;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageSwitcher {

    public static PageSwitcher s_Instance;

    private Stage m_Stage;
    private List<Page> m_Pages;
    private HashMap<String, Integer> m_NameToIDTable;
    private HashMap<Integer, String> m_IDToNameTable;
    private int m_CurrentPage;

    public PageSwitcher(Stage stage) {
        if (s_Instance != null) {
            Logger.error("PageSwitcher", "There should only ever be one instance of PageSwitcher");
        } else {
            s_Instance = this;

            m_Stage = stage;
            m_Pages = new ArrayList<>();
            m_NameToIDTable = new HashMap<String, Integer>();
            m_IDToNameTable = new HashMap<Integer, String>();
            m_CurrentPage = -1;
        }
    }

    /**
     * Registers a new page in the PageSwitcher with a given name. If this is the first page registered, this function will automatically call {@link #switchToPage(int)}
     * @param name The name of the Page being registered
     * @param page The Page being registered itself
     * @return The integer index of where the Page is stored internally
     */
    public int registerPage(String name, Page page) {
        int index = m_Pages.size();
        m_Pages.add(page);
        m_NameToIDTable.put(name, index);
        m_IDToNameTable.put(index, name);

        Scene pageScene = new Scene(m_Pages.get(index).getRoot(), m_Pages.get(index).getWidth(), m_Pages.get(index).getHeight());
        m_Pages.get(index).setScene(pageScene);

        m_Pages.get(index).reload();
        Logger.message("PageSwitcher", "Registered page with name '" + name + "'");

        if(index == 0) {
            m_Stage.setScene(pageScene);
            m_Stage.setWidth(page.getWidth());
            m_Stage.setHeight(page.getHeight());
            m_Stage.show();
            m_CurrentPage = index;

            Logger.message("PageSwitcher", "Switched to page '" + name + "'");
        }

        return index;
    }

    /**
     * Switches to the Page with the given index. If the index is invalid this function does nothing
     * @param index The index of the Page to switch into
     */
    public void switchToPage(int index) {
        if(index < 0 || index >= m_Pages.size())
            return;

        if(m_CurrentPage != -1) {
            m_Pages.get(m_CurrentPage).onClose();
        }
        m_CurrentPage = index;

        Page page = m_Pages.get(m_CurrentPage);
        m_Stage.setScene(page.getScene());
        m_Stage.setWidth(page.getWidth());
        m_Stage.setHeight(page.getHeight());
        m_Pages.get(m_CurrentPage).reload();

        Logger.message("PageSwitcher", "Switched to page '" + m_IDToNameTable.get(index) + "'");
    }

    /**
     * Retrieves the index of a given Page using its name (set when calling {@link #registerPage(String, Page)})
     * @param name The name of the Page to retrieve the index of
     * @return If the provided {@code name} can be found, the index of the Page using that name. If not, -1
     */
    public int getPageIndex(String name) {
        if(m_NameToIDTable.containsKey(name)) {
            return m_NameToIDTable.get(name);
        }

        return -1;
    }

}
