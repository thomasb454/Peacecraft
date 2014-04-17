package com.peacecraftec.bukkit.lots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import com.peacecraftec.bukkit.internal.hook.VaultAPI;
import com.peacecraftec.bukkit.lots.command.LotCommands;
import com.peacecraftec.bukkit.lots.listener.LotBlockListener;
import com.peacecraftec.bukkit.lots.listener.LotEntityListener;
import com.peacecraftec.bukkit.lots.listener.LotPlayerListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.redis.RedisDatabase;

public class PeacecraftLots extends Module {

	private RedisDatabase db;
	private Map<Integer, Lot> lots = new HashMap<Integer, Lot>();
	private Map<String, Town> towns = new HashMap<String, Town>();
	private int rentTask;
	
	public PeacecraftLots(ModuleManager manager) {
		super("Lots", manager);
	}
	
	@Override
	public void onEnable() {
		this.db = new RedisDatabase("localhost");
		this.loadConfig();
		this.loadTowns();
		this.loadLots();
		this.getManager().getPermissionManager().register(this, LotPermissions.class);
		this.getManager().getCommandManager().register(this, new LotCommands(this));
		this.getManager().getEventManager().register(this, new LotBlockListener(this));
		this.getManager().getEventManager().register(this, new LotEntityListener(this));
		this.getManager().getEventManager().register(this, new LotPlayerListener(this));
		this.startRentTask();
	}
	
	@Override
	public void onDisable() {
		this.stopRentTask();
		this.saveLots();
		this.saveTowns();
		this.lots.clear();
		this.towns.clear();
		this.db.cleanup();
		this.db = null;
	}
	
	@Override
	public void reload() {
		this.stopRentTask();
		this.saveLots();
		this.saveTowns();
		this.lots.clear();
		this.towns.clear();
		this.db.cleanup();
		this.db = new RedisDatabase("localhost");
		this.loadConfig();
		this.loadTowns();
		this.loadLots();
		this.startRentTask();
	}
	
	private void loadConfig() {
		this.getConfig().load();
		this.getConfig().applyDefault("max-town-lots-per-player", 1);
		this.getConfig().applyDefault("show-all-worlds-towns", true);
		this.getConfig().applyDefault("weeks-between-rent", 2);
		this.getConfig().applyDefault("next-rent", this.getWeeksAhead(getConfig().getInteger("weeks-between-rent", 2)));
		this.getConfig().save();
	}
	
