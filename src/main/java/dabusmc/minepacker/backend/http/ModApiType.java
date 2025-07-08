package dabusmc.minepacker.backend.http;

public enum ModApiType {
    Modrinth("modrinth");

    private final String m_TypeText;

    ModApiType(String typeText) {
        m_TypeText = typeText;
    }

    @Override
    public String toString() {
        return m_TypeText;
    }
}
