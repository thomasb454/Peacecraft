package com.peacecraftec.bukkit.eco.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;

import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

public class EcoManager {

	private PeacecraftEco module;
	private Map<String, EcoWorld> worlds = new HashMap<String, EcoWorld>();
	
	public EcoManager(PeacecraftEco module) {
		this.module = module;
		this.reload();
	}
	
	public double getDefaultBalance() {
		return this.module.getConfig().getDouble("default-balance", 100);
	}
	
	public String getCurrencyFormat() {
		return this.module.getConfig().getString("currency-format", "$%s");
	}
	
	public String format(double amount) {
		return String.format(this.getCurrencyFormat(), (double) Math.round(amount * 100) / 100);
	}
	
	public boolean hasWorth(String material) {
		return this.module.getConfig().contains("worth." + material);
	}
	
	public double getWorth(String material) {
		return this.module.getConfig().getDouble("worth." + material, 0);
	}
	
	public EcoWorld getWorld(String name) {
		EcoWorld w = this.worlds.get(name);
		if(w == null) {
			String old = name;
			while(this.module.getConfig().contains("world-mirrors." + name)) {
				name = this.module.getConfig().getString("world-mirrors." + name);
			}
			
			Storage players = null;
			if(!name.equals(old)) {
				players = this.getWorld(name).getPlayerFile();
			} else {
				File dir = new File(new File(this.module.getDirectory(), "worlds"), name);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				players = new YamlStorage(new File(dir, "players.yml").getPath());
				players.load();
			}
			
			w = new EcoWorld(name, players, this.module);
			this.worlds.put(name, w);
		}
		
		return w;
	}
	
	public List<String> getStatsWorlds() {
		return this.module.getConfig().getList("stats-worlds", String.class, new ArrayList<String>());
	}
	
	public void reload() {
		File worldsFolder = new File(this.module.getDirectory(), "worlds");
		if(!worldsFolder.exists()) {
			worldsFolder.mkdirs();
		}
		
		this.module.getConfig().load();
		if(!this.module.getConfig().contains("world-mirrors")) {
			this.module.getConfig().applyDefault("world-mirrors.worldtoset", "worldtomirror");
		}
		
		this.module.getConfig().applyDefault("default-balance", 100);
		this.module.getConfig().applyDefault("currency-format", "$%s");
		this.module.getConfig().applyDefault("chest-shop-tax.buy", 10);
		this.module.getConfig().applyDefault("chest-shop-tax.sell", 10);
		if(!this.module.getConfig().contains("worth")) {
			this.module.getConfig().applyDefault("worth.stone", 1.0);
		}
		
		this.module.getConfig().applyDefault("stats-worlds", new ArrayList<String>());
		this.module.getConfig().applyDefault("mob-money.enabled", true);
		Map<String, Object> mobMoney = new LinkedHashMap<String, Object>();
		mobMoney.put(EntityType.BLAZE.name(), 3.0);
		mobMoney.put(EntityType.CAVE_SPIDER.name(), 2.0);
		mobMoney.put(EntityType.CREEPER.name(), 2.0);
		mobMoney.put(EntityType.ENDER_DRAGON.name(), 50.0);
		mobMoney.put(EntityType.GHAST.name(), 3.0);
		mobMoney.put(EntityType.MAGMA_CUBE.name(), 2.0);
		mobMoney.put(EntityType.PIG_ZOMBIE.name(), 2.0);
		mobMoney.put(EntityType.SILVERFISH.name(), 2.0);
		mobMoney.put(EntityType.SKELETON.name(), 2.0);
		mobMoney.put(EntityType.SPIDER.name(), 2.0);
		mobMoney.put(EntityType.WITCH.name(), 10.0);
		mobMoney.put(EntityType.WITHER.name(), 50.0);
		mobMoney.put("WITHER_SKELETON", 5.0);
		mobMoney.put(EntityType.ZOMBIE.name(), 2.0);
		this.module.getConfig().applyDefault("mob-money.mobs", mobMoney);
		this.module.getConfig().save();
		
		this.worlds.clear();
	}
	
}
