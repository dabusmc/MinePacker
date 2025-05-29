package dabusmc.minepacker.backend.authorisation;

import dabusmc.minepacker.backend.analytics.Analytics;
import dabusmc.minepacker.backend.authorisation.microsoft.*;
import dabusmc.minepacker.backend.io.Browser;
import dabusmc.minepacker.backend.logging.Logger;
import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AuthManager {

    private final HTTPServer m_Server = new HTTPServer(MicrosoftAccount.MICROSOFT_LOGIN_REDIRECT_PORT);
    private final HTTPServer.VirtualHost m_Host = m_Server.getVirtualHost(null);

    private String m_LoginMethod = "Unknown";

    private List<AbstractAccount> m_Accounts;
    private int m_CurrentSelectedAccount;

    public AuthManager() {
        m_Accounts = new ArrayList<>();
        m_CurrentSelectedAccount = -1;
    }

    public AbstractAccount getCurrentAccount() {
        if(m_CurrentSelectedAccount == -1) {
            Logger.error("AuthManager", "No accounts are currently logged in!");
            return null;
        }

        return m_Accounts.get(m_CurrentSelectedAccount);
    }

    public void attemptMicrosoftLogin() {
        try {
            startServer();
            Browser.open(MicrosoftAccount.MICROSOFT_LOGIN_URL);
        } catch (IOException e) {
            Logger.error("AuthManager", e.toString());
        }
    }

    private void startServer() throws IOException {
        m_Host.addContext("/", (req, res) -> {
            Analytics.begin("LoginWithMicrosoft");

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

            try {
                m_LoginMethod = "Browser";
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
    public void endAuthServer() {
        m_Server.stop();
    }

    private void acquireAccessToken(String code) throws Exception {
        OAuthTokenResponse response = MicrosoftAccount.getAccessTokenFromCode(code);
        acquireXBLToken(response);
    }

    private void acquireXBLToken(OAuthTokenResponse tokenResponse) throws Exception {
        XBLAuthResponse response = MicrosoftAccount.getXBLToken(tokenResponse.AccessToken);
        acquireXsts(tokenResponse, response.Token);
    }

    private void acquireXsts(OAuthTokenResponse tokenResponse, String xblToken) throws Exception {
        XBLAuthResponse xstsAuthResponse = MicrosoftAccount.getXstsToken(xblToken);
        acquireMinecraftToken(tokenResponse, xstsAuthResponse);
    }

    private void acquireMinecraftToken(OAuthTokenResponse tokenResponse, XBLAuthResponse xstsAuthResponse) throws Exception {
        String xblUhs = xstsAuthResponse.DisplayClaims.XUI.getFirst().UHS;
        String xblXsts = xstsAuthResponse.Token;

        LoginResponse loginResponse = MicrosoftAccount.loginToMinecraft("XBL3.0 x=" + xblUhs + ";" + xblXsts);
        if(loginResponse == null) {
            throw new Exception("Failed to login to Minecraft");
        }

        Entitlements entitlements = MicrosoftAccount.getEntitlements(loginResponse.AccessToken);

        if (!(entitlements.Items.stream().anyMatch(i -> i.Name.equalsIgnoreCase("product_minecraft"))
                && entitlements.Items.stream().anyMatch(i -> i.Name.equalsIgnoreCase("game_minecraft")))) {
            throw new Exception("Account does not own Minecraft");
        }

        Profile profile = MicrosoftAccount.getMCProfile(loginResponse.AccessToken);
        if (profile == null) {
            throw new Exception("Failed to get Minecraft profile");
        }

        addMicrosoftAccount(tokenResponse, xstsAuthResponse, loginResponse, profile);
        Logger.info("AuthManager", "Added Minecraft Account with name: " + profile.Name);
    }

    private void addMicrosoftAccount(OAuthTokenResponse tokenResponse, XBLAuthResponse xstsAuthResponse, LoginResponse loginResponse, Profile profile) {
        if(m_Accounts.isEmpty()) {
            m_CurrentSelectedAccount = 0;
        }

        MicrosoftAccount account = new MicrosoftAccount();
        account.AccessToken = tokenResponse;
        account.XstsToken = xstsAuthResponse;
        account.AttemptedLoginResponse = loginResponse;
        account.AccountProfile = profile;
        m_Accounts.add(account);

        Analytics.end("LoginWithMicrosoft");
    }

}
