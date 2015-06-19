package com.peacecraftec.bukkit.internal.module;

import com.peacecraftec.bukkit.internal.module.cmd.BukkitCommandManager;
import com.peacecraftec.bukkit.internal.module.event.BukkitEventManager;
import com.peacecraftec.bukkit.internal.module.permission.BukkitPermissionManager;
import com.peacecraftec.bukkit.internal.module.scheduler.BukkitScheduler;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.CommandManager;
import com.peacecraftec.module.event.EventManager;
import com.peacecraftec.module.lang.LanguageManager;
import com.peacecraftec.module.permission.PermissionManager;
import com.peacecraftec.module.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.util.logging.Logger;

public class BukkitModuleManager extends ModuleManager {
    private CommandManager commands;
    private PermissionManager permissions;
    private EventManager events;
    private Scheduler scheduler;
    private LanguageManager languages;

    private Plugin plugin;

    public BukkitModuleManager(Plugin plugin) {
        super(plugin.getDataFolder());
        this.plugin = plugin;
        this.commands = new BukkitCommandManager(plugin, this);
        this.permissions = new BukkitPermissionManager();
        this.events = new BukkitEventManager(plugin);
        this.scheduler = new BukkitScheduler(plugin);
        this.languages = new LanguageManager(this);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public String getImplementationName() {
        return "PeacecraftBukkit";
    }

    @Override
    public Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public InputStream getResource(String path) {
        return this.plugin.getResource(path);
    }

    @Override
    public String getDefaultWorld() {
        return Bukkit.getWorlds().get(0).getName();
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commands;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return this.permissions;
    }

    @Override
    public EventManager getEventManager() {
        return this.events;
    }

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.languages;
    }
}
