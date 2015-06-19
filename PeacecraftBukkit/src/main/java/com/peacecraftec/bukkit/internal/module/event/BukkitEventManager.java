package com.peacecraftec.bukkit.internal.module.event;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.event.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitEventManager implements EventManager {

    private Plugin plugin;
    private Map<Module, List<Listener>> listeners = new HashMap<Module, List<Listener>>();

    public BukkitEventManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register(Module module, Object listener) {
        if(!(listener instanceof Listener)) {
            throw new IllegalArgumentException("Listener must be Bukkit Listener.");
        }

        Bukkit.getServer().getPluginManager().registerEvents((Listener) listener, this.plugin);
        if(!this.listeners.containsKey(module)) {
            this.listeners.put(module, new ArrayList<Listener>());
        }

        this.listeners.get(module).add((Listener) listener);
    }

    @Override
    public void unregister(Module module, Object listener) {
        if(!(listener instanceof Listener)) {
            throw new IllegalArgumentException("Listener must be Bukkit Listener.");
        }

        HandlerList.unregisterAll((Listener) listener);
        if(this.listeners.containsKey(module)) {
            this.listeners.get(module).remove((Listener) listener);
        }
    }

    @Override
    public void unregisterAll(Module module) {
        if(this.listeners.containsKey(module)) {
            for(Listener listener : new ArrayList<Listener>(this.listeners.get(module))) {
                this.unregister(module, listener);
            }

            this.listeners.remove(module);
        }
    }

    @Override
    public void callModuleEnableEvent(Module module) {
        Bukkit.getServer().getPluginManager().callEvent(new ModuleEnableEvent(module));
    }

    @Override
    public void callModuleDisableEvent(Module module) {
        Bukkit.getServer().getPluginManager().callEvent(new ModuleDisableEvent(module));
    }

}
