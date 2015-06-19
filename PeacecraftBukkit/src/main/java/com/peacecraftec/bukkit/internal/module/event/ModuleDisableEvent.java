package com.peacecraftec.bukkit.internal.module.event;

import com.peacecraftec.module.Module;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ModuleDisableEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Module module;

    public ModuleDisableEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return this.module;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}