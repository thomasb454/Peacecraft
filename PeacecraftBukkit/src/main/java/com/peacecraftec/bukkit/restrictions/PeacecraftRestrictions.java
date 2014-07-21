package com.peacecraftec.bukkit.restrictions;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.bukkit.restrictions.listener.RestrictionsListener;

public class PeacecraftRestrictions extends Module {

	public PeacecraftRestrictions(String name, ModuleManager manager) {
		super(name, manager);
	}
	
	@Override
	public void onEnable() {
		this.loadConfig();
		this.getManager().getPermissionManager().register(this, RestrictionsPermissions.class);
		this.getManager().getEventManager().register(this, new RestrictionsListener(this));
	}

	@Override
	public void onDisable() {
	}
	
	@Override
	public void reload() {
		this.loadConfig();
	}
	
	private void loadConfig() {
		this.getConfig().load();
		for(World world : Bukkit.getServer().getWorlds()) {
			this.getConfig().applyDefault("border." + world.getName() + ".x", 4000);
			this.getConfig().applyDefault("border." + world.getName() + ".z", 4000);
		}
		
		this.getConfig().save();
	}
	
}
