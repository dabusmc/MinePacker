package dabusmc.minepacker.backend.io;

import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.Logger;

public class MCFileSystem {

    private String m_BaseDirectory;
    private String m_VersionsDirectory;
    private String m_VersionDirectory;
    private String m_LibrariesDirectory;
    private String m_BinDirectory;

    private String m_AssetsDirectory;
    private String m_IndexesDirectory;
    private String m_LogConfigsDirectory;
    private String m_ObjectsDirectory;

    private String m_RuntimesDirectory;
    private String m_MinecraftRuntimesDirectory;

    private String m_VersionJSONFilePath;
    private String m_ClientJarFilePath;

    public void setProject(Project prj) {
        m_BaseDirectory = PackerFile.combineFilePaths(prj.getSaveDirectory(), "instance");
        m_VersionsDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "versions");
        m_LibrariesDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "libraries");
        m_BinDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "bin");

        m_AssetsDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "assets");
        m_IndexesDirectory = PackerFile.combineFilePaths(m_AssetsDirectory, "indexes");
        m_LogConfigsDirectory = PackerFile.combineFilePaths(m_AssetsDirectory, "log_configs");
        m_ObjectsDirectory = PackerFile.combineFilePaths(m_AssetsDirectory, "objects");

        m_RuntimesDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "runtimes");
        m_MinecraftRuntimesDirectory = PackerFile.combineFilePaths(m_RuntimesDirectory, "minecraft");
    }

    public void generateFileStructure() {
        PackerFile.createFolderIfNotExist(m_BaseDirectory);
        PackerFile.createFolderIfNotExist(m_VersionsDirectory);
        PackerFile.createFolderIfNotExist(m_LibrariesDirectory);
        PackerFile.createFolderIfNotExist(m_BinDirectory);

        PackerFile.createFolderIfNotExist(m_AssetsDirectory);
        PackerFile.createFolderIfNotExist(m_IndexesDirectory);
        PackerFile.createFolderIfNotExist(m_LogConfigsDirectory);
        PackerFile.createFolderIfNotExist(m_ObjectsDirectory);

        PackerFile.createFolderIfNotExist(m_RuntimesDirectory);
        PackerFile.createFolderIfNotExist(m_MinecraftRuntimesDirectory);
    }

    public String getBaseDirectory() {
        return m_BaseDirectory;
    }

    public String getVersionsDirectory() {
        return m_VersionsDirectory;
    }

    public String getVersionDirectory() {
        return m_VersionDirectory;
    }

    public void setVersionDirectory(String versionDir) {
        m_VersionDirectory = versionDir;
    }

    public String getLibrariesDirectory() {
        return m_LibrariesDirectory;
    }

    public String getBinDirectory() {
        return m_BinDirectory;
    }

    public String getAssetsDirectory() {
        return m_AssetsDirectory;
    }

    public String getIndexesDirectory() {
        return m_IndexesDirectory;
    }

    public String getLogConfigsDirectory() {
        return m_LogConfigsDirectory;
    }

    public String getObjectsDirectory() {
        return m_ObjectsDirectory;
    }

    public String getRuntimesDirectory() {
        return m_RuntimesDirectory;
    }

    public String getMinecraftRuntimesDirectory() {
        return m_MinecraftRuntimesDirectory;
    }



    public String getVersionJSONFilePath() {
        return m_VersionJSONFilePath;
    }

    public void setVersionJSONFilePath(String filePath) {
        m_VersionJSONFilePath = filePath;
    }

    public String getClientJarFilePath() {
        return m_ClientJarFilePath;
    }

    public void setClientJarFilePath(String filePath) {
        m_ClientJarFilePath = filePath;
    }

}
