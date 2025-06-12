package me.comu.config.configs;

import com.google.gson.*;
import me.comu.Comu;
import me.comu.config.Config;
import me.comu.logging.Logger;
import me.comu.people.Person;
import me.comu.people.enemy.EnemyManager;
import me.comu.people.friend.FriendManager;
import me.comu.people.staff.StaffManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;

public class PeopleConfig extends Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public PeopleConfig(File baseDir) {
        super("people.json", baseDir);
    }

    @Override
    public void save() {
        JsonObject root = new JsonObject();

        root.add("friends", serializePeople(Comu.getInstance().getFriendManager().getFriends()));
        root.add("enemies", serializePeople(Comu.getInstance().getEnemyManager().getEnemies()));
        root.add("staff", serializePeople(Comu.getInstance().getStaffManager().getStaff()));

        try (FileWriter writer = new FileWriter(getFile())) {
            GSON.toJson(root, writer);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger().print("Error saving people config: " + e.getMessage(), Logger.LogType.ERROR);
        }
    }

    @Override
    public void load() {
        if (!getFile().exists()) return;

        try (FileReader reader = new FileReader(getFile())) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);

            if (root.has("friends")) {
                loadPeople(root.getAsJsonArray("friends"), Comu.getInstance().getFriendManager());
            }

            if (root.has("enemies")) {
                loadPeople(root.getAsJsonArray("enemies"), Comu.getInstance().getEnemyManager());
            }

            if (root.has("staff")) {
                loadPeople(root.getAsJsonArray("staff"), Comu.getInstance().getStaffManager());
            }

        } catch (Exception e) {
            Logger.getLogger().print("Error loading people config: " + e.getMessage(), Logger.LogType.ERROR);
            e.printStackTrace();
        }
    }

    private JsonArray serializePeople(Collection<? extends Person> people) {
        JsonArray array = new JsonArray();
        for (Person person : people) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", person.getName());
            obj.addProperty("alias", person.getAlias());
            array.add(obj);
        }
        return array;
    }

    private void loadPeople(JsonArray array, Object manager) {
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();
            String alias = obj.get("alias").getAsString();

            if (manager instanceof FriendManager friendManager) {
                friendManager.add(name, alias);
            } else if (manager instanceof EnemyManager enemyManager) {
                enemyManager.add(name, alias);
            } else if (manager instanceof StaffManager staffManager) {
                staffManager.add(name, alias);
            }
        }
    }
}
