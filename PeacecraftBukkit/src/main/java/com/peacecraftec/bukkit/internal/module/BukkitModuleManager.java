package com.peacecraftec.bukkit.internal.module;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.internal.module.cmd.BukkitCommandManager;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitPlayerSender;
import com.peacecraftec.bukkit.internal.module.event.BukkitEventManager;
import com.peacecraftec.bukkit.internal.module.permission.BukkitPermissionManager;
import com.peacecraftec.bukkit.internal.module.scheduler.BukkitScheduler;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.CommandManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.module.event.EventManager;
import com.peacecraftec.module.permission.PermissionManager;
import com.peacecraftec.module.scheduler.Scheduler;

public class BukkitModuleManager extends ModuleManager {

	private CommandManager commands;
	private PermissionManager permissions;
	private EventManager events;
	private Scheduler scheduler;
	
	private Plugin plugin;
	
	public BukkitModuleManager(Plugin plugin) {
		super(plugin.getDataFolder());
		this.plugin = plugin;
		this.commands = new BukkitCommandManager(plugin, this);
		this.permissions = new BukkitPermissionManager();
		this.events = new BukkitEventManager(plugin);
		this.scheduler = new BukkitScheduler(plugin);
	}

	@Override
	public String getImplementationName() {
		return "Peacecraft";
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
	public void broadcastMessage(String key) {
		String msg = this.getLanguageManager().getDefault().translate(key);
		Bukkit.getServer().broadcastMessage(msg);
		if(this.isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getModule("Chat");
			chat.broadcastWeb(msg);
		}
	}

	@Override
	public void broadcastMessage(String key, Object... args) {
		String msg = this.getLanguageManager().getDefault().translate(key, args);
		Bukkit.getServer().broadcastMessage(msg);
		if(this.isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getModule("Chat");
			chat.broadcastWeb(msg);
		}
	}

	@Override
	public PlayerSender[] getPlayerSenders() {
		Player players[] = Bukkit.getServer().getOnlinePlayers();
		PlayerSender ret[] = new PlayerSender[players.length];
		for(int index = 0; index < players.length; index++) {
			ret[index] = new BukkitPlayerSender(players[index], this.getLanguageManager());
		}
		
		return ret;
	}

	@Override
	public List<PlayerSender> matchPlayerSender(String name) {
		List<Player> players = Bukkit.getServer().matchPlayer(name);
		boolean had = players.size() > 0;
		if(this.isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getModule("Chat");
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(!players.contains(player)) {
					if(chat.hasName(player.getName())) {
						String nick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', chat.getName(player.getName())));
						if(!had && nick.equalsIgnoreCase(name)) {
							players.clear();
							players.add(player);
							break;
						}
						
						if(nick.toLowerCase().contains(name.toLowerCase())) {
							players.add(player);
						}
					}
				}
			}
		}
		
		List<PlayerSender> ret = new ArrayList<PlayerSender>();
		for(Player player : players) {
			ret.add(new BukkitPlayerSender(player, this.getLanguageManager()));
		}
		
		return ret;
	}

	@Override
	public PlayerSender getPlayerSender(String name) {
		Player player = Bukkit.getServer().getPlayer(name);
		if(player == null) {
			return null;
		}
		
		return new BukkitPlayerSender(player, this.getLanguageManager());
	}

}
