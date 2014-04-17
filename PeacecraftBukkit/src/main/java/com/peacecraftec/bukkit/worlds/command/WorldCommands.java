package com.peacecraftec.bukkit.worlds.command;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;

import org.bukkit.entity.Player;

import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.bukkit.worlds.WorldPermissions;
import com.peacecraftec.bukkit.worlds.core.PeaceWorld;

public class WorldCommands extends Executor {

	private PeacecraftWorlds module;

	public WorldCommands(PeacecraftWorlds module) {
		this.module = module;
	}

	@Command(aliases = { "worlds" }, desc = "Manages worlds.", usage = "<subcommand>", min = 1, permission = WorldPermissions.MANAGE)
	public void worlds(CommandSender sender, String command, String args[]) {
		if(args[0].equalsIgnoreCase("create")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " create <name> <environment> [type] [seed] [genStructures] [generator]");
				return;
			}
			
			if(this.module.getWorldManager().configContainsWorld(args[1])) {
				sender.sendMessage("worlds.already-exists");
				return;
			}
			
			String name = args[1];
			Environment env = null;
			try {
				env = Environment.valueOf(args[2].toUpperCase());
				if(env == null) {
					sender.sendMessage("worlds.invalid-environment");
					return;
				}
			} catch(IllegalArgumentException e) {
				sender.sendMessage("worlds.invalid-environment");
				return;
			}
			
			WorldType type = WorldType.NORMAL;
			if(args.length > 3) {
				try {
					type = WorldType.valueOf(args[3].toUpperCase());
					if(type == null || type == WorldType.VERSION_1_1) {
						sender.sendMessage("worlds.invalid-worldtype");
						return;
					}
				} catch(IllegalArgumentException e) {
					sender.sendMessage("worlds.invalid-worldtype");
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
					sender.sendMessage("worlds.invalid-seed");
					return;
				}
			}
			
			boolean genStructures = true;
			if(args.length > 5) {
				if(!args[5].equalsIgnoreCase("true") && !args[5].equalsIgnoreCase("false")) {
					sender.sendMessage("worlds.invalid-genstruct");
					return;
				}
				
				genStructures = Boolean.parseBoolean(args[5]);
			}
			
			String generator = null;
			if(args.length > 6) {
				generator = args[6];
			}
			
			sender.sendMessage("worlds.creating", name);
			this.module.getWorldManager().createWorld(name, seed, generator, env, type, genStructures, provSeed);
			sender.sendMessage("worlds.created", name);
		} else if(args[0].equalsIgnoreCase("load")) {
			if(args.length < 2) {
				sender.sendMessage("generic.usage", "/" + command + " load <world>");
				return;
			}
			
			if(!this.module.getWorldManager().configContainsWorld(args[1])) {
				sender.sendMessage("generic.world-not-found");
				return;
			}
			
			if(this.module.getWorldManager().isLoaded(args[1])) {
				sender.sendMessage("worlds.already-loaded");
				return;
			}
			
			this.module.getWorldManager().loadWorld(args[1]);
			sender.sendMessage("worlds.loaded", args[1]);
		} else if(args[0].equalsIgnoreCase("import")) {
			if(args.length < 2) {
				sender.sendMessage("generic.usage", "/" + command + " import <world>");
				return;
			}
			
			if(this.module.getWorldManager().isLoaded(args[1])) {
				sender.sendMessage("worlds.already-loaded");
				return;
			}
			
			if(this.module.getWorldManager().importWorld(args[1])) {
				sender.sendMessage("worlds.imported", args[1]);
			} else {
				sender.sendMessage("generic.world-not-found", args[1]);
			}
		} else if(args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("setprop") || args[0].equalsIgnoreCase("tp")) {
			if(args.length < 2) {
				sender.sendMessage("generic.usage", "/" + command + " " + args[0] + " <world> <args>");
				return;
			}
			
			PeaceWorld world = this.module.getWorldManager().getWorld(args[1]);
			if(world == null) {
				sender.sendMessage("generic.world-not-found");
				return;
			}
			
			if(args[0].equalsIgnoreCase("unload")) {
				this.module.getWorldManager().unloadWorld(world.getName());
				sender.sendMessage("worlds.unloaded", world.getName());
			} else if(args[0].equalsIgnoreCase("setprop")) {
				if(args.length < 4) {
					sender.sendMessage("generic.usage", "/" + command + " setprop <world> <key> <value>");
					return;
				}
				
				String prop = args[2];
				String val = args[3];
				if(prop.equalsIgnoreCase("pvp")) {
					if(!val.equalsIgnoreCase("true") && !val.equalsIgnoreCase("false")) {
						sender.sendMessage("worlds.invalid-pvp");
						return;
					}
					
					boolean pvp = Boolean.valueOf(val);
					world.setPvp(pvp);
					sender.sendMessage("worlds.set-pvp", world.getName(), pvp);
				} else if(prop.equalsIgnoreCase("difficulty")) {
					Difficulty diff = null;
					try {
						diff = Difficulty.valueOf(val.toUpperCase());
						if(diff == null) {
							sender.sendMessage("worlds.invalid-difficulty");
							return;
						}
					} catch(IllegalArgumentException e) {
						sender.sendMessage("worlds.invalid-difficulty");
						return;
					}
					
					world.setDifficulty(diff);
					sender.sendMessage("worlds.set-difficulty", world.getName(), diff.name());
				} else if(prop.equalsIgnoreCase("gamemode")) {
					GameMode mode = null;
					try {
						mode = GameMode.valueOf(val.toUpperCase());
						if(mode == null) {
							sender.sendMessage("worlds.invalid-gamemode");
							return;
						}
					} catch(IllegalArgumentException e) {
						sender.sendMessage("worlds.invalid-gamemode");
						return;
					}
					
					world.setGameMode(mode);
					sender.sendMessage("worlds.set-gamemode", world.getName(), mode.name());
				}
			} else if(args[0].equalsIgnoreCase("tp")) {
				if(!(sender instanceof PlayerSender)) {
					sender.sendMessage("generic.cannot-use-command");
					return;
				}
				
				Location spawn = world.getWorld().getSpawnLocation();
				if(this.module.getManager().isEnabled("Permissions")) {
					spawn = ((PeacecraftPerms) this.module.getManager().getModule("Permissions")).getPermsManager().getSpawn(world.getWorld().getName(), sender.getName());
				}
				
				((Player) BukkitCommandSender.unwrap(sender)).teleport(spawn);
				sender.sendMessage("worlds.teleported", world.getName());
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
			
			sender.sendMessage(ret.toString());
		} else {
			sender.sendMessage("generic.invalid-sub", "create, load, import, unload, setprop, tp, list");
		}
	}

}
