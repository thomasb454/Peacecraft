package com.peacecraftec.bukkit.worlds;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.peacecraftec.bukkit.worlds.command.WorldCommands;
import com.peacecraftec.bukkit.worlds.core.PeaceWorld;
import com.peacecraftec.bukkit.worlds.core.WorldData;
import com.peacecraftec.bukkit.worlds.core.WorldManager;
import com.peacecraftec.bukkit.worlds.listener.WorldListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

public class PeacecraftWorlds extends Module {

	private WorldManager manager;

	public PeacecraftWorlds(ModuleManager manager) {
		super("Worlds", manager);
	}

	@Override
	public void onEnable() {
		this.manager = new WorldManager(this);
		this.getManager().getPermissionManager().register(this, WorldPermissions.class);
		this.getManager().getCommandManager().register(this, new WorldCommands(this));
		this.getManager().getEventManager().register(this, new WorldListener(this));
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			PeaceWorld world = this.getWorldManager().getWorld(player.getWorld().getName());
			GameMode mode = world.getGameMode();
			if(player.getGameMode() != mode && !player.hasPermission(WorldPermissions.GM_OVERRIDE)) {
				player.setGameMode(mode);
			}

			WorldData data = world.getData(player.getName());
			data.load(player);
		}
	}

	@Override
	public void onDisable() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			WorldData data = this.getWorldManager().getWorld(player.getWorld().getName()).getData(player.getName());
			data.save(player);
		}
		
		this.manager = null;
	}

	@Override
	public void reload() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			WorldData data = this.getWorldManager().getWorld(player.getWorld().getName()).getData(player.getName());
			data.save(player);
		}
		
		this.getWorldManager().reload();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			WorldData data = this.getWorldManager().getWorld(player.getWorld().getName()).getData(player.getName());
			data.load(player);
		}
	}

	public WorldManager getWorldManager() {
		return this.manager;
	}

}
