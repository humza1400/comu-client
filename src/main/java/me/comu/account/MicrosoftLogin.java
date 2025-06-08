package me.comu.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.comu.account.openauth.microsoft.MicrosoftAuthResult;
import me.comu.account.openauth.microsoft.MicrosoftAuthenticationException;
import me.comu.account.openauth.microsoft.MicrosoftAuthenticator;
import me.comu.logging.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class MicrosoftLogin {
    private static final String CLIENT_ID = "000000004C12AE6F"; // Official Minecraft client ID
    private static final String REDIRECT_URI = "https://login.live.com/oauth20_desktop.srf";

    public static MicrosoftLoginResult startLoginFlow() {
        try {
            String loginUrl = "https://login.live.com/oauth20_authorize.srf" +
                    "?client_id=" + CLIENT_ID +
                    "&response_type=token" +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8") +
                    "&scope=XboxLive.signin%20offline_access";

            try {
                openInPrivateBrowser(loginUrl);
            } catch (Exception e) {
                Desktop.getDesktop().browse(new URI(loginUrl));
            }

            JOptionPane.showMessageDialog(null, "Please log in using the opened browser window.\nAfter logging in, you will be redirected.\nCopy and paste the full redirected URL into the next prompt.");

            String redirectedUrl = JOptionPane.showInputDialog(null, "Paste the redirected URL here:");

            String accessToken = extractFragmentValue(redirectedUrl, "access_token");
            String refreshToken = extractFragmentValue(redirectedUrl, "refresh_token");

            JOptionPane.showMessageDialog(null, "Login successful!");
            System.out.println("Access Token: " + accessToken);
            System.out.println("Refresh Token: " + refreshToken);

            return new MicrosoftLoginResult(accessToken, refreshToken);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Login failed:\n" + e.getMessage());
        }

        return null;
    }

    public static Session completeMinecraftLogin(String accessToken) throws Exception {
        JsonObject xboxAuthPayload = new JsonObject();
        xboxAuthPayload.addProperty("RelyingParty", "http://auth.xboxlive.com");
        xboxAuthPayload.addProperty("TokenType", "JWT");
        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", "d=" + accessToken);
        xboxAuthPayload.add("Properties", properties);

        JsonObject xboxAuth = postJson("https://user.auth.xboxlive.com/user/authenticate", xboxAuthPayload);
        String xboxToken = xboxAuth.get("Token").getAsString();
        String userHash = xboxAuth.getAsJsonObject("DisplayClaims").getAsJsonArray("xui")
                .get(0).getAsJsonObject().get("uhs").getAsString();

        JsonObject xstsPayload = new JsonObject();
        xstsPayload.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        xstsPayload.addProperty("TokenType", "JWT");
        JsonObject xstsProps = new JsonObject();
        xstsProps.add("UserTokens", new Gson().toJsonTree(new String[]{xboxToken}));
        xstsProps.addProperty("SandboxId", "RETAIL");
        xstsPayload.add("Properties", xstsProps);

        JsonObject xstsAuth = postJson("https://xsts.auth.xboxlive.com/xsts/authorize", xstsPayload);
        String xstsToken = xstsAuth.get("Token").getAsString();

        String identityToken = "XBL3.0 x=" + userHash + ";" + xstsToken;
        JsonObject mcLoginPayload = new JsonObject();
        mcLoginPayload.addProperty("identityToken", identityToken);
        JsonObject mcToken = postJson("https://api.minecraftservices.com/authentication/login_with_xbox", mcLoginPayload);
        String mcAccessToken = mcToken.get("access_token").getAsString();

        HttpURLConnection profileConn = (HttpURLConnection) new URL("https://api.minecraftservices.com/minecraft/profile").openConnection();
        profileConn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        profileConn.connect();
        String profileJson = new BufferedReader(new InputStreamReader(profileConn.getInputStream()))
                .lines().reduce("", (acc, line) -> acc + line);
        JsonParser parser = new JsonParser();
        JsonObject profile = parser.parse(profileJson).getAsJsonObject();

        String name = profile.get("name").getAsString();
        UUID uuid = UUID.fromString(profile.get("id").getAsString());

        return new Session(
                name,
                uuid,
                mcAccessToken,
                Optional.of(userHash),
                Optional.empty(),
                Session.AccountType.MSA
        );
    }

    public static void LoginToMicrosoftAccount(Account account) {
        new Thread(() -> {
            try {
                String params = "client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8") +
                        "&grant_type=refresh_token" +
                        "&refresh_token=" + URLEncoder.encode(account.getRefreshToken(), "UTF-8") +
                        "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");

                byte[] postData = params.getBytes(StandardCharsets.UTF_8);
                URL url = new URL("https://login.live.com/oauth20_token.srf");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.getOutputStream().write(postData);

                String response = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                        .lines().reduce("", (acc, line) -> acc + line);

                JsonParser jsonParser = new JsonParser();
                JsonObject json = jsonParser.parse(response).getAsJsonObject();
                String accessToken = json.get("access_token").getAsString();
                String newRefreshToken = json.get("refresh_token").getAsString();

                Session session = completeMinecraftLogin(accessToken);
                setSession(session);

                account.setLastUsed(System.currentTimeMillis());
                if (!newRefreshToken.equals(account.getRefreshToken())) {
                    account.setRefreshToken(newRefreshToken);
                }

                // save config

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Microsoft re-login failed:\n" + e.getMessage());
            }
        }).start();
    }


    private static JsonObject postJson(String urlStr, JsonObject body) throws IOException {
        URL url = new URL(urlStr);
        JsonParser parser = new JsonParser();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
        }

        InputStream inputStream = conn.getResponseCode() < 400 ?
                conn.getInputStream() : conn.getErrorStream();

        String response = new BufferedReader(new InputStreamReader(inputStream))
                .lines().reduce("", (acc, line) -> acc + line);

        return parser.parse(response).getAsJsonObject();
    }

    private static String extractFragmentValue(String url, String key) throws Exception {
        if (!url.contains("#")) throw new Exception("No URL fragment found.");
        String fragment = url.split("#")[1];
        for (String param : fragment.split("&")) {
            String[] kv = param.split("=");
            if (kv[0].equals(key)) return java.net.URLDecoder.decode(kv[1], "UTF-8");
        }
        throw new Exception("Missing " + key + " in URL fragment.");
    }

    public static void openInPrivateBrowser(String url) throws IOException {
        String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
        Runtime.getRuntime().exec(new String[]{
                chromePath,
                "--incognito",
                url
        });
    }
    public static void setUserMicrosoft(String email, String password) {

        if (email.isBlank() || password.isBlank()) return;
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(email, password);
            UUID uuid = formatUUID(result.getProfile().getId());

            Session session = new Session(
                    result.getProfile().getName(),
                    uuid,
                    result.getAccessToken(),
                    Optional.empty(),
                    Optional.empty(),
                    Session.AccountType.MSA
            );

            setSession(session);
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            Logger.getLogger().print(e.getMessage(), Logger.LogType.ERROR);
        }
    }

    public static void setSession(Session newSession) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            Field sessionField = MinecraftClient.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(mc, newSession);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger().print(e.getMessage(), Logger.LogType.ERROR);
        }
    }

    public static UUID formatUUID(String rawUuid) {
        return UUID.fromString(rawUuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        ));
    }
}