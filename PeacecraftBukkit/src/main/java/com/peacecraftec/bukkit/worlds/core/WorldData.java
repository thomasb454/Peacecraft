package com.peacecraftec.bukkit.worlds.core;

import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.UUID;

public class WorldData {

    private PeacecraftWorlds module;
    private String key;
    private Storage data;

    public WorldData(PeacecraftWorlds module, String key, File invFolder) {
        this.module = module;
        this.key = key;
        this.data = new YamlStorage(new File(invFolder, this.key + ".yml"));
        this.data.load();
    }

    public String getName() {
        try {
            UUID uuid = UUID.fromString(this.key);
            return this.module.getManager().getUsername(uuid);
        } catch(IllegalArgumentException e) {
            return this.key;
        }
    }

    public void load(Player player) {
        player.setHealth(this.data.getDouble("status.health", player.getMaxHealth()));
        player.setExhaustion(this.data.getFloat("status.hunger", 20));
        player.setLevel(this.data.getInteger("status.level", 0));
        player.setExp(this.data.getFloat("status.exp", 0));
        this.loadInv(player.getInventory());
        this.loadEnder(player.getEnderChest());
    }

    public void save(Player player) {
        this.data.setValue("status.health", player.getHealth());
        this.data.setValue("status.hunger", player.getExhaustion());
        this.data.setValue("status.level", player.getLevel());
        this.data.setValue("status.exp", player.getExp());
        this.saveInv(player.getInventory());
        this.saveEnder(player.getEnderChest());
    }

    private void loadInv(PlayerInventory inv) {
        inv.clear();
        inv.setArmorContents(new ItemStack[] { null, null, null, null });
        if(!this.data.contains("inv")) {
            return;
        }

        for(int slot = 0; slot < inv.getSize(); slot++) {
            inv.setItem(slot, this.loadItem("inv", slot));
        }

        inv.setHelmet(this.loadItem("inv", inv.getSize()));
        inv.setChestplate(this.loadItem("inv", inv.getSize() + 1));
        inv.setLeggings(this.loadItem("inv", inv.getSize() + 2));
        inv.setBoots(this.loadItem("inv", inv.getSize() + 3));
    }

    private void loadEnder(Inventory inv) {
        inv.clear();
        if(!this.data.contains("ender")) {
            return;
        }

        for(int slot = 0; slot < inv.getSize(); slot++) {
            inv.setItem(slot, this.loadItem("ender", slot));
        }
    }

    private ItemStack loadItem(String prefix, int slot) {
        if(!this.data.contains(prefix + ".core.slots." + slot)) {
            return null;
        }

        return ItemStack.deserialize(this.data.getMap(prefix + ".core.slots." + slot));
    }

    private void saveInv(PlayerInventory inv) {
        this.data.remove("inv");
        for(int slot = 0; slot < inv.getSize(); slot++) {
            this.saveItem("inv", slot, inv.getItem(slot));
        }

        this.saveItem("inv", inv.getSize(), inv.getHelmet());
        this.saveItem("inv", inv.getSize() + 1, inv.getChestplate());
        this.saveItem("inv", inv.getSize() + 2, inv.getLeggings());
        this.saveItem("inv", inv.getSize() + 3, inv.getBoots());
        this.data.save();
    }

    private void saveEnder(Inventory inv) {
        this.data.remove("ender");
        for(int slot = 0; slot < inv.getSize(); slot++) {
            this.saveItem("ender", slot, inv.getItem(slot));
        }

        this.data.save();
    }

    private void saveItem(String prefix, int slot, ItemStack item) {
        if(item == null) {
            return;
        }

        this.data.setValue(prefix + ".core.slots." + slot, item.serialize());
    }

}
