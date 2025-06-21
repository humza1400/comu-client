package me.comu.account.auth;

public class MicrosoftLoginResult {
    public final String accessToken;
    public final String refreshToken;

    public MicrosoftLoginResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
