package com.peacecraftec.bukkit.lots.core;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Town {

    private String name;
    private String world;
    private String permission;
    private int x1;
    private int z1;
    private int x2;
    private int z2;
    private double priceperblock;
    private double rentperblock;

    public Town(String name, String world, String permission, int x1, int z1, int x2, int z2, double priceperblock, double rentperblock) {
        this.name = name;
        this.world = world;
        this.permission = permission;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.priceperblock = priceperblock;
        this.rentperblock = rentperblock;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        return Bukkit.getServer().getWorld(this.world);
    }

    public String getPermission() {
        return this.permission;
    }

    public void setPermission(String perm) {
        this.permission = perm;
    }

    public int getX1() {
        return this.x1;
    }

    public int getZ1() {
        return this.z1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getZ2() {
        return this.z2;
    }

    public void setBounds(int x1, int z1, int x2, int z2) {
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    public double getLotPrice(int size) {
        return size * this.priceperblock;
    }

    public double getPricePerBlock() {
        return this.priceperblock;
    }

    public void setPricePerBlock(double priceperblock) {
        this.priceperblock = priceperblock;
    }

    public double getLotRent(int size) {
        return size * this.rentperblock;
    }

    public double getRentPerBlock() {
        return this.rentperblock;
    }

    public void setRentPerBlock(double rentperblock) {
        this.rentperblock = rentperblock;
    }

}
