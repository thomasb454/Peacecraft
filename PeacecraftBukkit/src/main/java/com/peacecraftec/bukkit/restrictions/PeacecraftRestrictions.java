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
			if(this.getConfig().contains("border." + world.getName() + ".x")) {
				int x = this.getConfig().getInteger("border." + world.getName() + ".x");
				int z = this.getConfig().getInteger("border." + world.getName() +".z");
				this.getConfig().setValue("border." + world.getName() + ".minX", -x);
				this.getConfig().setValue("border." + world.getName() + ".minZ", -z);
				this.getConfig().setValue("border." + world.getName() + ".maxX", x);
				this.getConfig().setValue("border." + world.getName() + ".maxZ", z);
				this.getConfig().remove("border." + world.getName() +".x");
				this.getConfig().remove("border." + world.getName() +".z");
			} else {
				this.getConfig().applyDefault("border." + world.getName() + ".minX", -4000);
				this.getConfig().applyDefault("border." + world.getName() + ".minZ", -4000);
				this.getConfig().applyDefault("border." + world.getName() + ".maxX", 4000);
				this.getConfig().applyDefault("border." + world.getName() + ".maxZ", 4000);
			}
		}
		
		this.getConfig().save();
	}
	
}
