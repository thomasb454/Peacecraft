package com.peacecraftec.bukkit.worlds.core;

import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldManager {

    private PeacecraftWorlds module;
    private Storage data;
    private Map<String, PeaceWorld> worlds = new HashMap<String, PeaceWorld>();

    public WorldManager(PeacecraftWorlds module) {
        this.module = module;
        this.reload();
    }

    public PeaceWorld getMirror(String name) {
        name = name.toLowerCase();
        if(this.module.getConfig().contains("mirrors." + name)) {
            return this.getWorld(this.module.getConfig().getString("mirrors." + name));
        }

        return null;
    }

    public boolean configContainsWorld(String name) {
        return this.data.contains("worlds." + name.toLowerCase());
    }

    public boolean isLoaded(String name) {
        name = name.toLowerCase();
        return Bukkit.getServer().getWorld(name) != null && this.isDataLoaded(name);
    }

    public boolean isDataLoaded(String name) {
        name = name.toLowerCase();
        return this.worlds.containsKey(name);
    }

    public List<String> getWorlds() {
        return this.data.getRelativeKeys("worlds", false);
    }

    public PeaceWorld getWorld(String name) {
        PeaceWorld w = this.worlds.get(name.toLowerCase());
        if(w == null) {
            if(!this.configContainsWorld(name) && Bukkit.getServer().getWorld(name) != null) {
                this.createEntry(Bukkit.getServer().getWorld(name));
                this.loadWorld(name);
                w = this.worlds.get(name.toLowerCase());
            } else {
                return null;
            }
        }

        return w;
    }

    public void createWorld(String name, long seed, String generator, Environment env, WorldType type, boolean genStructures, boolean providedSeed) {
        name = name.toLowerCase();
        World w = this.createBukkitWorld(name, seed, generator, env, type, genStructures, providedSeed);
        this.data.setValue("worlds." + name + ".seed", seed);
        if(generator != null) {
            this.data.setValue("worlds." + name + ".generator", generator);
        }

        this.data.setValue("worlds." + name + ".environment", env.name());
        this.data.setValue("worlds." + name + ".type", type.name());
        this.data.setValue("worlds." + name + ".generateStructures", genStructures);
        this.data.setValue("worlds." + name + ".pvp", false);
        this.data.setValue("worlds." + name + ".difficulty", Difficulty.EASY.name());
        this.data.setValue("worlds." + name + ".gamemode", GameMode.SURVIVAL.name());
        this.data.save();
        PeaceWorld world = new PeaceWorld(this.module, this, name, this.data, new File(this.module.getDirectory(), "inventories" + File.separator + name));
        world.setWorld(w);
        this.worlds.put(name, world);
    }

    private void createEntry(World world) {
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".seed", world.getSeed());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".environment", world.getEnvironment().name());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".type", world.getWorldType().name());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".generateStructures", world.canGenerateStructures());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".pvp", world.getPVP());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".difficulty", world.getDifficulty().name());
        this.data.setValue("worlds." + world.getName().toLowerCase() + ".gamemode", GameMode.SURVIVAL.name());
        this.data.save();
    }

    public boolean importWorld(String name) {
        if(Bukkit.getServer().getWorld(name) == null) {
            if(this.configContainsWorld(name)) {
                this.loadWorld(name);
                return true;
            }

            File f = new File(Bukkit.getServer().getWorldContainer(), name);
            if(!f.exists() || !f.isDirectory()) {
                return false;
            }

            WorldCreator creator = new WorldCreator(name);
            if(name.endsWith("_nether")) {
                creator.environment(Environment.NETHER);
            } else if(name.endsWith("_the_end")) {
                creator.environment(Environment.THE_END);
            }

            World w = Bukkit.getServer().createWorld(creator);
            name = name.toLowerCase();
            this.createEntry(w);
            PeaceWorld world = new PeaceWorld(this.module, this, name, this.data, new File(this.module.getDirectory(), "inventories" + File.separator + name));
            world.setWorld(w);
            this.worlds.put(name, world);
        }

        return true;
    }

    public void deleteWorld(String name) {
        name = name.toLowerCase();
        if(this.isLoaded(name)) {
            this.unloadWorld(name);
        }

        new File(Bukkit.getServer().getWorldContainer(), name).delete();
        this.data.remove("worlds." + name);
        this.data.save();
    }

    public void loadWorld(String name) {
        name = name.toLowerCase();
        PeaceWorld world = new PeaceWorld(this.module, this, name, this.data, new File(this.module.getDirectory(), "inventories" + File.separator + name));
        if(Bukkit.getServer().getWorld(name) == null) {
            this.loadBukkitWorld(world);
        }

        world.setWorld(Bukkit.getServer().getWorld(name));
        this.worlds.put(name, world);
    }

    public void unloadWorld(String name) {
        name = name.toLowerCase();
        World world = Bukkit.getServer().getWorld(name);
        if(world == null) {
            return;
        }

        for(Player player : world.getPlayers()) {
            Location spawn = Bukkit.getServer().getWorld(this.module.getManager().getDefaultWorld()).getSpawnLocation();
            if(this.module.getManager().isEnabled("Permissions")) {
                spawn = ((PeacecraftPerms) this.module.getManager().getModule("Permissions")).getPermsManager().getSpawn(name, player.getName());
            }

            player.teleport(spawn);
        }

        this.worlds.remove(name.toLowerCase());
        if(Bukkit.getServer().getWorld(name) != null) {
            Bukkit.getServer().unloadWorld(name, true);
        }
    }

    private World createBukkitWorld(String name, long seed, String generator, Environment env, WorldType type, boolean genStructures, boolean providedSeed) {
        WorldCreator c = new WorldCreator(name);
        if(providedSeed) {
            c.seed(seed);
        }

        if(generator != null) {
            c.generator(generator);
        }

        c.environment(env);
        c.type(type);
        c.generateStructures(genStructures);
        return c.createWorld();
    }

    private void loadBukkitWorld(PeaceWorld world) {
        WorldCreator c = new WorldCreator(world.getName());
        c.seed(world.getSeed());
        if(world.hasGenerator()) {
            c.generator(world.getGenerator());
        }

        c.environment(world.getEnvironment());
        c.type(world.getType());
        c.generateStructures(world.generateStructures());
        c.createWorld();
    }

    public void reload() {
        this.worlds.clear();
        this.module.getConfig().load();
        if(!this.module.getConfig().contains("mirrors")) {
            this.module.getConfig().applyDefault("mirrors.worldtoset", "worldtomirror");
        }

        this.module.getConfig().save();
        this.data = new YamlStorage(new File(this.module.getDirectory(), "core.yml").getPath());
        this.data.load();
        for(World world : Bukkit.getServer().getWorlds()) {
            if(!this.data.contains("worlds." + world.getName())) {
                this.createEntry(world);
            }
        }

        for(String name : this.data.getRelativeKeys("worlds", false)) {
            this.loadWorld(name);
        }
    }

}
