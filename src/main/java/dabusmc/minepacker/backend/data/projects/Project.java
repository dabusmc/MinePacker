package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.data.MinecraftVersion;

public class Project {

    private String m_Name;
    private String m_Version;
    private MinecraftVersion m_MinecraftVersion;

    public Project() {

    }

    public String getName() {
        return m_Name;
    }

    public String getVersion() {
        return m_Version;
    }

    public MinecraftVersion getMinecraftVersion() {
        return m_MinecraftVersion;
    }

    public void setName(String name) {
        m_Name = name;
    }

    public void setVersion(String version) {
        m_Version = version;
    }

    public void setMinecraftVersion(MinecraftVersion minecraftVersion) {
        m_MinecraftVersion = minecraftVersion;
    }

}
