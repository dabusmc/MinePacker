package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.MinePackerApp;
import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.MinecraftVersion;
import dabusmc.minepacker.backend.io.MCFileSystem;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApi;
import dabusmc.minepacker.backend.util.ListPair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinecraftGenerator {

    private static final String MINECRAFT_RESOURCES_URL = "https://resources.download.minecraft.net";

    private final HashMap<String, String> m_VanillaJsonFileVersionMap;
    private final HashMap<String, JSONObject> m_JREVersionMap;

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
        // Check PC Configuration
        if(!canRunMinecraft(MinePackerRuntime.s_Instance.getOS(), MinePackerRuntime.s_Instance.getSystemArch())) {
            Logger.error("MinecraftGenerator", "Configuration '"
                    + MinePackerRuntime.s_Instance.getOS().toString()
                    + "-"
                    + MinePackerRuntime.s_Instance.getSystemArch().toString()
                    + "' isn't currently supported");
            return;
        }

        // Check Login

        // Setup
        MinecraftInstance instance = new MinecraftInstance();
        instance.InstanceProject = prj;

        // Pre-Generate Folder Structure
        instance.FileSystem = new MCFileSystem();
        instance.FileSystem.setProject(prj);
        PackerFile.deleteFolderIfExists(instance.FileSystem.getBaseDirectory());
        instance.FileSystem.generateFileStructure();

        // Generate Instance
        generateVersion(instance);
        gatherArguments(instance);
        generateJRE(instance);

//        // Download Resources
//        String assetsFolder = PackerFile.combineFilePaths(parentFolder, "assets");
//        PackerFile.createFolderIfNotExist(assetsFolder);
//
//        String assetsID = versionFileData.get("assets").toString();
//
//        JSONObject assetIndex = (JSONObject) versionFileData.get("assetIndex");
//        if(assetsID.equals(assetIndex.get("id").toString())) {
//            String assetsJSONPath = PackerFile.combineFilePaths(assetsFolder, assetsID + ".json");
//
//            String assetsJSONUrl = assetIndex.get("url").toString();
//            MinePackerRuntime.s_Instance.getModApi().downloadFromURL(assetsJSONUrl, assetsJSONPath);
//
//            PackerFile assetsJSONFile = new PackerFile(assetsJSONPath, false);
//            JSONObject assetsJSON = assetsJSONFile.readIntoJson();
//
//            JSONObject objectsJSON = (JSONObject) assetsJSON.get("objects");
//            int objectsCount = objectsJSON.size();
//            int objectsDownloadedCount = 0;
//            for(int i = 0; i < objectsCount; i++) {
//                String key = objectsJSON.keySet().toArray()[i].toString();
//
//                String absolutePath = PackerFile.combineFilePaths(assetsFolder, key);
//                String folderToCreate = absolutePath.substring(0, absolutePath.lastIndexOf('/'));
//                PackerFile.createFolderIfNotExist(folderToCreate);
//
//                String hash = ((JSONObject) objectsJSON.get(key)).get("hash").toString();
//                String url = MINECRAFT_RESOURCES_URL + "/" + hash.substring(0, 2) + "/" + hash;
//
//                // NOTE: This is commented out for testing as downloading all ~4000 assets on a single thread takes a LONG time
//                //MinePackerRuntime.s_Instance.getModApi().downloadFromURL(url, absolutePath);
//
//                objectsDownloadedCount += 1;
//                //Logger.info("MinecraftGenerator", "Downloaded " + objectsDownloadedCount + " / " + objectsCount + " assets");
//            }
//        }
//
//
//        // Download JRE Files
//        PackerFile jreManifestFile = new PackerFile(jreManifestPath, false);
//        JSONObject jreManifestData = (JSONObject) jreManifestFile.readIntoJson().get("files");
//
//        // NOTE: Splitting up the creation of directories and files ensures that all directories are created before downloading any files
//        List<String> directories = new ArrayList<>();
//        List<JSONObject> files = new ArrayList<>();
//        for(int i = 0; i < jreManifestData.size(); i++) {
//            String key = jreManifestData.keySet().toArray()[i].toString();
//            JSONObject data = (JSONObject) jreManifestData.get(key);
//
//            if(data.get("type").toString().equals("directory")) {
//                directories.add(key);
//            } else {
//                data.put("name", key);
//                files.add(data);
//            }
//        }
//
////        for(String dir : directories) {
////            String absDir = PackerFile.combineFilePaths(versionFolder, dir);
////            PackerFile.createFolderIfNotExist(absDir);
////        }
////
////        for(JSONObject file : files) {
////            JSONObject downloads = (JSONObject) file.get("downloads");
////            String downloadURL = ((JSONObject) downloads.get("raw")).get("url").toString();
////            String finalFilePath = PackerFile.combineFilePaths(versionFolder, file.get("name").toString());
////
////            MinePackerRuntime.s_Instance.getModApi().downloadFromURL(downloadURL, finalFilePath);
////        }
//
//        jreManifestFile.cleanup();

        Logger.info("MinecraftGenerator", "Generated Minecraft Instance for project '" + prj.getName() + "'");
    }

    private void generateVersion(MinecraftInstance instance) {
        String[] returnValues = downloadCorrectMinecraftVersion(instance.FileSystem.getBaseDirectory(), instance.InstanceProject.getMinecraftVersion());

        if(returnValues.length == 0) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance: Unrecognised Minecraft Version");
            return;
        }

        Logger.message("MinecraftGenerator", "Generated correct version file for " + instance.InstanceProject.getMinecraftVersion().toString());

        String versionFilePath = returnValues[0];
        String versionFolder = returnValues[1];

        instance.FileSystem.setVersionDirectory(versionFolder);
        instance.FileSystem.setVersionJSONFilePath(versionFilePath);
    }

    private void gatherArguments(MinecraftInstance instance) {
        instance.Arguments = new ListPair<>();

        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);

        List<String> gameArgs = new ArrayList<>();
        JSONArray jsonGameArgs = (JSONArray) ((JSONObject) versionFileData.get("arguments")).get("game");

        for(Object argObj : jsonGameArgs) {
            if(argObj.getClass().equals(String.class)) {
                gameArgs.add(argObj.toString());
            }

            // NOTE: The rest of the objects in jsonGameArgs are JSONObjects with specific rules attached. However, we only really
            //       care about one of these rules, custom window dimensions, and that is consistent across all versions. Therefore,
            //       it is okay to just hardcode that rule in the future and ignore the other rules in the version JSON
        }

        List<String> jvmArgs = new ArrayList<>();
        JSONArray jsonJVMArgs = (JSONArray) ((JSONObject) versionFileData.get("arguments")).get("jvm");

        for(Object argObj : jsonJVMArgs) {
            if(argObj.getClass().equals(String.class)) {
                jvmArgs.add(argObj.toString());
            } else {
                JSONObject arg = (JSONObject) argObj;
                JSONArray rules = (JSONArray) arg.get("rules");

                boolean allow = false;
                for(Object ruleObj : rules) {
                    JSONObject rule = (JSONObject) ruleObj;
                    boolean allowBase = rule.get("action").toString().equals("allow");

                    // NOTE: All of the rules for the JVM are based on the OS
                    JSONObject os = (JSONObject) rule.get("os");
                    if(os.containsKey("name")) {
                        String osName = os.get("name").toString();
                        if(MinePackerRuntime.s_Instance.getOS().toString().equals(osName) == allowBase) {
                            allow = true;
                        } else if((MinePackerRuntime.s_Instance.getOS().equals(MinePackerRuntime.OS.Mac) && osName.equals("osx")) == allowBase) {
                            allow = true;
                        }
                    } else if(os.containsKey("arch")) {
                        String arch = os.get("arch").toString();
                        if(MinePackerRuntime.s_Instance.getSystemArch().toString().equals(arch) == allowBase) {
                            allow = true;
                        }
                    }
                }

                if(allow) {
                    jvmArgs.add(arg.get("value").toString());
                }
            }
        }

        instance.Arguments.setFirst(gameArgs);
        instance.Arguments.setSecond(jvmArgs);

        Logger.message("MinecraftGenerator", "Gathered correct arguments");
    }

    private void generateJRE(MinecraftInstance instance) {
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);

        String jreJavaVersionComponent = ((JSONObject) versionFileData.get("javaVersion")).get("component").toString();
        int jreJavaVersionMajor = Integer.parseInt(((JSONObject) versionFileData.get("javaVersion")).get("majorVersion").toString());

        JSONObject jreData = getCorrectJREDataForSystem(MinePackerRuntime.s_Instance.getOS(), MinePackerRuntime.s_Instance.getSystemArch());
        if(jreData == null) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance: Architecture not supported");
            return;
        }

        JSONObject jreComponent = (JSONObject) ((JSONArray) jreData.get(jreJavaVersionComponent)).getFirst();
        String jreManifestPath = downloadJREVersionManifest(instance.FileSystem.getVersionDirectory(), jreComponent);

        if(jreManifestPath.isEmpty()) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance: Failed to find JRE Manifest");
            return;
        }

        instance.FileSystem.setJREJSONFilePath(jreManifestPath);
        Logger.message("MinecraftGenerator", "Generated correct JRE manifest file for " + instance.InstanceProject.getMinecraftVersion().toString());
    }

    private String[] downloadCorrectMinecraftVersion(String parentFolder, MinecraftVersion version) {
        String versionsFolder = PackerFile.combineFilePaths(parentFolder, "versions");
        PackerFile.createFolderIfNotExist(versionsFolder);
        String versionFolder = PackerFile.combineFilePaths(versionsFolder, version.toString());
        PackerFile.createFolderIfNotExist(versionFolder);
        String outputFile = PackerFile.combineFilePaths(versionFolder, version + ".json");

        String jsonPath = getJsonPathForVersion(version);

        if(jsonPath.isEmpty()) {
            return new String[0];
        }

        PackerFile.deleteFileIfExists(outputFile);
        long bytesWritten = MinePackerRuntime.s_Instance.getModApi().downloadFromURL(jsonPath, outputFile);

        if(bytesWritten == 0) {
            return new String[0];
        }

        String[] returnValues = new String[2];
        returnValues[0] = outputFile;
        returnValues[1] = versionFolder;
        return returnValues;
    }

    private String downloadJREVersionManifest(String parentFolder, JSONObject jreComponent) {
        String outputFile = PackerFile.combineFilePaths(parentFolder, "jre_manifest.json");
        String url = ((JSONObject) jreComponent.get("manifest")).get("url").toString();

        if(url.isEmpty()) {
            return "";
        }

        PackerFile.deleteFileIfExists(outputFile);
        long bytesWritten = MinePackerRuntime.s_Instance.getModApi().downloadFromURL(url, outputFile);

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
