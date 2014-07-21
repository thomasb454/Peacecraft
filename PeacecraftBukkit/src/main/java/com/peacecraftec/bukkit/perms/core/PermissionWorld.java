package com.peacecraftec.bukkit.perms.core;

import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionWorld {

	private String name;
	private String ginherit;
	private Storage groups;
	private Storage players;
	private PeacecraftPerms module;
	
	public PermissionWorld(String name, String ginherit, Storage groups, Storage players, PeacecraftPerms module) {
		this.name = name;
		this.ginherit = ginherit;
		this.groups = groups;
		this.players = players;
		this.module = module;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getGroupWorld() {
		return this.ginherit;
	}
	
	public boolean hasGroupsOf(String world) {
		return this.name.equalsIgnoreCase(world) || this.ginherit.equalsIgnoreCase(world) || this.module.getPermsManager().getWorld(world).getGroupWorld().equalsIgnoreCase(this.name);
	}
	
	public List<String> getGroups() {
		return this.groups.getRelativeKeys("groups", false);
	}
	
	public PermissionGroup getDefaultGroup() {
		return this.getGroup(this.groups.getString("default"));
	}
	
	public PermissionGroup getGroup(String name) {
		name = name.toLowerCase();
		if(!this.groups.contains("groups." + name)) {
			return null;
		}
		
		return new PermissionGroup(name, this, this.groups, this.module);
	}

	public void createGroup(String name) {
		this.groups.setValue("groups." + name + ".permissions", new ArrayList<String>());
		this.groups.save();
	}

	public void deleteGroup(PermissionGroup group) {
		this.groups.remove("groups." + group.getName());
		this.groups.save();
	}

	public void setDefaultGroup(PermissionGroup group) {
		this.groups.setValue("default", group.getName());
		this.groups.save();
	}

	public PermissionPlayer getPlayer(String name) {
		UUID uuid = this.module.getManager().getUUID(name);
		if(uuid == null) {
			return null;
		}
		
		if(!this.players.contains("players." + uuid.toString())) {
			return null;
		}
		
		return new PermissionPlayer(uuid, this, this.players, this.module);
	}
	
	public void addIfMissing(String name) {
		UUID uuid = this.module.getManager().getUUID(name);
		if(uuid != null && !this.players.contains("players." + uuid.toString())) {
			List<String> groups = new ArrayList<String>();
			groups.add(this.getDefaultGroup() != null ? this.getDefaultGroup().getName() : "default");
			this.players.setValue("players." + uuid.toString() + ".groups", groups);
			this.players.save();
		}
	}
	
	public boolean isAutoRanked(String fromGroup) {
		return this.groups.contains("auto-rank." + fromGroup);
	}
	
	public String getAutoRank(String fromGroup) {
		return this.groups.getString("auto-rank." + fromGroup + ".to");
	}
	
	public int getRankMinutes(String fromGroup) {
		return this.groups.getInteger("auto-rank." + fromGroup + ".minutes");
	}
	
	public Storage getGroupFile() {
		return this.groups;
	}
	
	public Storage getPlayerFile() {
		return this.players;
	}
	
	public void save() {
		this.groups.save();
		this.players.save();
	}

}
