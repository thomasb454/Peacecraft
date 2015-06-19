package com.peacecraftec.bukkit.lots.core;

import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.module.Module;
import com.peacecraftec.redis.RedisDatabase;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class LotManager {
    private Module module;
    private RedisDatabase db;
    private Map<Integer, Lot> lots = new HashMap<Integer, Lot>();
    private Map<String, Town> towns = new HashMap<String, Town>();

    public LotManager(Module module) {
        this.module = module;
        this.db = new RedisDatabase("localhost");
        this.loadTowns();
        this.loadLots();
    }

    public void cleanup() {
        this.saveLots();
        this.saveTowns();
        this.lots.clear();
        this.towns.clear();
        this.db.cleanup();
        this.db = null;
        this.module = null;
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

    public void loadLots() {
        this.lots.clear();
        for(String key : this.db.keys("lots.*.id")) {
            key = key.substring(0, key.lastIndexOf("."));
            Town town = this.getTown(this.db.getString(key + ".townName"));
            if(town == null) {
                this.module.getLogger().warning("Town \"" + this.db.getString(key + ".townName") + "\" was not found in lot loading.");
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
            this.lots.put(id, new Lot(this.module, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner, builders));
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
        this.db.setValue("lots." + lot.getId() + ".owner", lot.getOwnerUUID() != null ? lot.getOwnerUUID().toString() : "");
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

    public List<Lot> getTownLots(String town) {
        List<Lot> ret = new ArrayList<Lot>();
        for(Lot lot : this.getLots()) {
            if(lot.getTown().getName().equals(town)) {
                ret.add(lot);
            }
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

    public int getNextLotId() {
        int curr = this.db.contains("lots.nextid") ? this.db.getInt("lots.nextid") : 0;
        this.db.setValue("lots.nextid", curr + 1);
        return curr + 1;
    }
}
