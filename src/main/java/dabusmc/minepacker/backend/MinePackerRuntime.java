package dabusmc.minepacker.backend;

import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApi;
import dabusmc.minepacker.backend.mod_api.ModApiType;

public class MinePackerRuntime {

    public static MinePackerRuntime s_Instance = null;

    private LogLevel m_LogLevel;
    private ModApi m_ModApi = null;

    public MinePackerRuntime() {
        if (s_Instance != null) {
            Logger.error("MinePacker Runtime", "There should only ever be one instance of MinePackerRuntime");
        }  else {
            s_Instance = this;
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

    public LogLevel getLogLevel() {
        return m_LogLevel;
    }

    public void setLogLevel(LogLevel level) {
        m_LogLevel = level;
    }

}
