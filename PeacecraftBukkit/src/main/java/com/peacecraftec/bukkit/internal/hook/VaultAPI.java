package com.peacecraftec.bukkit.internal.hook;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

public class VaultAPI {

	private static Economy economy;
	private static Permission perms;
	private static Chat chat;
	
	public static void hook() {
		RegisteredServiceProvider<Economy> e = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if(e != null) {
			economy = e.getProvider();
			Bukkit.getServer().getLogger().info("[Peacecraft] Hooked into Vault Economy API.");
		} else {
			Bukkit.getServer().getLogger().severe("[Peacecraft] Could not find a Vault economy provider to use!");
		}
		
		RegisteredServiceProvider<Permission> p = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
		if(p != null) {
			perms = p.getProvider();
			Bukkit.getServer().getLogger().info("[Peacecraft] Hooked into Vault Permissions API.");
		} else {
			Bukkit.getServer().getLogger().severe("[Peacecraft] Could not find a Vault permissions provider to use!");
		}
		
		RegisteredServiceProvider<Chat> c = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
		if(c != null) {
			chat = c.getProvider();
			Bukkit.getServer().getLogger().info("[Peacecraft] Hooked into Vault Chat API.");
		} else {
			Bukkit.getServer().getLogger().severe("[Peacecraft] Could not find a Vault chat provider to use!");
		}
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	public static Permission getPermissions() {
		return perms;
	}
	
	public static Chat getChat() {
		return chat;
	}

	public static void setEconomy(Economy eco) {
		Bukkit.getServer().getServicesManager().register(Economy.class, eco, Bukkit.getServer().getPluginManager().getPlugin("Peacecraft"), ServicePriority.Highest);
		Bukkit.getServer().getLogger().info("[Peacecraft] Registered Vault economy \"" + eco.getName() + "\".");
	}
	
	public static void setPermissions(Permission perms) {
		Bukkit.getServer().getServicesManager().register(Permission.class, perms, Bukkit.getServer().getPluginManager().getPlugin("Peacecraft"), ServicePriority.Highest);
		Bukkit.getServer().getLogger().info("[Peacecraft] Registered Vault permissions \"" + perms.getName() + "\".");
	}
	
	public static void setChat(Chat chat) {
		Bukkit.getServer().getServicesManager().register(Chat.class, chat, Bukkit.getServer().getPluginManager().getPlugin("Peacecraft"), ServicePriority.Highest);
		Bukkit.getServer().getLogger().info("[Peacecraft] Registered Vault chat \"" + chat.getName() + "\".");
	}
	
}
