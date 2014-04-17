package com.peacecraftec.bukkit.extras;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.peacecraftec.bukkit.extras.listener.AutoCraftListener;
import com.peacecraftec.bukkit.extras.listener.PistonChestListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

public class PeacecraftExtras extends Module {

	public PeacecraftExtras(ModuleManager manager) {
		super("Extras", manager);
	}
	
	@Override
	public void onEnable() {
		this.loadConfig();
		this.getManager().getEventManager().register(this, new PistonChestListener(this));
		this.getManager().getEventManager().register(this, new AutoCraftListener(this));
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
		List<String> disabled = new ArrayList<String>();
		disabled.add(Material.BEDROCK.name());
		disabled.add(Material.MOB_SPAWNER.name());
		disabled.add(Material.ENDER_PORTAL_FRAME.name());
		disabled.add(Material.ENDER_PORTAL.name());
		disabled.add(Material.PORTAL.name());
		
		this.getConfig().applyDefault("pistonchest.disabled", disabled);
		this.getConfig().save();
	}
	
}
