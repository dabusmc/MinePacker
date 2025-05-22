package dabusmc.minepacker.backend;

import dabusmc.minepacker.backend.data.Settings;
import dabusmc.minepacker.backend.data.projects.MinecraftGenerator;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApi;
import dabusmc.minepacker.backend.mod_api.ModApiType;
import dabusmc.minepacker.backend.serialization.Serializer;

public class MinePackerRuntime {

    public static MinePackerRuntime s_Instance = null;

    private LogLevel m_LogLevel;
    private Project m_CurrentProject;
    private ModApi m_ModApi = null;
    private Settings m_Settings;

    private MinecraftGenerator m_MCGenerator;

    public MinePackerRuntime() {
        if (s_Instance != null) {
            Logger.error("MinePackerRuntime", "There should only ever be one instance of MinePackerRuntime");
        }  else {
            s_Instance = this;

            setLogLevel(LogLevel.MESSAGE);

            m_MCGenerator = new MinecraftGenerator();
            m_Settings = new Settings();
            Serializer.load(m_Settings);

            Logger.info("MinePackerRuntime", "Initialised Runtime");
        }
    }

    public ModApi getModApi() {
        return m_ModApi;
    }

    public void constructModApi(ModApiType type) {
        m_ModApi = ModApi.Create(type);
    }

    public boolean isConnected() {
        return m_ModApi.isConnected();
    }

    public Project getCurrentProject() {
        return m_CurrentProject;
    }

    public void setCurrentProject(Project p) {
        m_CurrentProject = p;
    }

    public LogLevel getLogLevel() {
        return m_LogLevel;
    }

    public void setLogLevel(LogLevel level) {
        m_LogLevel = level;
    }

    public MinecraftGenerator getMCGenerator() {
        return m_MCGenerator;
    }

    public Settings getSettings() {
        return m_Settings;
    }

}
