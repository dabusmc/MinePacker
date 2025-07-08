package dabusmc.minepacker.backend;

import dabusmc.minepacker.backend.authorisation.AuthManager;
import dabusmc.minepacker.backend.data.ModLibrary;
import dabusmc.minepacker.backend.data.Settings;
import dabusmc.minepacker.backend.data.projects.instances.InstanceManager;
import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.LogLevel;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.http.ModApi;
import dabusmc.minepacker.backend.http.ModApiType;
import dabusmc.minepacker.backend.io.serialization.Serializer;

public class MinePackerRuntime {

    public static MinePackerRuntime s_Instance = null;

    private LogLevel m_LogLevel;
    private Project m_CurrentProject;
    private ModApi m_ModApi = null;
    private Settings m_Settings;

    private OS m_OS;
    private SysArch m_SystemArch;

    private InstanceManager m_InstanceManager;
    private AuthManager m_AuthenticationManager;

    private ModLibrary m_ModLibrary;

    public MinePackerRuntime() {
        if (s_Instance != null) {
            Logger.error("MinePackerRuntime", "There should only ever be one instance of MinePackerRuntime");
        }  else {
            s_Instance = this;

            setLogLevel(LogLevel.MESSAGE);

            m_CurrentProject = null;

            m_InstanceManager = new InstanceManager();
            m_AuthenticationManager = new AuthManager();
            m_Settings = new Settings();
            Serializer.registerForAutosave(m_Settings);
            m_ModLibrary = new ModLibrary();

            String os = System.getProperty("os.name");
            if(os != null) {
                if(os.contains("Windows") || os.contains("windows")) {
                    m_OS = OS.Windows;
                } else if(os.contains("Mac")) {
                    m_OS = OS.Mac;
                } else if(os.contains("Linux") || os.contains("linux")) {
                    m_OS = OS.Linux;
                } else {
                    m_OS = OS.Unknown;
                }
            } else {
                m_OS = OS.Unknown;
            }

            String arch = System.getProperty("os.arch");
            if(arch != null) {
                if(arch.contains("x86") || arch.contains("i386") || arch.contains("i486") || arch.contains("i586") || arch.contains("i686")) {
                    m_SystemArch = SysArch.x86;
                } else if(arch.contains("x86_64") || arch.contains("amd64")) {
                    m_SystemArch = SysArch.x64;
                } else if(arch.contains("aarch64")) {
                    m_SystemArch = SysArch.arm64;
                } else {
                    m_SystemArch = SysArch.Unknown;
                }
            } else {
                m_SystemArch = SysArch.Unknown;
            }

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

    public InstanceManager getInstanceManager() {
        return m_InstanceManager;
    }

    public AuthManager getAuthenticationManager() { return m_AuthenticationManager; }

    public Settings getSettings() {
        return m_Settings;
    }

    public ModLibrary getModLibrary() {
        return m_ModLibrary;
    }

    public OS getOS() {
        return m_OS;
    }

    public SysArch getSystemArch() {
        return m_SystemArch;
    }

    public enum OS {
        Windows("windows"),
        Mac("mac-os"),
        Linux("linux"),
        Unknown("unknown");

        private final String m_Name;

        OS(String name) {
            m_Name = name;
        }

        @Override
        public String toString() {
            return m_Name;
        }
    }

    public enum SysArch {
        x64("x64"),
        x86("x86"),
        arm64("arm64"),
        Unknown("unknown");

        private final String m_Name;

        SysArch(String name) {
            m_Name = name;
        }

        @Override
        public String toString() {
            return m_Name;
        }
    }

}
