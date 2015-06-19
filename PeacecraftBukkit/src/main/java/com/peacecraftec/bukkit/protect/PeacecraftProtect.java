package com.peacecraftec.bukkit.protect;

import com.peacecraftec.bukkit.protect.command.ProtectCommands;
import com.peacecraftec.bukkit.protect.core.ProtectManager;
import com.peacecraftec.bukkit.protect.core.interact.InteractAction;
import com.peacecraftec.bukkit.protect.listener.ProtectListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

import java.util.HashMap;
import java.util.Map;

public class PeacecraftProtect extends Module {
    private ProtectManager manager;
    private Map<String, InteractAction> actions = new HashMap<String, InteractAction>();

    public PeacecraftProtect(String name, ModuleManager manager) {
        super(name, manager);
    }

    @Override
    public void onEnable() {
        this.manager = new ProtectManager(this);
        this.getManager().getPermissionManager().register(this, ProtectPermissions.class);
        this.getManager().getCommandManager().register(this, new ProtectCommands(this));
        this.getManager().getEventManager().register(this, new ProtectListener(this));
    }

    @Override
    public void onDisable() {
        this.manager = null;
    }

    @Override
    public void reload() {
    }

    public ProtectManager getProtectManager() {
        return this.manager;
    }

    public InteractAction getAction(String player) {
        return this.actions.get(player);
    }

    public void setAction(String player, InteractAction action) {
        if(action == null) {
            this.actions.remove(player);
        } else {
            this.actions.put(player, action);
        }
    }
}
