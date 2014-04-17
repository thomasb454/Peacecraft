package com.peacecraftec.bukkit.worlds.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;

import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.bukkit.worlds.WorldPermissions;
import com.peacecraftec.bukkit.worlds.core.PeaceWorld;
import com.peacecraftec.bukkit.worlds.core.WorldData;

public class WorldListener implements Listener {

	private PeacecraftWorlds module;
	
	public WorldListener(PeacecraftWorlds module) {
		this.module = module;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getPlayer().getLocation().getY() == 0) return;
		PeaceWorld world = this.module.getWorldManager().getWorld(event.getPlayer().getWorld().getName());
		GameMode mode = world.getGameMode();
		if(event.getPlayer().getGameMode() != mode && !event.getPlayer().hasPermission(WorldPermissions.GM_OVERRIDE)) {
			event.getPlayer().setGameMode(mode);
		}
		
		WorldData data = world.getData(event.getPlayer().getName());
		data.load(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		PeaceWorld world = this.module.getWorldManager().getWorld(event.getPlayer().getWorld().getName());
		WorldData data = world.getData(event.getPlayer().getName());
		data.save(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		PeaceWorld world = this.module.getWorldManager().getWorld(event.getPlayer().getWorld().getName());
		GameMode mode = world.getGameMode();
		if(event.getPlayer().getGameMode() != mode && !event.getPlayer().hasPermission(WorldPermissions.GM_OVERRIDE)) {
			event.getPlayer().setGameMode(mode);
		}
		
		PeaceWorld old = this.module.getWorldManager().getWorld(event.getFrom().getName());
		WorldData oldData = old.getData(event.getPlayer().getName());
		oldData.save(event.getPlayer());
		WorldData data = world.getData(event.getPlayer().getName());
		data.load(event.getPlayer());
	}
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		PeaceWorld world = this.module.getWorldManager().getWorld(event.getWorld().getName());
		for(Player player : event.getWorld().getPlayers()) {
			world.getData(player.getName()).save(player);
		}
	}
	
}
