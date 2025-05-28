package dabusmc.minepacker.backend.data;

import dabusmc.minepacker.backend.io.PackerFile;
import dabusmc.minepacker.backend.io.serialization.ISaveable;
import org.json.simple.JSONObject;

public class Settings implements ISaveable {

    private static final String PROJECTS_DIR_DEFAULT = "/projects";

    private String m_ProjectsDir = "";

    public String getProjectsDir() {
        if(m_ProjectsDir.isEmpty()) {
            return PackerFile.combineFilePaths(PackerFile.getCWD(), PROJECTS_DIR_DEFAULT);
        }

        return m_ProjectsDir;
    }

    public void setProjectsDir(String dir) {
        m_ProjectsDir = dir;
    }

    @Override
    public String getFileName() {
        return "settings.json";
    }

    @Override
    public String getSaveDirectory() {
        return PackerFile.combineFilePaths(PackerFile.getCWD(), "runtime");
    }

    @Override
    public JSONObject getSavableObject() {
        JSONObject data = new JSONObject();

        data.put("projects_dir", m_ProjectsDir);

        return data;
    }

    @Override
    public void getLoadedData(JSONObject data) {

    }
}
