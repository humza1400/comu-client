package me.comu.config;

import java.io.File;

public abstract class Config {
    private final String name;
    private final File directory;
    private final File file;

    public Config(String name, File directory) {
        this.name = name;
        this.directory = directory;
        this.file = new File(directory, name);
    }

    public String getName() {
        return name;
    }

    public File getDirectory() {
        return directory;
    }

    public File getFile() {
        return file;
    }

    public abstract void load();
    public abstract void save();
}
