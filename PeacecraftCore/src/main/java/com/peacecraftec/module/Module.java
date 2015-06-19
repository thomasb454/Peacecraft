package com.peacecraftec.module;

import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

import java.io.File;
import java.util.logging.Logger;

public abstract class Module {

    private String name;
    private File directory;
    private Storage config;
    private ModuleLogger logger;
    private ModuleManager manager;

    public Module(String name, ModuleManager manager) {
        this(name, manager, new YamlStorage(new File(new File(manager.getDataFolder(), name), "config.yml")));
    }

    public Module(String name, ModuleManager manager, Storage config) {
        this.name = name;
        this.directory = new File(manager.getDataFolder(), name);
        this.manager = manager;
        this.config = config;
        this.logger = new ModuleLogger(this);
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void reload();

    public String getName() {
        return this.name;
    }

    public File getDirectory() {
        if(!this.directory.exists()) {
            this.directory.mkdirs();
        }

        return this.directory;
    }

    public Storage getConfig() {
        return this.config;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public ModuleManager getManager() {
        return this.manager;
    }

}
