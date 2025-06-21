package me.comu.account;

public class Account {
    private final String email;
    private String username;
    private final String uuid;
    private String refreshToken;
    private final long dateAdded;
    private long lastUsed;
    private boolean starred;

    public Account(String email, String username, String uuid, String refreshToken) {
        this.email = email;
        this.username = username;
        this.uuid = uuid;
        this.refreshToken = refreshToken;
        this.dateAdded = System.currentTimeMillis();
        this.lastUsed = this.dateAdded;
        this.starred = false;
    }

    public Account(String email, String username, String uuid, String refreshToken, long dateAdded, long lastUsed, boolean starred) {
        this.email = email;
        this.username = username;
        this.uuid = uuid;
        this.refreshToken = refreshToken;
        this.dateAdded = dateAdded;
        this.lastUsed = lastUsed;
        this.starred = starred;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long ts) {
        this.lastUsed = ts;
    }

    public void setStarred(boolean b) {
        this.starred = b;
    }

    public boolean isStarred() {
        return starred;
    }
}
