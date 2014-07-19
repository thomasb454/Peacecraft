package com.peacecraftec.bukkit.worlds.core;

import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.bukkit.worlds.WorldPermissions;
import com.peacecraftec.storage.Storage;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class PeaceWorld {

	private PeacecraftWorlds module;
	private WorldManager manager;
	private String name;
	private Storage data;
	private World world;
	private File invFolder;
	
	public PeaceWorld(PeacecraftWorlds module, WorldManager manager, String name, Storage data, File invFolder) {
		this.module = module;
		this.manager = manager;
		this.name = name;
		this.data = data;
		this.invFolder = invFolder;
	}
	
	protected void setWorld(World world) {
		this.world = world;
		this.data.setValue("worlds." + this.name + ".seed", this.world.getSeed());
		this.data.save();
		this.world.setPVP(this.data.getBoolean("worlds." + this.name + ".pvp", false));
		this.world.setDifficulty(Difficulty.valueOf(this.data.getString("worlds." + this.name + ".difficulty", Difficulty.NORMAL.name())));
	}
	
	public String getName() {
		return this.name;
	}
	
	public World getWorld() {
		return this.world;
	}

	public long getSeed() {
		return this.data.getLong("worlds." + this.name + ".seed");
	}

	public boolean hasGenerator() {
		return this.getGenerator() != null;
	}

	public String getGenerator() {
		return this.data.contains("worlds." + this.name + ".generator") ? this.data.getString("worlds." + this.name + ".generator") : null;
	}

	public Environment getEnvironment() {
		return Environment.valueOf(this.data.getString("worlds." + this.name + ".environment", Environment.NORMAL.name()));
	}

	public WorldType getType() {
		return WorldType.valueOf(this.data.getString("worlds." + this.name + ".type", WorldType.NORMAL.name()));
	}

	public boolean generateStructures() {
		return this.data.getBoolean("worlds." + this.name + ".generateStructures", true);
	}
	
	public boolean getPvp() {
		return this.world.getPVP();
	}
	
	public void setPvp(boolean pvp) {
		this.world.setPVP(pvp);
		this.data.setValue("worlds." + this.name + ".pvp", pvp);
		this.data.save();
	}
	
	public Difficulty getDifficulty() {
		return this.world.getDifficulty();
	}
	
	public void setDifficulty(Difficulty diff) {
		this.world.setDifficulty(diff);
		this.data.setValue("worlds." + this.name + ".difficulty", diff.name());
		this.data.save();
	}
	
	public GameMode getGameMode() {
		return GameMode.valueOf(this.data.getString("worlds." + this.name + ".gamemode", GameMode.SURVIVAL.name()));
	}
	
	public void setGameMode(GameMode mode) {
		this.data.setValue("worlds." + this.name + ".gamemode", mode.name());
		this.data.save();
		for(Player player : this.world.getPlayers()) {
			if(player.getGameMode() != this.getGameMode() && !player.hasPermission(WorldPermissions.GM_OVERRIDE)) {
				player.setGameMode(this.getGameMode());
			}
		}
	}
	
	public WorldData getData(String player) {
		PeaceWorld mirror = this.manager.getMirror(this.name);
		if(mirror != null) {
			return mirror.getData(player);
		} else {
			UUID uuid = this.module.getManager().getUUID(player);
			String key = uuid != null ? uuid.toString() : player.toLowerCase();
			return new WorldData(this.module, key, this.invFolder);
		}
	}
}
