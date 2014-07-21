package com.peacecraftec.bukkit.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.peacecraftec.bukkit.core.command.CoreCommands;
import com.peacecraftec.bukkit.core.listener.CoreListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

public class PeacecraftCore extends Module {

	public PeacecraftCore(String name, ModuleManager manager) {
		super(name, manager);
	}
	
	@Override
	public void onEnable() {
		this.getManager().getCommandManager().register(this, new CoreCommands(this));
		this.getManager().getPermissionManager().register(this, CorePermissions.class);
		this.getManager().getEventManager().register(this, new CoreListener(this));
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.getManager().setUserPair(player.getUniqueId(), player.getName());
		}
	}

	@Override
	public void onDisable() {
	}
	
	@Override
	public void reload() {
	}
	
}
