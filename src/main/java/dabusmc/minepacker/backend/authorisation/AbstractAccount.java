package dabusmc.minepacker.backend.authorisation;

import dabusmc.minepacker.backend.authorisation.microsoft.OAuthTokenResponse;
import dabusmc.minepacker.backend.authorisation.microsoft.Profile;

public class AbstractAccount {

    public OAuthTokenResponse AccessToken;
    public Profile AccountProfile;
    public String LoginMethod;
    public AccountType Type;

    public AbstractAccount(AccountType type) {
        Type = type;
    }

    public enum AccountType {
        Abstract,
        Microsoft
    }

}
