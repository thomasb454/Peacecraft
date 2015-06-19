package com.peacecraftec.bukkit;

import com.peacecraftec.bukkit.backup.PeacecraftBackup;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.bukkit.internal.module.BukkitModuleManager;
import com.peacecraftec.bukkit.internal.module.cmd.CommandUtil;
import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import com.peacecraftec.bukkit.protect.PeacecraftProtect;
import com.peacecraftec.bukkit.restrictions.PeacecraftRestrictions;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.storage.yaml.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class PeacecraftBukkit extends JavaPlugin {

    private ModuleManager modules;

    @Override
    public void onEnable() {
        YamlStorage.setParameters(YamlConstructor.class, YamlRepresenter.class);

        this.modules = new BukkitModuleManager(this);

        CommandUtil.init(this.modules);
        VaultAPI.init(this.modules);

        this.modules.register("Core", PeacecraftCore.class);
        this.modules.register("Permissions", PeacecraftPerms.class);
        this.modules.register("Worlds", PeacecraftWorlds.class);
        this.modules.register("Chat", PeacecraftChat.class);
        this.modules.register("Economy", PeacecraftEco.class);
        this.modules.register("Protect", PeacecraftProtect.class);
        this.modules.register("Restrictions", PeacecraftRestrictions.class);
        this.modules.register("Backup", PeacecraftBackup.class);
        this.modules.register("Lots", PeacecraftLots.class);
        this.modules.register("Portals", PeacecraftPortals.class);
        this.modules.register("Stats", PeacecraftStats.class);
        this.modules.loadAll();

        VaultAPI.hook();
    }

    @Override
    public void onDisable() {
        this.modules.unloadAll();
        this.modules.cleanup();
        this.modules = null;

        VaultAPI.cleanup();
        CommandUtil.cleanup();
    }

    public ModuleManager getModules() {
        return this.modules;
    }

}
