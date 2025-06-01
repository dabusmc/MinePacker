package dabusmc.minepacker.backend.authorisation;

import dabusmc.minepacker.backend.authorisation.microsoft.OAuthTokenResponse;
import dabusmc.minepacker.backend.authorisation.microsoft.Profile;

import java.util.UUID;

public abstract class AbstractAccount {

    public OAuthTokenResponse AccessToken;
    public Profile AccountProfile;
    public String LoginMethod;
    public AccountType Type;
    public boolean LoggedIn;

    public AbstractAccount(AccountType type) {
        Type = type;
    }

    private String dashedUUID() {
        return this.AccountProfile.ID
                .replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5");
    }

    public UUID getRealUUID() {
        return (this.AccountProfile.ID == null ? UUID.randomUUID() : UUID.fromString(dashedUUID()));
    }

    public abstract String getUserType();

    public enum AccountType {
        Abstract,
        Microsoft
    }

}
