package com.peacecraftec.bukkit.perms.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.storage.Storage;

public class PermissionGroup {

	private String name;
	private PermissionWorld world;
	private Storage data;
	private PeacecraftPerms module;
	
	public PermissionGroup(String name, PermissionWorld world, Storage data, PeacecraftPerms module) {
		this.name = name;
		this.world = world;
		this.data = data;
		this.module = module;
	}
	
	public String getName() {
		return this.name;
	}
	
	public PermissionWorld getWorld() {
		return this.world;
	}
	
	public String getPrefix() {
		if(!this.data.contains("groups." + this.name + ".info.prefix")) {
			return "";
		}
		
		return ChatColor.translateAlternateColorCodes('&', this.data.getString("groups." + this.name + ".info.prefix"));
	}
	
	public String getSuffix() {
		if(!this.data.contains("groups." + this.name + ".info.suffix")) {
			return "";
		}
		
		return ChatColor.translateAlternateColorCodes('&', this.data.getString("groups." + this.name + ".info.suffix"));
	}
	
	public void setPrefix(String prefix) {
		this.data.setValue("groups." + this.name + ".info.prefix", prefix);
		this.data.save();
		if(this.module.getManager().isEnabled("Chat")) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(player.getWorld().getName()) && this.world.getPlayer(player.getName()).getGroupNames().contains(this.name)) {
					((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
				}
			}
		}
	}
	
	public void setSuffix(String suffix) {
		this.data.setValue("groups." + this.name + ".info.suffix", suffix);
		this.data.save();
		if(this.module.getManager().isEnabled("Chat")) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(player.getWorld().getName()) && this.world.getPlayer(player.getName()).getGroupNames().contains(this.name)) {
					((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
				}
			}
		}
	}
	
	public void removePrefix() {
		this.data.remove("groups." + this.name + ".info.prefix");
		this.data.save();
		if(this.module.getManager().isEnabled("Chat")) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(player.getWorld().getName()) && this.world.getPlayer(player.getName()).getGroupNames().contains(this.name)) {
					((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
				}
			}
		}
	}
	
	public void removeSuffix() {
		this.data.remove("groups." + this.name + ".info.suffix");
		this.data.save();
		if(this.module.getManager().isEnabled("Chat")) {
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(player.getWorld().getName()) && this.world.getPlayer(player.getName()).getGroupNames().contains(this.name)) {
					((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
				}
			}
		}
	}
	
	public List<PermissionGroup> getInheritance() {
		List<PermissionGroup> ret = new ArrayList<PermissionGroup>();
		if(!this.data.contains("groups." + this.name + ".inherits")) {
			return ret;
		}
		
		for(String group : this.data.getList("groups." + this.name + ".inherits", String.class)) {
			PermissionGroup g = this.world.getGroup(group);
			if(g != null) {
				ret.add(g);
			}
		}
		
		return ret;
	}
	
	public List<String> getRawPermissions() {
		if(!this.data.contains("groups." + this.name + ".permissions")) {
			return new ArrayList<String>();
		}
		
		return this.data.getList("groups." + this.name + ".permissions", String.class);
	}
	
	public Map<String, Boolean> getOwnPermissions() {
		Set<Permission> known = Bukkit.getPluginManager().getPermissions();
		List<String> perms = this.getRawPermissions();
		Map<String, Boolean> ret = new HashMap<String, Boolean>();
		for(String perm : perms) {
			if(perm.startsWith("INTERNAL_PERMISSION") || perm.startsWith("-INTERNAL_PERMISSION")) continue;
			boolean val = true;
			if(perm.startsWith("-")) {
				perm = perm.substring(1, perm.length());
				val = false;
			}
			
			if(perm.endsWith("*")) {
				String node = perm.substring(0, perm.length() - 1);
				if(perm.endsWith(".")) {
					node = node.substring(0, perm.length() - 1);
				}
				
				for(Permission p : known) {
					if(p.getName().startsWith(node) && !p.getName().startsWith("INTERNAL_PERMISSION")) {
						if(!ret.containsKey(p.getName()) || ret.get(p.getName()) != val) {
							ret.put(p.getName(), val);
						}
					}
				}
			}
			
			if(!ret.containsKey(perm) || ret.get(perm) != val) {
				ret.put(perm, val);
			}
		}
		
		return ret;
	}
	
	public Map<String, Boolean> getAllPermissions() {
		Map<String, Boolean> perms = new HashMap<String, Boolean>();
		for(PermissionGroup group : this.getInheritance()) {
			perms.putAll(group.getAllPermissions());
		}
		
		perms.putAll(this.getOwnPermissions());
		return perms;
	}
	
	public boolean addPermission(String permission) {
		List<String> perms = this.getRawPermissions();
		if(!perms.contains(permission)) {
			perms.add(permission);
			this.data.setValue("groups." + this.name + ".permissions", perms);
			this.data.save();
			
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(p.getWorld().getName()) && this.getWorld().getPlayer(p.getName()).getGroupNames().contains(this.getName().toLowerCase())) {
					this.module.refreshPermissions(p);
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removePermission(String permission) {
		List<String> perms = this.getRawPermissions();
		if(perms.contains(permission)) {
			perms.remove(permission);
			this.data.setValue("groups." + this.name + ".permissions", perms);
			this.data.save();
			
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				if(this.getWorld().hasGroupsOf(p.getWorld().getName()) && this.getWorld().getPlayer(p.getName()).getGroupNames().contains(this.getName().toLowerCase())) {
					this.module.refreshPermissions(p);
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getInfoBoolean(String name, boolean def) {
		return this.data.getBoolean("groups." + this.name + ".info." + name, def);
	}
	
	public double getInfoDouble(String name, double def) {
		return this.data.getDouble("groups." + this.name + ".info." + name, def);
	}
	
	public int getInfoInt(String name, int def) {
		return this.data.getInteger("groups." + this.name + ".info." + name, def);
	}
	
	public String getInfoString(String name, String def) {
		return this.data.getString("groups." + this.name + ".info." + name, def);
	}
	
	public void setInfoBoolean(String name, boolean value) {
		this.data.setValue("groups." + this.name + ".info." + name, value);
		this.data.save();
	}
	
	public void setInfoDouble(String name, double value) {
		this.data.setValue("groups." + this.name + ".info." + name, value);
		this.data.save();
	}
	
	public void setInfoInteger(String name, int value) {
		this.data.setValue("groups." + this.name + ".info." + name, value);
		this.data.save();
	}
	
	public void setInfoString(String name, String value) {
		this.data.setValue("groups." + this.name + ".info." + name, value);
		this.data.save();
	}

}
