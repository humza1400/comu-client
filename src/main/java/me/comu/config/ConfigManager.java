package me.comu.config;

import me.comu.config.configs.AccountsConfig;
import me.comu.config.configs.ModulesConfig;
import me.comu.config.configs.PeopleConfig;
import me.comu.logging.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final List<Config> configs = new ArrayList<>();
    private final File configDir;

    public ConfigManager(File baseDirectory) {
        this.configDir = baseDirectory;
        if (!configDir.exists()) configDir.mkdirs();

        register(new ModulesConfig(baseDirectory));
        register(new PeopleConfig(baseDirectory));
        register(new AccountsConfig(baseDirectory));
    }

    public void register(Config config) {
        configs.add(config);
    }

    public void saveAll() {
        for (Config config : configs) {
            config.save();
        }
    }

    public void loadAll() {
        for (Config config : configs) {
            Logger.getLogger().print("Loading config: " + config.getName());
            config.load();
        }
    }

    public File getConfigDirectory() {
        return configDir;
    }

    public <T extends Config> T getConfig(Class<T> clazz) {
        for (Config config : configs) {
            if (clazz.isInstance(config)) {
                return clazz.cast(config);
            }
        }
        throw new IllegalStateException("No config registered for " + clazz.getSimpleName());
    }
}
