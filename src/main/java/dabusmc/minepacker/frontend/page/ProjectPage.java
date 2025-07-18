package dabusmc.minepacker.frontend.page;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.frontend.base.Page;
import dabusmc.minepacker.frontend.base.Panel;
import dabusmc.minepacker.frontend.components.MPButton;
import dabusmc.minepacker.frontend.components.MPHBox;
import dabusmc.minepacker.frontend.components.MPSeparator;
import dabusmc.minepacker.frontend.components.MPVBox;
import dabusmc.minepacker.frontend.page.projectpanels.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ProjectPage extends Page {

    private MPHBox m_Root;

    private LinkedHashMap<String, Panel> m_ProjectPanels;
    private String m_CurrentPanel;
    private List<String> m_DisabledPanels;

    private PPLeftPanel m_LeftPanel;

    public ProjectPage() {
        m_Root = new MPHBox();

        m_CurrentPanel = "blank";

        m_ProjectPanels = new LinkedHashMap<>();
        m_ProjectPanels.put("blank", new PPBlankRightPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Overview", new OverviewPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Mods", new ModsPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Configs", new ConfigsPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Notes", new NotesPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Themes", new ThemesPanel(0.75f, Orientation.HORIZONTAL));
        m_ProjectPanels.put("Settings", new SettingsPanel(0.75f, Orientation.HORIZONTAL));

        m_LeftPanel = new PPLeftPanel(0.25f, Orientation.HORIZONTAL, m_ProjectPanels);
    }

    @Override
    public void initComponents() {
        m_DisabledPanels = new ArrayList<>();

        if(MinePackerRuntime.Instance.getCurrentProject().getLoader() == Mod.Loader.Vanilla) {
            m_DisabledPanels.add("Mods");
            m_DisabledPanels.add("Configs");
        }

        m_DisabledPanels.add("Configs");
        m_DisabledPanels.add("Notes");
        m_DisabledPanels.add("Themes");

        addPanel(m_LeftPanel);

        MPSeparator separator = new MPSeparator();
        separator.setOrientation(Orientation.VERTICAL);
        m_Root.getChildren().addAll(separator);

        addPanel(m_ProjectPanels.get(m_LeftPanel.getCurrentPanel()));
    }

    @Override
    public Pane getRoot() {
        return m_Root;
    }

    public class PPLeftPanel extends Panel {

        private MPVBox m_Root;

        private HashMap<String, Panel> m_Panels;
        private String m_CurrentPanel;

        public PPLeftPanel(float screenSpaceRatio, Orientation direction, HashMap<String, Panel> panels) {
            super(screenSpaceRatio, direction);

            m_Root = new MPVBox(7.5);
            m_Panels = panels;
            m_CurrentPanel = "blank";
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

            for(String key : m_Panels.keySet()) {
                if(key.equals("blank")) {
                    continue;
                }

                MPButton button = new MPButton(key);
                button.setBtnWidth(getWidth() * 0.75f);

                button.setOnAction(action -> {
                    m_CurrentPanel = key;
                    getPage().reload();
                });

                if(key.equals(m_CurrentPanel) || m_DisabledPanels.contains(key)) {
                    button.setDisable(true);
                }

                m_Root.getChildren().add(button);
            }
        }

        public String getCurrentPanel() {
            return m_CurrentPanel;
        }

    }

    public class PPBlankRightPanel extends Panel {

        private MPVBox m_Root;

        public PPBlankRightPanel(float screenSpaceRatio, Orientation direction) {
            super(screenSpaceRatio, direction);

            m_Root = new MPVBox();
        }

        @Override
        public Pane getRoot() {
            return m_Root;
        }

        @Override
        public void initComponents() {
            m_Root.setBoxWidth(getWidth());
            m_Root.setAlignment(Pos.TOP_CENTER);
            m_Root.setPadding(new Insets(7.5f));
        }

    }

}
