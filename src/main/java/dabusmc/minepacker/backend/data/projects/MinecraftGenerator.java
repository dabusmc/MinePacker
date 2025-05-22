package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.MinecraftVersion;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class MinecraftGenerator {

    private HashMap<String, String> m_VanillaJsonFileVersionMap;
    private HashMap<String, JSONObject> m_JREVersionMap;

    public MinecraftGenerator() {
        m_VanillaJsonFileVersionMap = new HashMap<>();
        generateVersionMap();
        m_JREVersionMap = new HashMap<>();
        generateJREMap();
    }

    public boolean canRunMinecraft(MinePackerRuntime.OS os, MinePackerRuntime.SysArch arch) {
        if(os == MinePackerRuntime.OS.Unknown || arch == MinePackerRuntime.SysArch.Unknown)
            return false;

        if(os == MinePackerRuntime.OS.Windows) {
            return arch == MinePackerRuntime.SysArch.x64 || arch == MinePackerRuntime.SysArch.x86 || arch == MinePackerRuntime.SysArch.arm64;
        }

        // TODO: Make sure other operating systems are supported
        return false;
    }

    public void generateInstance(Project prj) {
        if(!canRunMinecraft(MinePackerRuntime.s_Instance.getOS(), MinePackerRuntime.s_Instance.getSystemArch())) {
            Logger.error("MinecraftGenerator", "Configuration '"
                    + MinePackerRuntime.s_Instance.getOS().toString()
                    + "-"
                    + MinePackerRuntime.s_Instance.getSystemArch().toString()
                    + "' isn't currently supported");
            return;
        }

        String parentFolder = PackerFile.combineFilePaths(prj.getSaveDirectory(), "instance");
        PackerFile.createFolderIfNotExist(parentFolder);

        // Version File
        String versionFile = downloadCorrectVersion(parentFolder, prj.getMinecraftVersion());
        if(versionFile.isEmpty()) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance: Unrecognised Minecraft Version");
            return;
        }

        // JRE
        JSONObject jreData = getCorrectJREDataForSystem(MinePackerRuntime.s_Instance.getOS(), MinePackerRuntime.s_Instance.getSystemArch());
        if(jreData == null) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance: Architecture not supported");
            return;
        }
    }

    private String downloadCorrectVersion(String parentFolder, MinecraftVersion version) {
        String versionsFolder = PackerFile.combineFilePaths(parentFolder, "versions");
        PackerFile.createFolderIfNotExist(versionsFolder);
        String versionFolder = PackerFile.combineFilePaths(versionsFolder, version.toString());
        PackerFile.createFolderIfNotExist(versionFolder);
        String outputFile = PackerFile.combineFilePaths(versionFolder, version + ".json");

        String jsonPath = getJsonPathForVersion(version);

        if(jsonPath.isEmpty()) {
            return "";
        }

        PackerFile.deleteFileIfExists(outputFile);
        long bytesWritten = MinePackerRuntime.s_Instance.getModApi().downloadFromURL(jsonPath, outputFile);

        if(bytesWritten == 0) {
            return "";
        }

        return outputFile;
    }

    public String getJsonPathForVersion(MinecraftVersion version) {
        // TODO: Make this support more than Vanilla
        if(m_VanillaJsonFileVersionMap.containsKey(version.toString())) {
            return m_VanillaJsonFileVersionMap.get(version.toString());
        }

        Logger.fatal("MinecraftGenerator", "Unrecognised MinecraftVersion '" + version.toString() + "'");
        return "";
    }

    public JSONObject getCorrectJREDataForSystem(MinePackerRuntime.OS os, MinePackerRuntime.SysArch arch) {
        switch(os) {
            case Windows -> {
                return m_JREVersionMap.get(os + "-" + arch.toString());
            }
            case Mac -> {
                if(arch == MinePackerRuntime.SysArch.x64) {
                    return m_JREVersionMap.get("mac-os");
                } else if(arch == MinePackerRuntime.SysArch.arm64) {
                    return m_JREVersionMap.get("mac-os-arm64");
                } else {
                    return null;
                }
            }
            case Linux -> {
                if(arch == MinePackerRuntime.SysArch.x64) {
                    return m_JREVersionMap.get("linux");
                } else if(arch == MinePackerRuntime.SysArch.x86) {
                    return m_JREVersionMap.get("linux-i386");
                } else {
                    return null;
                }
            }
            default -> {
                return null;
            }
        }
    }

    private void generateVersionMap() {
        PackerFile versionManifestFile = new PackerFile(PackerFile.getResource("/dabusmc/minepacker/version_manifest_v2.json"), false);

        JSONArray versionManifestVersions = (JSONArray) versionManifestFile.readIntoJson().get("versions");
        for(Object versionObj : versionManifestVersions) {
            JSONObject version = (JSONObject) versionObj;
            m_VanillaJsonFileVersionMap.put(version.get("id").toString(), version.get("url").toString());
        }
    }

    private void generateJREMap() {
        PackerFile jreManifestFile = new PackerFile(PackerFile.getResource("/dabusmc/minepacker/jre_manifest.json"), false);

        JSONObject jreVersions = (JSONObject) jreManifestFile.readIntoJson().get("manifest");
        m_JREVersionMap.put("linux", (JSONObject) jreVersions.get("linux"));
        m_JREVersionMap.put("linux-i386", (JSONObject) jreVersions.get("linux-i386"));
        m_JREVersionMap.put("mac-os", (JSONObject) jreVersions.get("mac-os"));
        m_JREVersionMap.put("mac-os-arm64", (JSONObject) jreVersions.get("mac-os-arm64"));
        m_JREVersionMap.put("windows-arm64", (JSONObject) jreVersions.get("windows-arm64"));
        m_JREVersionMap.put("windows-x64", (JSONObject) jreVersions.get("windows-x64"));
        m_JREVersionMap.put("windows-x86", (JSONObject) jreVersions.get("windows-x86"));
    }


}
