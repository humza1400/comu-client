package me.comu.account;

public class MicrosoftLoginResult {
    public final String accessToken;
    public final String refreshToken;

    public MicrosoftLoginResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
