package com.peacecraftec.bukkit.lots.core;

import com.peacecraftec.module.Module;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lot {

    private Module module;
    private Town town;
    private int id;
    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;
    private int size;
    private double price;
    private boolean forsale;
    private UUID owner;
    private List<UUID> builders;

    public Lot(Module module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, String owner, String builders) {
        this(module, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner, new ArrayList<UUID>());
        this.buildBuilderList(builders);
    }

    public Lot(Module module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, UUID owner, List<UUID> builders) {
        this(module, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner != null ? owner.toString() : null, builders);
    }

    private Lot(Module module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, String owner, List<UUID> builders) {
        this.module = module;
        this.town = town;
        this.id = id;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.size = size;
        this.price = price;
        this.forsale = forsale;
        this.builders = builders;
    }

    public Town getTown() {
        return this.town;
    }

    public int getId() {
        return this.id;
    }

    public int getSize() {
        return this.size;
    }

    public double getPrice() {
        if(this.price <= 0) {
            return this.town.getLotPrice(this.size / (this.y2 - this.y1 + 1));
        }

        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRent() {
        return this.town.getLotRent(this.getSize());
    }

    public boolean isForSale() {
        return this.forsale;
    }

    public void setForSale(boolean forsale) {
        this.forsale = forsale;
    }

    public String getOwner() {
        if(this.owner == null) {
            return "";
        }

        return this.module.getManager().getUsername(this.owner);
    }

    public UUID getOwnerUUID() {
        return this.owner;
    }

    public void setOwner(String owner) {
        if(owner == null) {
            owner = "";
        }

        this.owner = this.module.getManager().getUUID(owner);
    }

    public String getBuildersString() {
        return this.buildBuilderString();
    }

    public List<String> getBuilders() {
        List<String> ret = new ArrayList<String>();
        for(UUID uuid : this.builders) {
            ret.add(this.module.getManager().getUsername(uuid));
        }

        return ret;
    }

    public void addBuilder(String builder) {
        UUID uuid = this.module.getManager().getUUID(builder);
        if(uuid != null) {
            this.builders.add(uuid);
        }
    }

    public void removeBuilder(String builder) {
        UUID uuid = this.module.getManager().getUUID(builder);
        if(uuid != null) {
            this.builders.remove(uuid);
        }
    }

    public World getWorld() {
        return this.town.getWorld();
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getZ1() {
        return this.z1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public int getZ2() {
        return this.z2;
    }

    private void buildBuilderList(String builders) {
        String parts[] = builders.split(":");
        this.builders = new ArrayList<UUID>();
        for(String uuid : parts) {
            try {
                this.builders.add(UUID.fromString(uuid));
            } catch(IllegalArgumentException e) {
            }
        }
    }

    private String buildBuilderString() {
        StringBuilder build = new StringBuilder();
        for(UUID builder : this.builders) {
            if(build.length() > 0) {
                build.append(":");
            }

            build.append(builder.toString());
        }

        return build.toString();
    }

    public boolean canBuild(Player player) {
        return player.getUniqueId().equals(this.owner) || this.getBuilders().contains(player.getName()) || this.getBuilders().contains("#everyone");
    }

    public boolean canInteract(Player player) {
        return this.canBuild(player) || this.getBuilders().contains("#everyoneinteract");
    }

    public void setBounds(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

}
