package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.data.MinecraftVersion;

public class Project {

    public static Project generateDefaultProject() {
        Project p = new Project();
        p.setName("Default Project");
        p.setVersion("1.0.0");
        p.setMinecraftVersion(MinecraftVersion.MC_1_21_5);
        return p;
    }

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
