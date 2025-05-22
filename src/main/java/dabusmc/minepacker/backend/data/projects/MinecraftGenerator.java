package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.data.MinecraftVersion;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;

public class MinecraftGenerator {

    public static MinecraftGenerator s_Instance;

    private HashMap<String, String> m_JsonFileVersionMap;

    public MinecraftGenerator() {
        if(s_Instance != null) {
            Logger.error("MinecraftGenerator", "There should only ever be one instance of MinecraftGenerator");
        } else {
            m_JsonFileVersionMap = new HashMap<>();
            generateVersionMap();

            s_Instance = this;
        }
    }

    public String getJsonPathForVersion(MinecraftVersion version) {
        if(m_JsonFileVersionMap.containsKey(version.toString())) {
            return m_JsonFileVersionMap.get(version.toString());
        }

        Logger.fatal("MinecraftGenerator", "Unrecognised MinecraftVersion '" + version.toString() + "'");
        return "";
    }

    private void generateVersionMap() {
        PackerFile versionManifestFile = new PackerFile(PackerFile.getResource("/dabusmc/minepacker/version_manifest_v2.json"));

        JSONParser parser = new JSONParser();
        try {
            JSONArray versionManifestVersions = (JSONArray) ((JSONObject) parser.parse(versionManifestFile.getReader())).get("versions");
            for(Object versionObj : versionManifestVersions) {
                JSONObject version = (JSONObject) versionObj;
                m_JsonFileVersionMap.put(version.get("id").toString(), version.get("url").toString());
            }

            Logger.message("MinecraftGenerator", "Created MC Version Table");
        } catch (IOException | ParseException e) {
            Logger.error("MinecraftGenerator", e.toString());
        }
    }


}
