package com.peacecraftec.bukkit.perms.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

public class PermissionManager {

	private Map<String, PermissionWorld> worlds = new HashMap<String, PermissionWorld>();
	private PeacecraftPerms module;
	
	public PermissionManager(PeacecraftPerms module) {
		this.module = module;
		this.reload();
	}
	
	public PermissionWorld getWorld(String name) {
		PermissionWorld w = this.worlds.get(name);
		if(w == null) {
			String gname = name;
			String uname = name;
			while(this.module.getConfig().contains("world-mirrors.groups." + gname)) {
				gname = this.module.getConfig().getString("world-mirrors.groups." + gname);
			}
			
			while(this.module.getConfig().contains("world-mirrors.users." + uname)) {
				uname = this.module.getConfig().getString("world-mirrors.users." + uname);
			}
			
			Storage groups = null;
			if(!gname.equals(name)) {
				groups = this.getWorld(gname).getGroupFile();
			} else {
				File gdir = new File(new File(this.module.getDirectory(), "worlds"), gname);
				if(!gdir.exists()) {
					gdir.mkdirs();
				}
				
				File grp = new File(gdir, "groups.yml");
				boolean makeGrp = false;
				if(!grp.exists()) {
					makeGrp = true;
				}
				
				groups = new YamlStorage(grp.getPath());
				groups.load();
				if(makeGrp) {
					groups.setValue("default", "default");
					groups.setValue("auto-rank.example.to", "asdfgroup");
					groups.setValue("auto-rank.example.minutes", 30);
					groups.setValue("groups.default.info.prefix", "&e");
					groups.setValue("groups.default.info.suffix", "");
					List<String> perms = new ArrayList<String>();
					perms.add("perms.example");
					groups.setValue("groups.default.permissions", perms);
					
					groups.setValue("groups.next.info.prefix", "&a");
					groups.setValue("groups.next.info.suffix", "");
					List<String> inherit = new ArrayList<String>();
					inherit.add("default");
					groups.setValue("groups.next.inherits", inherit);
					groups.setValue("groups.next.permissions", perms);
					groups.save();
				}
			}
			
			Storage users = null;
			if(!uname.equals(name)) {
				users = this.getWorld(uname).getPlayerFile();
			} else {
				File udir = new File(new File(this.module.getDirectory(), "worlds"), uname);
				if(!udir.exists()) {
					udir.mkdirs();
				}
				
				File plrs = new File(udir, "players.yml");
				users = new YamlStorage(plrs.getPath());
				users.load();
			}
			
			w = new PermissionWorld(name, gname, groups, users, this.module);
			this.worlds.put(name, w);
		}
		
		return w;
	}
	
	public PermissionWorld getWorldIfGroups(String name) {
		File gdir = new File(new File(this.module.getDirectory(), "worlds"), name);
		if(!gdir.exists()) {
			return null;
		}
		
		return this.getWorld(name);
	}
	
	public Location getSpawn(String world, String player) {
		PermissionWorld w = this.getWorld(world);
		w.addIfMissing(player);
		for(PermissionGroup group : w.getPlayer(player).getGroups()) {
			if(this.hasGroupSpawn(world, group.getName())) {
				return this.getGroupSpawn(world, group.getName());
			}
		}
		
		return this.getGlobalSpawn(world);
	}
	
	public boolean hasGlobalSpawn(String world) {
		world = world.toLowerCase();
		return this.module.getConfig().contains("spawns." + world);
	}
	
	public Location getGlobalSpawn(String world) {
		if(!this.hasGlobalSpawn(world)) {
			World w = Bukkit.getServer().getWorld(world);
			if(w != null) {
				this.setGlobalSpawn(w.getName(), w.getSpawnLocation());
			}
		}
		
		String spawn = this.module.getConfig().getString("spawns." + world);
		try {
			String split[] = spawn.split(",");
			if(split.length > 3) {
				return new Location(Bukkit.getServer().getWorld(world), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
			} else {
				return new Location(Bukkit.getServer().getWorld(world), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
			}
		} catch(Exception e) {
			this.module.getLogger().warning("Invalid global spawn for world \"" + world + "\"!");
		}
		
		return null;
	}
	
	public boolean hasGroupSpawn(String world, String group) {
		return this.module.getConfig().contains("group-spawns." + world.toLowerCase() + "." + group.toLowerCase());
	}
	
	public Location getGroupSpawn(String world, String group) {
		if(!this.hasGroupSpawn(world, group)) {
			return null;
		}
		
		String spawn = this.module.getConfig().getString("group-spawns." + world.toLowerCase() + "." + group.toLowerCase());
		try {
			String split[] = spawn.split(",");
			if(split.length > 3) {
				return new Location(Bukkit.getServer().getWorld(world), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
			} else {
				return new Location(Bukkit.getServer().getWorld(world), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
			}
		} catch(Exception e) {
			this.module.getLogger().warning("Invalid spawn for group \"" + group + "\" in world \"" + world + "\"!");
		}
		
		return null;
	}
	
	public void setGroupSpawn(String world, String group, Location spawn) {
		this.module.getConfig().setValue("group-spawns." + world.toLowerCase() + "." + group.toLowerCase(), spawn.getX() + "," + spawn.getY() + "," + spawn.getZ() + "," + spawn.getYaw() + "," + spawn.getPitch());
		this.module.getConfig().save();
	}
	
	public void setGlobalSpawn(String world, Location spawn) {
		this.module.getConfig().setValue("spawns." + world, spawn.getX() + "," + spawn.getY() + "," + spawn.getZ() + "," + spawn.getYaw() + "," + spawn.getPitch());
		this.module.getConfig().save();
	}
	
	public int getTotalPlayers() {
		return this.module.getConfig().getInteger("data.total-players");
	}
	
	public void playerJoined() {
		this.module.getConfig().setValue("data.total-players", this.getTotalPlayers() + 1);
		this.module.getConfig().save();
	}
	
	public void reload() {
		File worldsFolder = new File(this.module.getDirectory(), "worlds");
		if(!worldsFolder.exists()) {
			worldsFolder.mkdirs();
		}
		
		this.module.getConfig().load();
		if(!this.module.getConfig().contains("world-mirrors.groups")) {
			this.module.getConfig().applyDefault("world-mirrors.groups.worldtoset", "worldtomirror");
		}
		
		if(!this.module.getConfig().contains("world-mirrors.users")) {
			this.module.getConfig().applyDefault("world-mirrors.users.worldtoset", "worldtomirror");
		}
		
		this.module.getConfig().applyDefault("data.total-players", 0);
		this.module.getConfig().save();
		
		this.worlds.clear();
	}
	
}
