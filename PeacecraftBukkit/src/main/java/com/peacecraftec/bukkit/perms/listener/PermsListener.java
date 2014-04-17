package com.peacecraftec.bukkit.perms.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.internal.module.event.ModuleEnableEvent;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.perms.core.PermissionGroup;
import com.peacecraftec.bukkit.perms.core.PermissionPlayer;
import com.peacecraftec.bukkit.perms.core.PermissionWorld;

public class PermsListener implements Listener {

	private PeacecraftPerms module;
	private Map<String, PermissionAttachment> attachments = new HashMap<String, PermissionAttachment>();
	
	public PermsListener(PeacecraftPerms module) {
		this.module = module;
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.module.refreshPermissions(player);
		}
	}
	
	@EventHandler
	public void onModuleEnable(ModuleEnableEvent event) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.module.refreshPermissions(player);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.attachTo(event.getPlayer());
		if(this.module.getManager().isEnabled("Chat")) {
			((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(this.attachments.containsKey(event.getPlayer().getName())) {
			this.detach(event.getPlayer());
			this.attachments.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if(this.attachments.containsKey(event.getPlayer().getName())) {
			this.detach(event.getPlayer());
			this.attachments.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		this.module.refreshPermissions(event.getPlayer());
		if(this.module.getManager().isEnabled("Chat")) {
			((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		World w = event.getPlayer().getWorld();
		PermissionWorld world = this.module.getPermsManager().getWorldIfGroups(w.getName());
		if(world == null) {
			if(!this.module.getPermsManager().hasGlobalSpawn(w.getName())) {
				this.module.getPermsManager().setGlobalSpawn(w.getName(), w.getSpawnLocation());
			}
			
			event.setRespawnLocation(this.module.getPermsManager().getGlobalSpawn(w.getName()));
			return;
		}
		
		world.addIfMissing(event.getPlayer().getName());
		PermissionPlayer player = world.getPlayer(event.getPlayer().getName());
		Location groupSpawn = null;
		for(PermissionGroup g : player.getGroups()) {
			if(this.module.getPermsManager().hasGroupSpawn(w.getName(), g.getName())) {
				groupSpawn = this.module.getPermsManager().getGroupSpawn(w.getName(), g.getName());
				break;
			}
		}
		
		if(groupSpawn == null) {
			if(!this.module.getPermsManager().hasGlobalSpawn(w.getName())) {
				this.module.getPermsManager().setGlobalSpawn(w.getName(), w.getSpawnLocation());
			}
			
			event.setRespawnLocation(this.module.getPermsManager().getGlobalSpawn(w.getName()));
		} else {
			event.setRespawnLocation(groupSpawn);
		}
	}

	public void attachTo(Player player) {
		boolean existed = this.module.getPermsManager().getWorld(player.getWorld().getName()).getPlayer(player.getName()) != null;
		this.module.getPermsManager().getWorld(player.getWorld().getName()).addIfMissing(player.getName());
		PermissionAttachment attach = player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Peacecraft"));
		attach.setPermission("INTERNAL_PERMISSION." + player.getName(), true);
		attach.setPermission("INTERNAL_PERMISSION.-" + player.getName(), true);
		this.attachments.put(player.getName(), attach);
		this.module.refreshPermissions(player);
		if(!existed) {
			this.module.getPermsManager().playerJoined();
			this.module.getManager().broadcastMessage("perms.welcome", player.getName(), this.module.getPermsManager().getTotalPlayers());
		}
	}
	
	public void detach(Player player) {
		player.removeAttachment(this.getAttachments().get(player.getName()));
		Permission positive = Bukkit.getServer().getPluginManager().getPermission(player.getName());
		Permission negative = Bukkit.getServer().getPluginManager().getPermission("-" + player.getName());
		if(positive != null) {
			Bukkit.getServer().getPluginManager().removePermission(positive);
		}
		
		if(negative != null) {
			Bukkit.getServer().getPluginManager().removePermission(negative);
		}
	}
	
	public Map<String, PermissionAttachment> getAttachments() {
		return this.attachments;
	}
	
}
