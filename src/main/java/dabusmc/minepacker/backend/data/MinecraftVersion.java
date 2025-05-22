package dabusmc.minepacker.backend.data;

public enum MinecraftVersion {
    MC_1_21_5("1.21.5");

    private final String m_VersionText;

    MinecraftVersion(String versionText) {
        m_VersionText = versionText;
    }

    @Override
    public String toString() {
        return m_VersionText;
    }
}