	private void startRentTask() {
		if(this.rentTask != -1) {
			this.stopRentTask();
		}
		
		this.rentTask = this.getManager().getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				long time = getConfig().getLong("next-rent");
				if(System.currentTimeMillis() >= time) {
					getConfig().setValue("next-rent", getWeeksAhead(getConfig().getInteger("weeks-between-rent", 2)));
					getConfig().save();
					for(Lot lot : getLots()) {
						if(lot.getOwner() != null && !lot.getOwner().equals("") && lot.getRent() > 0) {
							VaultAPI.getEconomy().withdrawPlayer(lot.getOwner().toLowerCase(), lot.getWorld().getName().toLowerCase(), lot.getRent());
							PlayerSender player = getManager().getPlayerSender(lot.getOwner());
							if(player != null) {
								player.sendMessage("lot.lot-rent-collected", VaultAPI.getEconomy().format(lot.getRent()), lot.getId());
							}
						}
					}
					
					getManager().broadcastMessage("lots.collected-all-rent");
				}
			}
		}, 20, 20);
	}
	
	private void stopRentTask() {
		this.getManager().getScheduler().cancelTask(this, this.rentTask);
		this.rentTask = -1;
	}
	
	private long getWeeksAhead(int weeks) {
		return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7 * weeks);
	}
	
	public void loadLots() {
		this.lots.clear();
		for(String key : this.db.keys("lots.*.id")) {
			key = key.substring(0, key.lastIndexOf("."));
			Town town = this.getTown(this.db.getString(key + ".townName"));
			if(town == null) {
				this.getLogger().warning("Town \"" + this.db.getString(key + ".townName") + "\" was not found in lot loading.");
				continue;
			}
			
			int id = this.db.getInt(key + ".id");
			int x1 = this.db.getInt(key + ".x1");
			int y1 = this.db.getInt(key + ".y1");
			int z1 = this.db.getInt(key + ".z1");
			int x2 = this.db.getInt(key + ".x2");
			int y2 = this.db.getInt(key + ".y2");
			int z2 = this.db.getInt(key + ".z2");
			int size = this.db.getInt(key + ".size");
			double price = this.db.getDouble(key + ".price");
			boolean forsale = this.db.getBoolean(key + ".forsale");
			String owner = this.db.getString(key + ".owner");
			String builders = this.db.getString(key + ".builders");
			this.lots.put(id, new Lot(this, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner, builders));
		}
	}
	
	public void saveLots() {
		for(Lot lot : this.getLots()) {
			this.saveLot(lot);
		}
	}
	
	public void saveLot(Lot lot) {
		this.db.setValue("lots." + lot.getId() + ".townName", lot.getTown().getName());
		this.db.setValue("lots." + lot.getId() + ".id", lot.getId());
		this.db.setValue("lots." + lot.getId() + ".x1", lot.getX1());
		this.db.setValue("lots." + lot.getId() + ".y1", lot.getY1());
		this.db.setValue("lots." + lot.getId() + ".z1", lot.getZ1());
		this.db.setValue("lots." + lot.getId() + ".x2", lot.getX2());
		this.db.setValue("lots." + lot.getId() + ".y2", lot.getY2());
		this.db.setValue("lots." + lot.getId() + ".z2", lot.getZ2());
		this.db.setValue("lots." + lot.getId() + ".size", lot.getSize());
		this.db.setValue("lots." + lot.getId() + ".price", lot.getPrice());
		this.db.setValue("lots." + lot.getId() + ".forsale", lot.isForSale());
		this.db.setValue("lots." + lot.getId() + ".owner", lot.getUnconvertedOwner() != null ? lot.getUnconvertedOwner() : lot.getOwnerUUID() != null ? lot.getOwnerUUID().toString() : "");
		this.db.setValue("lots." + lot.getId() + ".builders", lot.getBuildersString());
	}
	
	public void deleteLot(Lot lot) {
		this.db.remove("lots." + lot.getId() + ".townName");
		this.db.remove("lots." + lot.getId() + ".id");
		this.db.remove("lots." + lot.getId() + ".x1");
		this.db.remove("lots." + lot.getId() + ".y1");
		this.db.remove("lots." + lot.getId() + ".z1");
		this.db.remove("lots." + lot.getId() + ".x2");
		this.db.remove("lots." + lot.getId() + ".y2");
		this.db.remove("lots." + lot.getId() + ".z2");
		this.db.remove("lots." + lot.getId() + ".size");
		this.db.remove("lots." + lot.getId() + ".price");
		this.db.remove("lots." + lot.getId() + ".forsale");
		this.db.remove("lots." + lot.getId() + ".owner");
		this.db.remove("lots." + lot.getId() + ".builders");
	}
	
	public void loadTowns() {
		this.towns.clear();
		for(String key : this.db.keys("towns.*.name")) {
			key = key.substring(0, key.lastIndexOf("."));
			String name = this.db.getString(key + ".name");
			String world = this.db.getString(key + ".world");
			String permission = this.db.contains(key + ".permission") ? this.db.getString(key + ".permission") : LotPermissions.OWN_LOT;
			int x1 = this.db.getInt(key + ".x1");
			int z1 = this.db.getInt(key + ".z1");
			int x2 = this.db.getInt(key + ".x2");
			int z2 = this.db.getInt(key + ".z2");
			double priceperblock = this.db.getDouble(key + ".priceperblock");
			double rentperblock = this.db.contains(key + ".rentperblock") ? this.db.getDouble(key + ".rentperblock") : 0;
			this.towns.put(name, new Town(name, world, permission, x1, z1, x2, z2, priceperblock, rentperblock));
		}
	}
	
	public void saveTowns() {
		for(Town town : this.getTowns()) {
			this.saveTown(town);
		}
	}
	
	public void saveTown(Town town) {
		this.db.setValue("towns." + town.getName() + ".name", town.getName());
		this.db.setValue("towns." + town.getName() + ".world", town.getWorld().getName());
		this.db.setValue("towns." + town.getName() + ".permission", town.getPermission());
		this.db.setValue("towns." + town.getName() + ".x1", town.getX1());
		this.db.setValue("towns." + town.getName() + ".z1", town.getZ1());
		this.db.setValue("towns." + town.getName() + ".x2", town.getX2());
		this.db.setValue("towns." + town.getName() + ".z2", town.getZ2());
		this.db.setValue("towns." + town.getName() + ".priceperblock", town.getPricePerBlock());
		this.db.setValue("towns." + town.getName() + ".rentperblock", town.getRentPerBlock());
	}
	
	public void deleteTown(Town town) {
		this.db.remove("towns." + town.getName() + ".name");
		this.db.remove("towns." + town.getName() + ".world");
		this.db.remove("towns." + town.getName() + ".permission");
		this.db.remove("towns." + town.getName() + ".x1");
		this.db.remove("towns." + town.getName() + ".z1");
		this.db.remove("towns." + town.getName() + ".x2");
		this.db.remove("towns." + town.getName() + ".z2");
		this.db.remove("towns." + town.getName() + ".priceperblock");
		this.db.remove("towns." + town.getName() + ".rentperblock");
	}
	
	public List<Lot> getTownLots(String town) {
		List<Lot> ret = new ArrayList<Lot>();
		for(Lot lot : this.getLots()) {
			if(lot.getTown().getName().equals(town)) ret.add(lot);
		}
		
		return ret;
	}
	
	public Collection<Lot> getLots() {
		return this.lots.values();
	}
	
	public List<Lot> getPlayerLots(String player) {
		List<Lot> ret = new ArrayList<Lot>();
		for(Lot lot : this.getLots()) {
			if(lot.getOwner() != null && lot.getOwner().equalsIgnoreCase(player)) ret.add(lot);
		}
		
		return ret;
	}
	
	public Lot getLot(int id) {
		return this.lots.get(id);
	}
	
	public Lot getLot(Location loc) {
		return this.getLot(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public Lot getLot(World world, int x, int y, int z) {
		for(Lot lot : this.getLots()) {
			if(lot.getWorld() != null && lot.getWorld().getName().equals(world.getName()) && x >= lot.getX1() && x <= lot.getX2() && y >= lot.getY1() && y <= lot.getY2() && z >= lot.getZ1() && z <= lot.getZ2()) {
				return lot;
			}
		}
		
		return null;
	}
	
	public void addLot(Lot lot) {
		this.lots.put(lot.getId(), lot);
		this.saveLot(lot);
	}
	
	public void removeLot(Lot lot) {
		this.lots.remove(lot.getId());
		this.deleteLot(lot);
	}
	
	public Collection<Town> getTowns() {
		return this.towns.values();
	}
	
	public Town looseGetTown(String name) {
		for(String key : this.towns.keySet()) {
			if(key.equalsIgnoreCase(name)) {
				return this.towns.get(key);
			}
		}
		
		return null;
	}
	
	public Town getTown(String name) {
		return this.towns.get(name);
	}
	
	public Town getTown(Location loc) {
		return this.getTown(loc.getWorld(), loc.getBlockX(), loc.getBlockZ());
	}
	
	public Town getTown(World world, int x, int z) {
		for(Town town : this.getTowns()) {
			if(town.getWorld() != null && town.getWorld().getName().equals(world.getName()) && x >= town.getX1() && x <= town.getX2() && z >= town.getZ1() && z <= town.getZ2()) {
				return town;
			}
		}
		
		return null;
	}
	
	public void addTown(Town town) {
		this.towns.put(town.getName(), town);
		this.saveTown(town);
	}
	
	public void removeTown(Town town) {
		this.towns.remove(town.getName());
		this.deleteTown(town);
	}
	
	public int getNextLotId() {
		int curr = this.db.contains("lots.nextid") ? this.db.getInt("lots.nextid") : 0;
		this.db.setValue("lots.nextid", curr + 1);
		return curr + 1;
	}
	
	// CONVERSION CODE
	public void convert(String name) {
		String player = name;
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			for(Lot lot : this.getLots()) {
				if(lot.getUnconvertedOwner() != null && lot.getUnconvertedOwner().equals(player)) {
					lot.setConvertedOwner(uuid);
				}
				
				if(lot.getUnconvertedBuilders() != null && lot.getUnconvertedBuilders().contains(player)) {
					lot.addConvertedBuilder(name, uuid);
				}
			}
		} else {
			this.getLogger().severe("Player " + name + " does not have a UUID to convert data to!");
		}
	}
	// END CONVERSION CODE
	
}
