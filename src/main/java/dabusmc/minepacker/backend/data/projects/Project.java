package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.MinecraftVersion;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.io.serialization.ISaveable;
import org.json.simple.JSONObject;

public class Project implements ISaveable {

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
    private Mod.Loader m_Loader;
    private boolean m_RegenerateInstance;

    public Project() {
        m_RegenerateInstance = false;
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

    public Mod.Loader getLoader() {
        return m_Loader;
    }

    public boolean shouldRegenerateInstance() {
        return m_RegenerateInstance;
    }

    public void setName(String name) {
        m_Name = name;
    }

    public void setVersion(String version) {
        m_Version = version;
    }

    public void setMinecraftVersion(MinecraftVersion minecraftVersion) {
        m_MinecraftVersion = minecraftVersion;
        m_RegenerateInstance = true;
    }

    public void setLoader(Mod.Loader loader) {
        m_Loader = loader;
        m_RegenerateInstance = true;
    }

    public void instanceRegenerated() {
        m_RegenerateInstance = false;
    }

    @Override
    public String getFileName() {
        return PackerFile.convertNameToFileName(m_Name) + ".json";
    }

    @Override
    public String getSaveDirectory() {
        return PackerFile.combineFilePaths(MinePackerRuntime.s_Instance.getSettings().getProjectsDir(), PackerFile.convertNameToFileName(m_Name));
    }

    @Override
    public JSONObject getSavableObject() {
        JSONObject obj = new JSONObject();

        return obj;
    }

    @Override
    public void getLoadedData(JSONObject data) {

    }
}
