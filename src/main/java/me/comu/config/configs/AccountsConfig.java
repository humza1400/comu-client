package me.comu.config.configs;

import com.google.gson.*;
import me.comu.Comu;
import me.comu.account.Account;
import me.comu.config.Config;
import me.comu.logging.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class AccountsConfig extends Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public AccountsConfig(File baseDir) {
        super("accounts.json", baseDir);
    }

    @Override
    public void save() {
        JsonArray arr = new JsonArray();
        for (Account acct : Comu.getInstance().getAccountManager().getAccounts()) {
            JsonObject o = new JsonObject();
            o.addProperty("email", acct.getEmail());
            o.addProperty("username", acct.getUsername());
            o.addProperty("uuid", acct.getUuid());
            o.addProperty("refreshToken", acct.getRefreshToken());
            o.addProperty("dateAdded", acct.getDateAdded());
            o.addProperty("lastUsed", acct.getLastUsed());
            o.addProperty("isStarred", acct.isStarred());
            arr.add(o);
        }

        try (FileWriter w = new FileWriter(getFile())) {
            GSON.toJson(arr, w);
        } catch (Exception e) {
            Logger.getLogger().print("Failed to save accounts: " + e.getMessage(), Logger.LogType.ERROR);
        }
    }

    @Override
    public void load() {
        if (!getFile().exists()) return;

        try (FileReader r = new FileReader(getFile())) {
            JsonArray arr = GSON.fromJson(r, JsonArray.class);
            var mgr = Comu.getInstance().getAccountManager();
            mgr.clear();

            for (JsonElement el : arr) {
                JsonObject o = el.getAsJsonObject();
                String email = o.get("email").getAsString();
                String user = o.get("username").getAsString();
                String uuid = o.get("uuid").getAsString();
                String token = o.get("refreshToken").getAsString();
                long added = o.get("dateAdded").getAsLong();
                long lastUsed = o.get("lastUsed").getAsLong();
                boolean starred = o.get("isStarred").getAsBoolean();

                mgr.register(new Account(email, user, uuid, token, added, lastUsed, starred));
            }
        } catch (Exception e) {
            Logger.getLogger().print("Failed to load accounts: " + e.getMessage(), Logger.LogType.ERROR);
        }
    }
}
