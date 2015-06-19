package com.peacecraftec.bukkit.eco.core;

import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EcoWorld {

    private String name;
    private Storage players;
    private PeacecraftEco module;

    public EcoWorld(String name, Storage players, PeacecraftEco module) {
        this.name = name;
        this.players = players;
        this.module = module;
    }

    public String getName() {
        return this.name;
    }

    public EcoPlayer getPlayer(String name) {
        UUID uuid = this.module.getManager().getUUID(name);
        String key = uuid != null ? uuid.toString() : name;
        if(!this.players.contains("players." + key)) {
            return null;
        }

        return new EcoPlayer(key, this, this.players, this.module);
    }

    public List<EcoPlayer> getPlayers() {
        List<EcoPlayer> ret = new ArrayList<EcoPlayer>();
        for(String key : this.players.getRelativeKeys("players", false)) {
            ret.add(new EcoPlayer(key, this, this.players, this.module));
        }

        return ret;
    }

    public void addIfMissing(String name) {
        UUID uuid = this.module.getManager().getUUID(name);
        String key = uuid != null ? uuid.toString() : name;
        if(!this.players.contains("players." + key)) {
            this.players.setValue("players." + key + ".balance", this.module.getEcoManager().getDefaultBalance());
            this.players.save();
        }
    }

    public Storage getPlayerFile() {
        return this.players;
    }

    public void save() {
        this.players.save();
    }
}
