package me.comu.account;

public class Account {

    private String refreshToken;
    private final String uuid;
    private final String username;
    private final String email;
    private long lastUsed;


    public Account(String username, String uuid, String refreshToken, String email, long lastUsed) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.uuid = uuid;
        this.email = email;
        this.lastUsed = lastUsed;
    }

    public String getUsername()
    {
        return username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUuid() {
        return uuid;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }
}
