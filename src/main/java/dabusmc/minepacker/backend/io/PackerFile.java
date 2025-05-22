package dabusmc.minepacker.backend.io;

import dabusmc.minepacker.backend.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackerFile {

    public static String convertNameToFileName(String name) {
        String fn = name;
        fn = fn.replaceAll(" ", "_");
        fn = fn.toLowerCase();
        return fn;
    }

    public static URL getResource(String path) {
        return PackerFile.class.getResource(path);
    }

    public static String getCWD() {
        return System.getProperty("user.dir");
    }

    public static String combineFilePaths(String path1, String path2) {
        return path1 + "/" + path2;
    }

    public static void createFolderIfNotExist(String dir) {
        Path path = Path.of(dir);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                Logger.error("PackerFile", e.toString());
            }
        }
    }

    public static void deleteFileIfExists(String file) {
        Path path = Path.of(file);
        if(Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                Logger.error("PackerFile", e.toString());
            }
        }
    }

    private String m_Path;
    private File m_File;
    private BufferedWriter m_Writer;

    public PackerFile(URL url, boolean overwrite) {
        m_Path = url.getFile();
        open(overwrite);
    }

    public PackerFile(String path, boolean overwrite) {
        m_Path = path.trim();
        open(overwrite);
    }

    public boolean fileExists() {
        return m_File.isFile();
    }

    public void construct() {
        try {
            m_File.createNewFile();
            m_Writer = new BufferedWriter(new FileWriter(m_File));
        } catch (IOException e) {
            Logger.fatal("PackerFile", e.toString());
        }
    }

    public FileReader getReader() {
        try {
            return new FileReader(m_Path);
        } catch (FileNotFoundException e) {
            Logger.fatal("PackerFile", e.toString());
            return null;
        }
    }

    public BufferedWriter getWriter() {
        return m_Writer;
    }

    public String getPath() {
        return m_Path;
    }

    public void cleanup() {
        try {
            m_Writer.close();
        } catch (IOException e) {
            Logger.fatal("PackerFile", e.toString());
        }
    }

    public JSONObject readIntoJson() {
        JSONParser parser = new JSONParser();

        try {
            return (JSONObject) parser.parse(getReader());
        } catch (IOException | ParseException e) {
            Logger.error("PackerFile", e.toString());
        }

        return null;
    }

    private void open(boolean overwrite) {
        m_File = new File(m_Path);

        if (fileExists()) {
            try {
                m_Writer = new BufferedWriter(new FileWriter(m_File, !overwrite));
            } catch (IOException e) {
                Logger.fatal("PackerFile", e.toString());
            }
        }
    }

}
