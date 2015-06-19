package com.peacecraftec.bukkit.internal.vault;

import com.peacecraftec.module.ModuleManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class VaultAPI {
    private static ModuleManager manager;
    private static Economy economy;
    private static Permission perms;
    private static Chat chat;

    public static void init(ModuleManager manager) {
        VaultAPI.manager = manager;
    }

    public static void hook() {
        RegisteredServiceProvider<Economy> e = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if(e != null) {
            VaultAPI.economy = e.getProvider();
            Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Hooked into Vault Economy API.");
        } else {
            Bukkit.getServer().getLogger().severe("[PeacecraftBukkit] Could not find a Vault economy provider to use!");
        }

        RegisteredServiceProvider<Permission> p = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(p != null) {
            VaultAPI.perms = p.getProvider();
            Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Hooked into Vault Permissions API.");
        } else {
            Bukkit.getServer().getLogger().severe("[PeacecraftBukkit] Could not find a Vault permissions provider to use!");
        }

        RegisteredServiceProvider<Chat> c = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(c != null) {
            VaultAPI.chat = c.getProvider();
            Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Hooked into Vault Chat API.");
        } else {
            Bukkit.getServer().getLogger().severe("[PeacecraftBukkit] Could not find a Vault chat provider to use!");
        }
    }

    public static void cleanup() {
        VaultAPI.economy = null;
        VaultAPI.perms = null;
        VaultAPI.chat = null;
        VaultAPI.manager = null;
    }

    public static Economy getEconomy() {
        return VaultAPI.economy;
    }

    public static Permission getPermissions() {
        return VaultAPI.perms;
    }

    public static Chat getChat() {
        return VaultAPI.chat;
    }

    public static void setEconomy(Economy eco) {
        Bukkit.getServer().getServicesManager().register(Economy.class, eco, Bukkit.getServer().getPluginManager().getPlugin(VaultAPI.manager.getImplementationName()), ServicePriority.Highest);
        Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Registered Vault economy \"" + eco.getName() + "\".");
    }

    public static void setPermissions(Permission perms) {
        Bukkit.getServer().getServicesManager().register(Permission.class, perms, Bukkit.getServer().getPluginManager().getPlugin(VaultAPI.manager.getImplementationName()), ServicePriority.Highest);
        Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Registered Vault permissions \"" + perms.getName() + "\".");
    }

    public static void setChat(Chat chat) {
        Bukkit.getServer().getServicesManager().register(Chat.class, chat, Bukkit.getServer().getPluginManager().getPlugin(VaultAPI.manager.getImplementationName()), ServicePriority.Highest);
        Bukkit.getServer().getLogger().info("[PeacecraftBukkit] Registered Vault chat \"" + chat.getName() + "\".");
    }
}
