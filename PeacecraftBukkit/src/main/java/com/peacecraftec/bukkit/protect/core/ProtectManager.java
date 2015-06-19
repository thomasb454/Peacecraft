package com.peacecraftec.bukkit.protect.core;

import com.peacecraftec.bukkit.protect.PeacecraftProtect;
import com.peacecraftec.redis.RedisSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class ProtectManager {
    private static final BlockFace NSEW[] = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

    private PeacecraftProtect module;

    public ProtectManager(PeacecraftProtect module) {
        this.module = module;
    }

    public Protection getProtection(Location loc) {
        String key = "protections." + loc.getWorld().getName().toLowerCase() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
        if(this.module.getManager().getDatabase().contains(key + ".owner")) {
            List<String> allowedPlayers = new ArrayList<String>();
            RedisSet set = this.module.getManager().getDatabase().getSet(key + ".allowedPlayers");
            if(set.exists()) {
                allowedPlayers.addAll(set.all());
            }

            return new Protection(loc, this.module.getManager().getDatabase().getString(key + ".owner"), allowedPlayers, Access.valueOf(this.module.getManager().getDatabase().getString(key + ".access")));
        }

        if(loc.getBlock().getType() == Material.CHEST) {
            for(BlockFace face : NSEW) {
                Block block = loc.getBlock().getRelative(face);
                if(block.getType() == Material.CHEST) {
                    String k = "protections." + block.getLocation().getWorld().getName().toLowerCase() + "-" + block.getLocation().getBlockX() + "-" + block.getLocation().getBlockY() + "-" + block.getLocation().getBlockZ();
                    if(this.module.getManager().getDatabase().contains(k + ".owner")) {
                        List<String> allowedPlayers = new ArrayList<String>();
                        RedisSet set = this.module.getManager().getDatabase().getSet(k + ".allowedPlayers");
                        if(set.exists()) {
                            allowedPlayers.addAll(set.all());
                        }

                        return new Protection(block.getLocation(), this.module.getManager().getDatabase().getString(k + ".owner"), allowedPlayers, Access.valueOf(this.module.getManager().getDatabase().getString(k + ".access")));
                    }
                }
            }
        }

        return null;
    }

    public void addProtection(Protection protection) {
        String key = "protections." + protection.getLocation().getWorld().getName().toLowerCase() + "-" + protection.getLocation().getBlockX() + "-" + protection.getLocation().getBlockY() + "-" + protection.getLocation().getBlockZ();
        this.module.getManager().getDatabase().setValue(key + ".owner", protection.getOwner());
        RedisSet set = this.module.getManager().getDatabase().getSet(key + ".allowedPlayers");
        for(String player : protection.getRawAllowedPlayers()) {
            set.add(player);
        }

        this.module.getManager().getDatabase().setValue(key + ".access", protection.getAccess().name());
    }

    public void removeProtection(Location loc) {
        String key = "protections." + loc.getWorld().getName().toLowerCase() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ();
        this.module.getManager().getDatabase().remove(key + ".owner");
        this.module.getManager().getDatabase().remove(key + ".allowedPlayers");
        this.module.getManager().getDatabase().remove(key + ".access");
    }

    public void addAllowedPlayer(Location loc, String player) {
        RedisSet set = this.module.getManager().getDatabase().getSet("protections." + loc.getWorld().getName().toLowerCase() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ() + ".allowedPlayers");
        set.add(player);
    }

    public void removeAllowedPlayer(Location loc, String player) {
        RedisSet set = this.module.getManager().getDatabase().getSet("protections." + loc.getWorld().getName().toLowerCase() + "-" + loc.getBlockX() + "-" + loc.getBlockY() + "-" + loc.getBlockZ() + ".allowedPlayers");
        set.remove(player);
    }
}
