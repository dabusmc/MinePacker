package dabusmc.minepacker.backend.data;

import dabusmc.minepacker.backend.http.ModApiType;

public class Mod {

    private String m_ID;
    private String m_Slug;
    private String m_Title;
    private String m_Tagline;
    private String m_Description;
    private String m_IconURL;
    private ModApiType m_Provider;

    public Mod() {

    }

    public String getID() {
        return m_ID;
    }

    public String getSlug() {
        return m_Slug;
    }

    public String getTitle() {
        return m_Title;
    }

    public String getTagline() {
        return m_Tagline;
    }

    public String getDescription() {
        return m_Description;
    }

    public String getIconURL() {
        return m_IconURL;
    }

    public ModApiType getProvider() { return m_Provider; }

    public void setID(String id) {
        m_ID = id;
    }

    public void setSlug(String slug) {
        m_Slug = slug;
    }

    public void setTitle(String title) {
        m_Title = title;
    }

    public void setTagline(String tagline) {
        m_Tagline = tagline;
    }

    public void setDescription(String description) {
        m_Description = description;
    }

    public void setIconURL(String icon) {
        m_IconURL = icon;
    }

    public void setProvider(ModApiType type) {
        m_Provider = type;
    }

    @Override
    public String toString() {
        String out = m_Title;
        out += " (";
        out += m_ID;
        out += " | ";
        out += m_Slug;
        out += ")\n\t\t\t\t\t";
        out += m_Tagline;
        return out;
    }

    public enum Loader {
        Fabric,
        Forge,
        Quilt,
        NeoForge,
        Vanilla,
        Unknown
    }

    public static Loader stringToLoader(String string) {
        if(string.equalsIgnoreCase("fabric")) {
            return Loader.Fabric;
        } else if(string.equalsIgnoreCase("forge")) {
            return Loader.Forge;
        } else if(string.equalsIgnoreCase("quilt")) {
            return Loader.Quilt;
        } else if(string.equalsIgnoreCase("neoforge")) {
            return Loader.NeoForge;
        } else if(string.equalsIgnoreCase("vanilla")) {
            return Loader.Vanilla;
        }

        return Loader.Unknown;
    }

    public static String loaderToString(Loader loader) {
        switch (loader) {
            case Fabric -> {
                return "Fabric";
            }
            case Forge -> {
                return "Forge";
            }
            case Quilt -> {
                return "Quilt";
            }
            case NeoForge -> {
                return "NeoForge";
            }
            case Vanilla -> {
                return "Vanilla";
            }
            default -> {
                return "Unknown";
            }
        }
    }

}
