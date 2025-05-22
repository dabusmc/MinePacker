package dabusmc.minepacker.backend.io;

import dabusmc.minepacker.backend.logging.Logger;

import java.io.*;
import java.net.URL;

public class PackerFile {

    public static URL getResource(String path) {
        return PackerFile.class.getResource(path);
    }

    private String m_Path;
    private File m_File;
    private BufferedWriter m_Writer;

    public PackerFile(URL url) {
        m_Path = url.getFile();
        open();
    }

    public PackerFile(String path) {
        m_Path = path.trim();
        open();
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

    private void open() {
        m_File = new File(m_Path);

        if (fileExists()) {
            try {
                m_Writer = new BufferedWriter(new FileWriter(m_File, true));
            } catch (IOException e) {
                Logger.fatal("PackerFile", e.toString());
            }
        }
    }

}
