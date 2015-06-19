package com.peacecraftec.bukkit.worlds.command;

import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.bukkit.worlds.WorldPermissions;
import com.peacecraftec.bukkit.worlds.core.PeaceWorld;
import com.peacecraftec.module.cmd.Command;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class WorldCommands {

    private PeacecraftWorlds module;

    public WorldCommands(PeacecraftWorlds module) {
        this.module = module;
    }

    @Command(aliases = { "worlds" }, desc = "worlds.command.worlds", usage = "<subcommand>", min = 1, permission = WorldPermissions.MANAGE)
    public void worlds(CommandSender sender, String command, String args[]) {
        if(args[0].equalsIgnoreCase("create")) {
            if(args.length < 3) {
                sendMessage(sender, "internal.usage", "/" + command + " create <name> <environment> [type] [seed] [genStructures] [generator]");
                return;
            }

            if(this.module.getWorldManager().configContainsWorld(args[1])) {
                sendMessage(sender, "worlds.already-exists");
                return;
            }

            String name = args[1];
            Environment env = null;
            try {
                env = Environment.valueOf(args[2].toUpperCase());
                if(env == null) {
                    sendMessage(sender, "worlds.invalid-environment");
                    return;
                }
            } catch(IllegalArgumentException e) {
                sendMessage(sender, "worlds.invalid-environment");
                return;
            }

            WorldType type = WorldType.NORMAL;
            if(args.length > 3) {
                try {
                    type = WorldType.valueOf(args[3].toUpperCase());
                    if(type == null || type == WorldType.VERSION_1_1) {
                        sendMessage(sender, "worlds.invalid-worldtype");
                        return;
                    }
                } catch(IllegalArgumentException e) {
                    sendMessage(sender, "worlds.invalid-worldtype");
                    return;
                }
            }

            boolean provSeed = false;
            long seed = 0;
            if(args.length > 4) {
                try {
                    seed = Long.parseLong(args[4]);
                    provSeed = true;
                } catch(NumberFormatException e) {
                    sendMessage(sender, "worlds.invalid-seed");
                    return;
                }
            }

            boolean genStructures = true;
            if(args.length > 5) {
                if(!args[5].equalsIgnoreCase("true") && !args[5].equalsIgnoreCase("false")) {
                    sendMessage(sender, "worlds.invalid-genstruct");
                    return;
                }

                genStructures = Boolean.parseBoolean(args[5]);
            }

            String generator = null;
            if(args.length > 6) {
                generator = args[6];
            }

            sendMessage(sender, "worlds.creating", name);
            this.module.getWorldManager().createWorld(name, seed, generator, env, type, genStructures, provSeed);
            sendMessage(sender, "worlds.created", name);
        } else if(args[0].equalsIgnoreCase("delete")) {
            if(args.length < 2) {
                sendMessage(sender, "internal.usage", "/" + command + " delete <world>");
                return;
            }

            if(!this.module.getWorldManager().configContainsWorld(args[1])) {
                sendMessage(sender, "internal.world-not-found");
                return;
            }

            this.module.getWorldManager().deleteWorld(args[1]);
            sendMessage(sender, "worlds.deleted", args[1]);
        } else if(args[0].equalsIgnoreCase("load")) {
            if(args.length < 2) {
                sendMessage(sender, "internal.usage", "/" + command + " load <world>");
                return;
            }

            if(!this.module.getWorldManager().configContainsWorld(args[1])) {
                sendMessage(sender, "internal.world-not-found");
                return;
            }

            if(this.module.getWorldManager().isLoaded(args[1])) {
                sendMessage(sender, "worlds.already-loaded");
                return;
            }

            this.module.getWorldManager().loadWorld(args[1]);
            sendMessage(sender, "worlds.loaded", args[1]);
        } else if(args[0].equalsIgnoreCase("import")) {
            if(args.length < 2) {
                sendMessage(sender, "internal.usage", "/" + command + " import <world>");
                return;
            }

            if(this.module.getWorldManager().isLoaded(args[1])) {
                sendMessage(sender, "worlds.already-loaded");
                return;
            }

            if(this.module.getWorldManager().importWorld(args[1])) {
                sendMessage(sender, "worlds.imported", args[1]);
            } else {
                sendMessage(sender, "internal.world-not-found", args[1]);
            }
        } else if(args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("setprop") || args[0].equalsIgnoreCase("tp")) {
            if(args.length < 2) {
                sendMessage(sender, "internal.usage", "/" + command + " " + args[0] + " <world> <args>");
                return;
            }

            PeaceWorld world = this.module.getWorldManager().getWorld(args[1]);
            if(world == null) {
                sendMessage(sender, "internal.world-not-found");
                return;
            }

            if(args[0].equalsIgnoreCase("unload")) {
                this.module.getWorldManager().unloadWorld(world.getName());
                sendMessage(sender, "worlds.unloaded", world.getName());
            } else if(args[0].equalsIgnoreCase("setprop")) {
                if(args.length < 4) {
                    sendMessage(sender, "internal.usage", "/" + command + " setprop <world> <key> <value>");
                    return;
                }

                String prop = args[2];
                String val = args[3];
                if(prop.equalsIgnoreCase("pvp")) {
                    if(!val.equalsIgnoreCase("true") && !val.equalsIgnoreCase("false")) {
                        sendMessage(sender, "worlds.invalid-pvp");
                        return;
                    }

                    boolean pvp = Boolean.valueOf(val);
                    world.setPvp(pvp);
                    sendMessage(sender, "worlds.set-pvp", world.getName(), pvp);
                } else if(prop.equalsIgnoreCase("difficulty")) {
                    Difficulty diff = null;
                    try {
                        diff = Difficulty.valueOf(val.toUpperCase());
                        if(diff == null) {
                            sendMessage(sender, "worlds.invalid-difficulty");
                            return;
                        }
                    } catch(IllegalArgumentException e) {
                        sendMessage(sender, "worlds.invalid-difficulty");
                        return;
                    }

                    world.setDifficulty(diff);
                    sendMessage(sender, "worlds.set-difficulty", world.getName(), diff.name());
                } else if(prop.equalsIgnoreCase("gamemode")) {
                    GameMode mode = null;
                    try {
                        mode = GameMode.valueOf(val.toUpperCase());
                        if(mode == null) {
                            sendMessage(sender, "worlds.invalid-gamemode");
                            return;
                        }
                    } catch(IllegalArgumentException e) {
                        sendMessage(sender, "worlds.invalid-gamemode");
                        return;
                    }

                    world.setGameMode(mode);
                    sendMessage(sender, "worlds.set-gamemode", world.getName(), mode.name());
                }
            } else if(args[0].equalsIgnoreCase("tp")) {
                if(!(sender instanceof Player)) {
                    sendMessage(sender, "internal.cannot-use-command");
                    return;
                }

                Location spawn = world.getWorld().getSpawnLocation();
                if(this.module.getManager().isEnabled("Permissions")) {
                    spawn = ((PeacecraftPerms) this.module.getManager().getModule("Permissions")).getPermsManager().getSpawn(world.getWorld().getName(), sender.getName());
                }

                ((Player) sender).teleport(spawn);
                sendMessage(sender, "worlds.teleported", world.getName());
            }
        } else if(args[0].equalsIgnoreCase("list")) {
            StringBuilder ret = new StringBuilder();
            for(String world : this.module.getWorldManager().getWorlds()) {
                if(ret.length() > 0) {
                    ret.append(ChatColor.WHITE);
                    ret.append(", ");
                }

                if(this.module.getWorldManager().isLoaded(world)) {
                    ret.append(ChatColor.GREEN);
                } else {
                    ret.append(ChatColor.RED);
                }

                ret.append(world);
            }

            sendMessage(sender, ret.toString());
        } else {
            sendMessage(sender, "internal.invalid-sub", "create, delete, load, import, unload, setprop, tp, list");
        }
    }

}
