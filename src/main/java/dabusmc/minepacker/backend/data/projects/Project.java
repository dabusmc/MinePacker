package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersion;
import dabusmc.minepacker.backend.data.Mod;
import dabusmc.minepacker.backend.data.minecraft.MinecraftVersionConverter;
import dabusmc.minepacker.backend.http.ModApi;
import dabusmc.minepacker.backend.http.apis.ModrinthApi;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.io.serialization.ISaveable;
import dabusmc.minepacker.backend.io.serialization.ISaveableMT;
import javafx.concurrent.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Project implements ISaveableMT {

    public static Project generateDefaultProject() {
        Project p = new Project();
        // NOTE: Set the name to something the user is unlikely to use
        p.setName("cHJvamVjdA==");
        p.setVersion("1.0.0");
        p.setMinecraftVersion(MinecraftVersion.MC_1_21_5);
        p.setLoader(Mod.Loader.Vanilla);
        p.saved();
        return p;
    }

    private String m_Name;
    private String m_Version;
    private MinecraftVersion m_MinecraftVersion;
    private Mod.Loader m_Loader;
    private boolean m_RegenerateInstance;
    private boolean m_ChangesMade;

    private List<String> m_Mods = new ArrayList<>();

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

    public boolean hasMod(String id) {
        return m_Mods.contains(id);
    }

    public List<String> getModIDs() {
        return m_Mods;
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

    public void addMod(String id) {
        if(!hasMod(id)) {
            m_Mods.add(id);
            m_RegenerateInstance = true;
            m_ChangesMade = true;
        }
    }

    public void removeMod(String id) {
        if(hasMod(id)) {
            m_Mods.remove(id);
            m_RegenerateInstance = true;
            m_ChangesMade = true;
        }
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
    public Task<JSONObject> getSaveProcess() {
        return new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                int modsLength = m_Mods.size();
                long finalSize = (long) 3.0 + modsLength;

                updateProgress(0.0, finalSize);
                updateMessage("Writing Project Data");

                JSONObject out = new JSONObject();

                out.put("name", m_Name);
                out.put("version", m_Version);

                updateProgress(1.0, finalSize);
                updateMessage("Writing Minecraft Data");

                JSONObject mc = new JSONObject();

                mc.put("version", m_MinecraftVersion.toString());
                mc.put("loader", m_Loader.toString());

                out.put("minecraft", mc);

                updateProgress(2.0, finalSize);
                updateMessage("Writing Instance Data");

                JSONObject instance = new JSONObject();
                instance.put("should_regenerate", m_RegenerateInstance);
                out.put("instance", instance);

                JSONArray mods = new JSONArray();
                for(int i = 0; i < modsLength; i++) {
                    String modID = m_Mods.get(i);
                    Mod m = MinePackerRuntime.s_Instance.getModLibrary().getMod(modID);

                    updateProgress(3.0 + i, finalSize);
                    updateMessage("Writing Mod: '" + modID + "'");

                    JSONObject mod = new JSONObject();
                    mod.put("id", modID);
                    mod.put("provider", m.getProvider().toString());
                    mods.add(mod);
                }

                out.put("mods", mods);

                m_ChangesMade = false;

                updateProgress(finalSize, finalSize);

                return out;
            }
        };
    }

    @Override
    public Task<Void> getLoadProcess(JSONObject data) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int modsLength = ((JSONArray) data.get("mods")).size();
                long finalSize = (long) 3.0 + modsLength;

                updateProgress(0.0, finalSize);
                updateMessage("Reading Project Data");

                m_Name = data.get("name").toString();
                m_Version = data.get("version").toString();

                updateProgress(1.0, finalSize);
                updateMessage("Reading Minecraft Data");

                JSONObject mc = (JSONObject) data.get("minecraft");

                m_MinecraftVersion = MinecraftVersionConverter.getVersion(mc.get("version").toString());
                m_Loader = MinecraftVersionConverter.getLoader(mc.get("loader").toString());

                updateProgress(2.0, finalSize);
                updateMessage("Reading Instance Data");

                JSONObject instance = (JSONObject) data.get("instance");
                m_RegenerateInstance = Boolean.parseBoolean(instance.get("should_regenerate").toString());

                JSONArray mods = (JSONArray)data.get("mods");
                for(int i = 0; i < mods.size(); i++) {
                    JSONObject modJSON = (JSONObject) mods.get(i);
                    String provider = modJSON.get("provider").toString();

                    updateProgress(3.0 + i, finalSize);
                    updateMessage("Reading Mod: '" + modJSON.get("id") + "'");

                    switch(ModApi.typeFromString(provider)) {
                        case Modrinth -> {
                            Mod mod = MinePackerRuntime.s_Instance.getModApi().getModFromID(modJSON.get("id").toString());
                            addMod(mod.getID());
                            MinePackerRuntime.s_Instance.getModLibrary().registerMod(mod.getID(), mod);
                        }
                    }
                }

                m_ChangesMade = false;

                updateProgress(finalSize, finalSize);

                return null;
            }
        };
    }
}
