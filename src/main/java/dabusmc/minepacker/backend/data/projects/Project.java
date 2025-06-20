package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersion;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersionConverter;
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
    private boolean m_ChangesMade;

    public Project() {
        m_RegenerateInstance = false;
        m_ChangesMade = false;
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

    public boolean shouldSave() {
        return m_ChangesMade;
    }

    public void setName(String name) {
        m_Name = name;
        m_ChangesMade = true;
    }

    public void setVersion(String version) {
        m_Version = version;
        m_ChangesMade = true;
    }

    public void setMinecraftVersion(MinecraftVersion minecraftVersion) {
        m_MinecraftVersion = minecraftVersion;
        m_RegenerateInstance = true;
        m_ChangesMade = true;
    }

    public void setLoader(Mod.Loader loader) {
        m_Loader = loader;
        m_RegenerateInstance = true;
        m_ChangesMade = true;
    }

    public void instanceRegenerated() {
        m_RegenerateInstance = false;
    }

    public void saved() {
        m_ChangesMade = false;
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

        obj.put("name", m_Name);
        obj.put("version", m_Version);

        JSONObject mc = new JSONObject();

        mc.put("version", m_MinecraftVersion.toString());
        mc.put("loader", m_Loader.toString());

        obj.put("minecraft", mc);

        JSONObject instance = new JSONObject();

        instance.put("should_regenerate", m_RegenerateInstance);

        obj.put("instance", instance);

        return obj;
    }

    @Override
    public void getLoadedData(JSONObject data) {
        m_Name = data.get("name").toString();
        m_Version = data.get("version").toString();

        JSONObject mc = (JSONObject) data.get("minecraft");

        m_MinecraftVersion = MinecraftVersionConverter.getVersion(mc.get("version").toString());
        m_Loader = MinecraftVersionConverter.getLoader(mc.get("loader").toString());

        JSONObject instance = (JSONObject) data.get("instance");

        m_RegenerateInstance = Boolean.parseBoolean(instance.get("should_regenerate").toString());

        m_ChangesMade = false;
    }
}
