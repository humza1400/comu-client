package me.comu.config.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.comu.Comu;
import me.comu.config.Config;
import me.comu.keybind.Keybind;
import me.comu.logging.Logger;
import me.comu.module.Module;
import me.comu.module.ToggleableModule;
import me.comu.utils.PropertyUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class ModulesConfig extends Config {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ModulesConfig(File baseDir) {
        super("modules.json", baseDir);
    }

    @Override
    public void save() {
        JsonArray array = new JsonArray();
        List<Module> modules = Comu.getInstance().getModuleManager().getModules();

        for (Module module : modules) {
            JsonObject object = new JsonObject();
            object.addProperty("name", module.getName());

            if (!module.getDisplayName().equals(module.getName())) {
                object.addProperty("displayName", module.getDisplayName());
            }

            if (module instanceof ToggleableModule toggleable) {
                object.addProperty("enabled", toggleable.isEnabled());

                Keybind keybind = Comu.getInstance().getKeybindManager().getKeybindByLabel(toggleable.getName());
                if (keybind != null) {
                    object.addProperty("key", keybind.getKey());
                }
            }

            JsonObject properties = new JsonObject();
            for (var property : module.getProperties()) {
                properties.addProperty(property.getName(), PropertyUtils.serializeValue(property));
            }
            object.add("properties", properties);

            array.add(object);
        }

        try (FileWriter writer = new FileWriter(getFile())) {
            GSON.toJson(array, writer);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.getLogger().print("Error saving modules config: " + e.getMessage(), Logger.LogType.ERROR);
        }
    }

    @Override
    public void load() {
        if (!getFile().exists()) return;

        try (FileReader reader = new FileReader(getFile())) {
            JsonArray array = GSON.fromJson(reader, JsonArray.class);

            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                String name = obj.get("name").getAsString();

                Module module = Comu.getInstance().getModuleManager().getModuleByName(name);
                if (module == null) {
                    Logger.getLogger().print("Module not found: " + name, Logger.LogType.WARN);
                    continue;
                }

                if (obj.has("displayName")) {
                    String displayName = obj.get("displayName").getAsString();
                    module.setDisplayName(displayName);
                }

                if (module instanceof ToggleableModule toggleable) {
                    if (obj.has("enabled")) {
                        toggleable.setEnabled(obj.get("enabled").getAsBoolean());
                    }

                    if (obj.has("key")) {
                        int keyCode = obj.get("key").getAsInt();
                        Keybind keybind = Comu.getInstance().getKeybindManager().getKeybindByLabel(name);
                        if (keybind != null) {
                            keybind.setKey(keyCode);
                        }
                    }
                }

                if (obj.has("properties")) {
                    JsonObject props = obj.getAsJsonObject("properties");

                    for (var property : module.getProperties()) {
                        if (!props.has(property.getName())) continue;
                        String value = props.get(property.getName()).getAsString();
                        Object parsed = PropertyUtils.parseValue(property, value);
                        PropertyUtils.safelySet(property, parsed);

                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger().print("Error loading modules config: " + e.getMessage(), Logger.LogType.ERROR);
            e.printStackTrace();
        }
    }
}
