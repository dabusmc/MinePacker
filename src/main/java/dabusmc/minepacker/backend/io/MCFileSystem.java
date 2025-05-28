package dabusmc.minepacker.backend.io;

import dabusmc.minepacker.backend.data.projects.Project;
import dabusmc.minepacker.backend.logging.Logger;

public class MCFileSystem {

    private String m_BaseDirectory;
    private String m_VersionsDirectory;
    private String m_VersionDirectory;
    private String m_AssetsDirectory;
    private String m_BinDirectory;
    private String m_JREDirectory;

    private String m_VersionJSONFilePath;
    private String m_JREJSONFilePath;

    public void setProject(Project prj) {
        m_BaseDirectory = PackerFile.combineFilePaths(prj.getSaveDirectory(), "instance");
        m_VersionsDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "versions");
        m_AssetsDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "assets");
        m_BinDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "bin");
        m_JREDirectory = PackerFile.combineFilePaths(m_BaseDirectory, "jre");
    }

    public void generateFileStructure() {
        PackerFile.createFolderIfNotExist(m_BaseDirectory);
        PackerFile.createFolderIfNotExist(m_VersionsDirectory);
        PackerFile.createFolderIfNotExist(m_AssetsDirectory);
        PackerFile.createFolderIfNotExist(m_BinDirectory);
        PackerFile.createFolderIfNotExist(m_JREDirectory);
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

    public String getAssetsDirectory() {
        return m_AssetsDirectory;
    }

    public String getBinDirectory() {
        return m_BinDirectory;
    }

    public String getJREDirectory() {
        return m_JREDirectory;
    }



    public String getVersionJSONFilePath() {
        return m_VersionJSONFilePath;
    }

    public void setVersionJSONFilePath(String filePath) {
        m_VersionJSONFilePath = filePath;
    }

    public String getJREJSONFilePath() {
        return m_JREJSONFilePath;
    }

    public void setJREJSONFilePath(String filePath) {
        m_JREJSONFilePath = filePath;
    }

}
