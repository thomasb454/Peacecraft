package com.peacecraftec.bukkit.protect.core;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Protection {
    private Location loc;
    private String owner;
    private List<String> allowedPlayers;
    private Access access;

    public Protection(Location loc, String owner, List<String> allowedPlayers, Access access) {
        this.loc = loc;
        this.owner = owner;
        this.allowedPlayers = allowedPlayers;
        this.access = access;
    }

    public Location getLocation() {
        return this.loc;
    }

    public String getOwner() {
        return this.owner;
    }

    public List<String> getAllowedPlayers() {
        List<String> allowedPlayers = new ArrayList<String>(this.allowedPlayers);
        allowedPlayers.add(this.owner);
        return allowedPlayers;
    }

    public List<String> getRawAllowedPlayers() {
        return this.allowedPlayers;
    }

    public Access getAccess() {
        return this.access;
    }
}
