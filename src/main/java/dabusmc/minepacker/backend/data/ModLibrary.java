package dabusmc.minepacker.backend.data;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class ModLibrary {

    private HashMap<String, Mod> m_ModMap;

    public ModLibrary() {
        m_ModMap = new HashMap<>();
    }

    public boolean containsMod(String id) {
        return m_ModMap.containsKey(id);
    }

    public Mod getMod(String id) {
        if(!containsMod(id))
            return null;

        return m_ModMap.get(id);
    }

    public void registerMod(String id, Mod mod) {
        if(containsMod(id))
            return;

        Logger.message("ModLibrary", "Added Mod '" + mod.getTitle() + "'");
        m_ModMap.put(id, mod);
    }

    public void registerMod(String id, JSONObject mod) {
        registerMod(id, MinePackerRuntime.s_Instance.getModApi().constructModFromJsonObject(mod));
    }
}
