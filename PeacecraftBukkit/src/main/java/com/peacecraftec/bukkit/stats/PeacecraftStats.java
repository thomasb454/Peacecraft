package com.peacecraftec.bukkit.stats;

import org.bukkit.Bukkit;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.bukkit.stats.command.StatsCommands;
import com.peacecraftec.bukkit.stats.listener.StatsListener;
import com.peacecraftec.web.stats.StatSystem;
import com.peacecraftec.web.stats.StatsFactory;

public class PeacecraftStats extends Module {
	
	private StatSystem stats;
	
	public PeacecraftStats(ModuleManager manager) {
		super("Stats", manager);
	}

	@Override
	public void onEnable() {
		this.stats = StatsFactory.create(this.getManager(), "bukkit");
		this.getManager().getPermissionManager().register(this, StatsPermissions.class);
		this.getManager().getCommandManager().register(this, new StatsCommands(this));
		this.getManager().getEventManager().register(this, new StatsListener(this));
	}
	
	@Override
	public void onDisable() {
		this.stats.cleanup();
		this.stats = null;
	}

	@Override
	public void reload() {
		this.stats.cleanup();
		this.stats = null;
		this.stats = StatsFactory.create(this.getManager(), "bukkit");
	}
	
	public StatSystem getStatSystem() {
		return this.stats;
	}
	
	public long getPlayTime(String player) {
		long ret = this.stats.getLong("login.playtime", player);
		if(Bukkit.getServer().getPlayerExact(player) != null) {
			ret += System.currentTimeMillis() - this.stats.getLong("login.last_login", player);
		}
		
		return ret;
	}
	
}
