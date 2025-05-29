package dabusmc.minepacker.backend.authorisation;

import dabusmc.minepacker.backend.authorisation.microsoft.MicrosoftAccount;
import dabusmc.minepacker.backend.authorisation.microsoft.OAuthTokenResponse;
import dabusmc.minepacker.backend.authorisation.microsoft.XBLAuthResponse;
import dabusmc.minepacker.backend.io.Browser;
import dabusmc.minepacker.backend.logging.Logger;
import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AuthManager {

    private AbstractAccount m_WorkingAccount;

    private final HTTPServer m_Server = new HTTPServer(MicrosoftAccount.MICROSOFT_LOGIN_REDIRECT_PORT);
    private final HTTPServer.VirtualHost m_Host = m_Server.getVirtualHost(null);

    public AuthManager() {
        m_WorkingAccount = null;
    }

    public void attemptMicrosoftLogin() {
        try {
            startServer();
            Browser.open(MicrosoftAccount.MICROSOFT_LOGIN_URL);
        } catch (IOException e) {
            Logger.error("AuthManager", e.toString());
        }
    }

    public AbstractAccount getWorkingAccount() {
        return m_WorkingAccount;
    }

    private void startServer() throws IOException {
        m_Host.addContext("/", (req, res) -> {
            if (req.getParams().containsKey("error")) {
                res.getHeaders().add("Content-Type", "text/plain");
                res.send(500, "Error logging in.");
                Logger.error("AuthManager", "Error logging into Microsoft account: " + URLDecoder
                        .decode(req.getParams().get("error_description"), StandardCharsets.UTF_8));
                endAuthServer();
                return 0;
            }

            if (!req.getParams().containsKey("code")) {
                res.getHeaders().add("Content-Type", "text/plain");
                res.send(400, "Code is missing");
                endAuthServer();
                return 0;
            }

            String loginMethod = "Browser";
            //Logger.info("AuthManager", req.getParams().get("code"));
            try {
                m_WorkingAccount = new MicrosoftAccount();
                m_WorkingAccount.LoginMethod = loginMethod;
                acquireAccessToken(req.getParams().get("code"));
            } catch (Exception e) {
                Logger.fatal("AuthManager", e.toString());

                res.getHeaders().add("Content-Type", "text/html");
                res.send(500, "Error logging in.");
                endAuthServer();
                return 0;
            }

            res.getHeaders().add("Content-Type", "text/plain");
            res.send(200, "Login complete. You can now close this window and go back to MinePacker");
            endAuthServer();

            return 0;
        });

        m_Server.start();
    }

    // FIXME: The server isn't properly stopping and the application continues to run even when this function has been called and the GUI has been closed
    private void endAuthServer() {
        m_Server.stop();
    }

    private void acquireAccessToken(String code) throws Exception {
        OAuthTokenResponse response = MicrosoftAccount.getAccessTokenFromCode(code);
        m_WorkingAccount.AccessToken = response;
        acquireXBLToken(response);
    }

    private void acquireXBLToken(OAuthTokenResponse tokenResponse) throws Exception {
        XBLAuthResponse response = MicrosoftAccount.getXBLToken(tokenResponse.AccessToken);
        if(response != null) {
            Logger.info("AuthManager", response.Token);
        } else {
            Logger.error("AuthManager", "Failed to generate Xbox Live Token");
        }
    }

}
