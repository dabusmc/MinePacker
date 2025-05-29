package dabusmc.minepacker.backend.authorisation;

public class AuthManager {

    private AbstractAccount m_CurrentAccountLoggedIn;

    public AuthManager() {
        m_CurrentAccountLoggedIn = null;
    }

    public void attemptMicrosoftLogin() {

    }

    public AbstractAccount getCurrentLoggedInAccount() {
        return m_CurrentAccountLoggedIn;
    }

    private void startServer() {

    }

}
