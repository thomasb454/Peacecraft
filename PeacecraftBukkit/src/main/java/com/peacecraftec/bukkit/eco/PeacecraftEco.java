package com.peacecraftec.bukkit.eco;

import com.peacecraftec.bukkit.eco.command.EcoCommands;
import com.peacecraftec.bukkit.eco.core.EcoManager;
import com.peacecraftec.bukkit.eco.listener.EcoListener;
import com.peacecraftec.bukkit.eco.vault.PeaceEcoVault;
import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PeacecraftEco extends Module {

    private EcoManager manager;

    public PeacecraftEco(String name, ModuleManager manager) {
        super(name, manager);
    }

    @Override
    public void onEnable() {
        this.manager = new EcoManager(this);
        VaultAPI.setEconomy(new PeaceEcoVault(this.manager, this.getManager()));

        this.getManager().getPermissionManager().register(this, EcoPermissions.class);
        this.getManager().getCommandManager().register(this, new EcoCommands(this));
        this.getManager().getEventManager().register(this, new EcoListener(this));

        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            this.manager.getWorld(player.getWorld().getName()).addIfMissing(player.getName());
        }
    }

    @Override
    public void onDisable() {
        this.manager = null;
    }

    @Override
    public void reload() {
        this.getEcoManager().reload();
    }

    public EcoManager getEcoManager() {
        return this.manager;
    }

}
