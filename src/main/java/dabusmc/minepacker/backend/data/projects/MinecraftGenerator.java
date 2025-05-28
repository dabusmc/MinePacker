package dabusmc.minepacker.backend.data.projects;

import dabusmc.minepacker.MinePackerApp;
import dabusmc.minepacker.backend.MinePackerRuntime;
import dabusmc.minepacker.backend.data.MinecraftVersion;
import dabusmc.minepacker.backend.io.MCFileSystem;
import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.logging.Logger;
import dabusmc.minepacker.backend.mod_api.ModApi;
import dabusmc.minepacker.backend.util.ListPair;
import dabusmc.minepacker.backend.util.StringUtils;
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
        Logger.info("MinecraftGenerator", "Generating Minecraft instance...");

        generateVersion(instance);

        gatherArguments(instance);

        determineMainClass(instance);

        generateMinecraft(instance);

        generateAssets(instance);

        generateLogging(instance);

        generateLibraries(instance);

        generateRuntimes(instance);

        // Finish
        Logger.info("MinecraftGenerator", "Generated Minecraft Instance for project '" + prj.getName() + "'");
    }

    // NOTE: Generation Functions

    private void generateVersion(MinecraftInstance instance) {
        String[] returnValues = downloadCorrectMinecraftVersion(instance.FileSystem.getBaseDirectory(), instance.InstanceProject.getMinecraftVersion());

        if(returnValues.length == 0) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance for " + instance.InstanceProject.getName() + ": Unrecognised Minecraft Version");
            return;
        }

        Logger.message("MinecraftGenerator", "Generated correct version file for " + instance.InstanceProject.getName());

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

                boolean allow = true;
                for(Object ruleObj : rules) {
                    JSONObject rule = (JSONObject) ruleObj;
                    boolean allowBase = rule.get("action").toString().equals("allow");

                    // NOTE: All of the rules for the JVM are based on the OS
                    JSONObject os = (JSONObject) rule.get("os");
                    if(os.containsKey("name")) {
                        String osName = os.get("name").toString();
                        if(MinePackerRuntime.s_Instance.getOS().toString().equals(osName) != allowBase) {
                            allow = false;
                        } else if((MinePackerRuntime.s_Instance.getOS().equals(MinePackerRuntime.OS.Mac) && osName.equals("osx")) != allowBase) {
                            allow = false;
                        }
                    } else if(os.containsKey("arch")) {
                        String arch = os.get("arch").toString();
                        if(MinePackerRuntime.s_Instance.getSystemArch().toString().equals(arch) != allowBase) {
                            allow = false;
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

        Logger.message("MinecraftGenerator", "Gathered correct arguments for " + instance.InstanceProject.getName());
    }

    private void generateAssets(MinecraftInstance instance) {
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);
        String assetsID = versionFileData.get("assets").toString();

        // Download asset index
        JSONObject assetIndex = (JSONObject) versionFileData.get("assetIndex");
        if(assetsID.equals(assetIndex.get("id").toString())) {
            String assetsJSONPath = PackerFile.combineFilePaths(instance.FileSystem.getIndexesDirectory(), assetsID + ".json");

            String assetsJSONUrl = assetIndex.get("url").toString();
            MinePackerRuntime.s_Instance.getModApi().downloadFromURL(assetsJSONUrl, assetsJSONPath);

            // Download asset objects
            JSONObject assetObjects = (JSONObject) new PackerFile(assetsJSONPath, false).readIntoJson(true).get("objects");

            int objectsCount = assetObjects.size();
            int objectsDownloadedCount = 0;

            for(int i = 0; i < objectsCount; i++) {
                String key = assetObjects.keySet().toArray()[i].toString();
                String hash = ((JSONObject) assetObjects.get(key)).get("hash").toString();

                String absolutePath = PackerFile.combineFilePaths(instance.FileSystem.getObjectsDirectory(), PackerFile.combineFilePaths(hash.substring(0, 2), hash));
                String folderToCreate = absolutePath.substring(0, absolutePath.lastIndexOf('/'));
                PackerFile.createFolderIfNotExist(folderToCreate);

                String url = MINECRAFT_RESOURCES_URL + "/" + hash.substring(0, 2) + "/" + hash;

                // NOTE: This is commented out for testing as downloading all ~4000 objects on a single thread takes a LONG time
                MinePackerRuntime.s_Instance.getModApi().downloadFromURL(url, absolutePath);

                objectsDownloadedCount += 1;
                Logger.message("MinecraftGenerator", "Downloaded " + objectsDownloadedCount + " / " + objectsCount + " asset objects");
            }
        }

        Logger.message("MinecraftGenerator", "Generated asset files for " + instance.InstanceProject.getName());
    }

    private void generateLogging(MinecraftInstance instance) {
        JSONObject loggingJSON = (JSONObject) new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true).get("logging");
        JSONObject clientLoggingJSON = (JSONObject) loggingJSON.get("client");
        JSONObject clientLoggingFileJSON = (JSONObject) clientLoggingJSON.get("file");

        String fileName = clientLoggingFileJSON.get("id").toString();
        String filePath = PackerFile.combineFilePaths(instance.FileSystem.getLogConfigsDirectory(), fileName);
        String url = clientLoggingFileJSON.get("url").toString();

        String jvmArg = StringUtils.setVariableInString(clientLoggingJSON.get("argument").toString(), "path", "\"" + filePath + "\"");
        instance.Arguments.addToSecond(jvmArg);

        MinePackerRuntime.s_Instance.getModApi().downloadFromURL(url, filePath);

        Logger.message("MinecraftGenerator", "Generated logging file for " + instance.InstanceProject.getName());
    }

    private void generateLibraries(MinecraftInstance instance) {
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);

        JSONArray libsJSON = (JSONArray) versionFileData.get("libraries");

        int libraryCount = libsJSON.size();
        int librariesDownloaded = 0;

        for(Object libObj : libsJSON) {
            JSONObject libJSON = (JSONObject) libObj;
            JSONObject libDownloadJSON = (JSONObject) ((JSONObject)libJSON.get("downloads")).get("artifact");

            String outputFile = PackerFile.combineFilePaths(instance.FileSystem.getLibrariesDirectory(), libDownloadJSON.get("path").toString());
            String outputDir = PackerFile.getDirectoryFromAbsolutePath(outputFile);
            String fileURL = libDownloadJSON.get("url").toString();

            if(libJSON.containsKey("rules")) {
                JSONArray rules = (JSONArray) libJSON.get("rules");
                boolean allow = true;

                for(Object ruleObj : rules) {
                    JSONObject ruleJSON = (JSONObject) ruleObj;
                    boolean allowBase = ruleJSON.get("action").toString().equals("allow");

                    // NOTE: All of the rules for the libraries are based on the OS
                    JSONObject os = (JSONObject) ruleJSON.get("os");
                    if(os.containsKey("name")) {
                        String osName = os.get("name").toString();

                        if(osName.equals("osx")) {
                            if(!MinePackerRuntime.s_Instance.getOS().equals(MinePackerRuntime.OS.Mac)) {
                                allow = false;
                            }
                        } else {
                            if (MinePackerRuntime.s_Instance.getOS().toString().equals(osName) != allowBase) {
                                allow = false;
                            }
                        }
                    }
                }

                if(allow) {
                    PackerFile.createFolderIfNotExist(outputDir);
                    MinePackerRuntime.s_Instance.getModApi().downloadFromURL(fileURL, outputFile);

                    librariesDownloaded += 1;
                    Logger.message("MinecraftGenerator", "Downloaded " + librariesDownloaded + " / " + libraryCount + " libraries");
                }
            } else {
                PackerFile.createFolderIfNotExist(outputDir);
                MinePackerRuntime.s_Instance.getModApi().downloadFromURL(fileURL, outputFile);

                librariesDownloaded += 1;
                Logger.message("MinecraftGenerator", "Downloaded " + librariesDownloaded + " / " + libraryCount + " libraries");
            }
        }

        Logger.message("MinecraftGenerator", "Generated library files for " + instance.InstanceProject.getName());
    }

    private void determineMainClass(MinecraftInstance instance) {
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);
        instance.MainClass = versionFileData.get("mainClass").toString();
        Logger.message("MinecraftGenerator", "Determined main class for " + instance.InstanceProject.getName());
    }

    private void generateMinecraft(MinecraftInstance instance) {
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);
        String clientJarRelPath = "net/minecraft/client/" + instance.InstanceProject.getMinecraftVersion().toString();
        String clientJarFilePath = PackerFile.combineFilePaths(instance.FileSystem.getLibrariesDirectory(), clientJarRelPath);
        PackerFile.createFolderIfNotExist(clientJarFilePath);
        clientJarFilePath += "/client.jar";

        String url = ((JSONObject) ((JSONObject) versionFileData.get("downloads")).get("client")).get("url").toString();
        MinePackerRuntime.s_Instance.getModApi().downloadFromURL(url, clientJarFilePath);

        instance.FileSystem.setClientJarFilePath(clientJarFilePath);

        Logger.message("MinecraftGenerator", "Generated Minecraft Jar for " + instance.InstanceProject.getName());
    }

    private void generateRuntimes(MinecraftInstance instance) {
        // Gather correct JRE Manifest file
        JSONObject versionFileData = new PackerFile(instance.FileSystem.getVersionJSONFilePath(), false).readIntoJson(true);

        String jreJavaVersionComponent = ((JSONObject) versionFileData.get("javaVersion")).get("component").toString();
        int jreJavaVersionMajor = Integer.parseInt(((JSONObject) versionFileData.get("javaVersion")).get("majorVersion").toString());

        JSONObject jreData = getCorrectJREDataForSystem(MinePackerRuntime.s_Instance.getOS(), MinePackerRuntime.s_Instance.getSystemArch());
        if(jreData == null) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance for " + instance.InstanceProject.getName() + ": Architecture not supported");
            return;
        }

        JSONObject jreComponent = (JSONObject) ((JSONArray) jreData.get(jreJavaVersionComponent)).getFirst();
        String jreManifestPath = downloadJREVersionManifest(instance.FileSystem.getVersionDirectory(), jreComponent);

        if(jreManifestPath.isEmpty()) {
            Logger.error("MinecraftGenerator", "Failed to generate Minecraft instance for " + instance.InstanceProject.getName() + ": Failed to find JRE Manifest");
            return;
        }

        // Install Runtimes
        JSONObject jreManifestData = (JSONObject) new PackerFile(jreManifestPath, false).readIntoJson(true).get("files");

        // NOTE: Splitting up the creation of directories and files ensures that all directories are created before downloading any files
        List<String> directories = new ArrayList<>();
        List<JSONObject> files = new ArrayList<>();
        for(int i = 0; i < jreManifestData.size(); i++) {
            String key = jreManifestData.keySet().toArray()[i].toString();
            JSONObject data = (JSONObject) jreManifestData.get(key);

            if(data.get("type").toString().equals("directory")) {
                directories.add(key);
            } else {
                data.put("name", key);
                files.add(data);
            }
        }

        for(String dir : directories) {
            String absDir = PackerFile.combineFilePaths(instance.FileSystem.getMinecraftRuntimesDirectory(), dir);
            PackerFile.createFolderIfNotExist(absDir);
        }

        for(JSONObject file : files) {
            JSONObject downloads = (JSONObject) file.get("downloads");
            String downloadURL = ((JSONObject) downloads.get("raw")).get("url").toString();
            String finalFilePath = PackerFile.combineFilePaths(instance.FileSystem.getMinecraftRuntimesDirectory(), file.get("name").toString());

            MinePackerRuntime.s_Instance.getModApi().downloadFromURL(downloadURL, finalFilePath);
        }

        Logger.message("MinecraftGenerator", "Generated Runtimes for " + instance.InstanceProject.getName());
    }

    // NOTE: Utility functions

    public boolean canRunMinecraft(MinePackerRuntime.OS os, MinePackerRuntime.SysArch arch) {
        if(os == MinePackerRuntime.OS.Unknown || arch == MinePackerRuntime.SysArch.Unknown)
            return false;

        if(os == MinePackerRuntime.OS.Windows) {
            return arch == MinePackerRuntime.SysArch.x64 || arch == MinePackerRuntime.SysArch.x86 || arch == MinePackerRuntime.SysArch.arm64;
        }

        // TODO: Make sure other operating systems are supported
        return false;
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
