package com.peacecraftec.bukkit.perms;

import com.peacecraftec.bukkit.chat.ChatPermissions;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.perms.command.PermsCommands;
import com.peacecraftec.bukkit.perms.core.PermissionManager;
import com.peacecraftec.bukkit.perms.core.PermissionPlayer;
import com.peacecraftec.bukkit.perms.core.PermissionWorld;
import com.peacecraftec.bukkit.perms.listener.PermsListener;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeacecraftPerms extends Module {

	private PermissionManager manager;
	private PermsListener listen;
	
	public PeacecraftPerms(ModuleManager manager) {
		super("Permissions", manager);
	}

	@Override
	public void onEnable() {
		this.manager = new PermissionManager(this);
		this.getManager().getPermissionManager().register(this, PermsPermissions.class);
		this.getManager().getCommandManager().register(this, new PermsCommands(this));
		this.getManager().getEventManager().register(this, this.listen = new PermsListener(this));
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.listen.attachTo(player);
		}
		
		this.getManager().getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				if(getManager().isEnabled("Stats")) {
					PeacecraftStats stats = (PeacecraftStats) getManager().getModule("Stats");
					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
						PermissionWorld w = manager.getWorld(player.getWorld().getName());
						PermissionPlayer p = w.getPlayer(player.getName());
						if(p != null) {
							for(String group : new ArrayList<String>(p.getGroupNames())) {
								if(w.isAutoRanked(group)) {
									long minutes = stats.getPlayTime(player.getName()) / 60000;
									if(minutes >= w.getRankMinutes(group)) {
										String to = w.getAutoRank(group);
										if(w.getGroup(to) != null) {
											p.removeGroup(group);
											p.addGroup(to);
											BukkitCommandSender.wrap(player, getManager().getLanguageManager()).sendMessage("perms.auto-ranked-up", group, to);
										}
									}
								}
							}
						}
					}
				}
			}
		}, 20, 2400);
	}

	@Override
	public void onDisable() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.listen.detach(player);
		}

		this.listen = null;
		this.manager = null;
	}
	
	@Override
	public void reload() {
		this.getPermsManager().reload();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.refreshPermissions(player);
		}
	}

	public PermissionManager getPermsManager() {
		return this.manager;
	}

	public void refreshPermissions(Player player) {
		if(!this.listen.getAttachments().containsKey(player.getName())) {
			this.listen.attachTo(player);
		}

		this.getPermsManager().getWorld(player.getWorld().getName()).addIfMissing(player.getName());
		Map<String, Boolean> permissions = this.getPermsManager().getWorld(player.getWorld().getName()).getPlayer(player.getName()).getAllPermissions();
		Permission positive = Bukkit.getServer().getPluginManager().getPermission("INTERNAL_PERMISSION." + player.getName());
		Permission negative = Bukkit.getServer().getPluginManager().getPermission("INTERNAL_PERMISSION.-" + player.getName());
		if(positive != null) {
			Bukkit.getServer().getPluginManager().removePermission(positive);
		}
		
		if(negative != null) {
			Bukkit.getServer().getPluginManager().removePermission(negative);
		}

		Map<String, Boolean> po = new HashMap<String, Boolean>();
		Map<String, Boolean> ne = new HashMap<String, Boolean>();
		for(String key : permissions.keySet()) {
			if(!key.startsWith("INTERNAL_PERMISSION")) {
				if(permissions.get(key)) {
					po.put(key, true);
				} else {
					ne.put(key, false);
				}
			}
		}

		positive = new Permission("INTERNAL_PERMISSION." + player.getName(), PermissionDefault.FALSE, po);
		negative = new Permission("INTERNAL_PERMISSION.-" + player.getName(), PermissionDefault.FALSE, ne);
		Bukkit.getServer().getPluginManager().addPermission(positive);
		Bukkit.getServer().getPluginManager().addPermission(negative);
		player.recalculatePermissions();
		if(this.getManager().isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getManager().getModule("Chat");
			if(player.hasPermission(ChatPermissions.MOD)) {
				chat.setMod(player.getName(), true);
			} else {
				chat.setMod(player.getName(), false);
			}
		}
	}

	public boolean hasPermission(String player, String permission) {
		PermissionPlayer p = this.getPermsManager().getWorld(this.getManager().getDefaultWorld()).getPlayer(player);
		if(p == null) {
			return false;
		}

		Boolean b = p.getAllPermissions().get(permission);
		if(b != null) {
			return b;
		} else {
			return false;
		}
	}

}
