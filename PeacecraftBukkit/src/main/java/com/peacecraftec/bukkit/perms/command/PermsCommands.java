package com.peacecraftec.bukkit.perms.command;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.perms.PermsPermissions;
import com.peacecraftec.bukkit.perms.core.PermissionGroup;
import com.peacecraftec.bukkit.perms.core.PermissionPlayer;
import com.peacecraftec.bukkit.perms.core.PermissionWorld;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PermsCommands extends Executor {
	
	private PeacecraftPerms module;
	
	public PermsCommands(PeacecraftPerms module) {
		this.module = module;
	}
	
	@Command(aliases = {"permplayer", "pplayer"}, desc = "Manages player permissions.", usage = "<player> <subcommand>", min = 2, permission = PermsPermissions.MANAGE)
	public void permplayer(CommandSender sender, String command, String args[]) {
		String world = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
		PermissionPlayer player = this.module.getPermsManager().getWorld(world).getPlayer(args[0]);
		if(player == null) {
			sender.sendMessage("generic.player-not-found");
			return;
		}
		
		if(args[1].equalsIgnoreCase("addperm")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> addperm <permission>");
				return;
			}
			
			if(player.addPermission(args[2])) {
				sender.sendMessage("perms.perm-granted", args[2], player.getName());
			} else {
				sender.sendMessage("perms.perm-already-has", player.getName());
			}
		} else if(args[1].equalsIgnoreCase("delperm")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> delperm <permission>");
				return;
			}
			
			if(player.removePermission(args[2])) {
				sender.sendMessage("perms.perm-removed", args[2], player.getName());
			} else {
				sender.sendMessage("perms.perm-doesnt-have", player.getName());
			}
		} else if(args[1].equalsIgnoreCase("addgroup")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> addgroup <group>");
				return;
			}
			
			if(this.module.getPermsManager().getWorld(world).getGroup(args[2]) == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}
			
			if(player.addGroup(args[2])) {
				sender.sendMessage("perms.added-to-group", player.getName(), args[2]);
			} else {
				sender.sendMessage("perms.already-in-group", player.getName());
			}
		} else if(args[1].equalsIgnoreCase("removegroup")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> removegroup <group>");
				return;
			}
			
			if(this.module.getPermsManager().getWorld(world).getGroup(args[2]) == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}
			
			if(player.removeGroup(args[2])) {
				sender.sendMessage("perms.removed-from-group", player.getName(), args[2]);
			} else {
				sender.sendMessage("perms.not-in-group", player.getName());
			}
		} else if(args[1].equalsIgnoreCase("swapgroup")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> swapgroup [from] <to>");
				return;
			}
			
			String from = null;
			String to = null;
			if(args.length > 3) {
				from = args[2].toLowerCase();
				to = args[3].toLowerCase();
			} else {
				to = args[2].toLowerCase();
			}
			
			if(from != null && this.module.getPermsManager().getWorld(world).getGroup(from) == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}
			
			if(from != null && !player.getGroupNames().contains(from)) {
				sender.sendMessage("perms.not-in-group", player.getName());
				return;
			}
			
			if(this.module.getPermsManager().getWorld(world).getGroup(to) == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}
			
			if(player.getGroupNames().contains(to)) {
				sender.sendMessage("perms.already-in-group", player.getName());
				return;
			}
			
			if(from != null) {
				player.removeGroup(from);
				player.addGroup(to);
				sender.sendMessage("perms.moved-from-group", player.getName(), from, to);
			} else {
				for(String group : player.getGroupNames()) {
					player.removeGroup(group);
				}
				
				player.addGroup(to);
				sender.sendMessage("perms.moved-to-group", player.getName(), to);
			}
		} else if(args[1].equalsIgnoreCase("setprefix")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> setprefix <prefix/remove>");
				return;
			}
			
			args[2] = args[2].replaceAll("_", " ");
			if(args[2].equalsIgnoreCase("remove")) {
				player.removePrefix();
				sender.sendMessage("perms.prefix-removed", player.getName());
			} else {
				player.setPrefix(args[2]);
				sender.sendMessage("perms.prefix-put", player.getName(), ChatColor.translateAlternateColorCodes('&', args[2]));
			}
		} else if(args[1].equalsIgnoreCase("setsuffix")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/" + command + " <player> setsuffix <suffix/remove>");
				return;
			}
			
			args[2] = args[2].replaceAll("_", " ");
			if(args[2].equalsIgnoreCase("remove")) {
				player.removeSuffix();
				sender.sendMessage("perms.suffix-removed", player.getName());
			} else {
				player.setSuffix(args[2]);
				sender.sendMessage("perms.suffix-put", player.getName(), ChatColor.translateAlternateColorCodes('&', args[2]));
			}
		} else {
			sender.sendMessage("generic.invalid-sub", "addperm, delperm, addgroup, removegroup, swapgroup, setprefix, setsuffix");
		}
	}
	
	@Command(aliases = {"permgroup", "pgroup"}, desc = "Manages group permissions.", usage = "<group> <subcommand>", min = 2, permission = PermsPermissions.MANAGE)
	public void permgroup(CommandSender sender, String command, String args[]) {
		String world = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
		String groupName = args[0];
		if(args[1].equalsIgnoreCase("create")) {
			PermissionGroup group = this.module.getPermsManager().getWorld(world).getGroup(groupName);
			if(group != null) {
				sender.sendMessage("perms.group-exists", group.getName());
				return;
			}

			this.module.getPermsManager().getWorld(world).createGroup(groupName);
			sender.sendMessage("perms.group-created", group.getName());
		} else {
			PermissionGroup group = this.module.getPermsManager().getWorld(world).getGroup(groupName);
			if(group == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}

			if(args[1].equalsIgnoreCase("delete")) {
				this.module.getPermsManager().getWorld(world).deleteGroup(group);
				sender.sendMessage("perms.group-deleted", group.getName());
			} else if(args[1].equalsIgnoreCase("addperm")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> addperm <permission>");
					return;
				}

				if(group.addPermission(args[2])) {
					sender.sendMessage("perms.perm-granted", args[2], group.getName());
				} else {
					sender.sendMessage("perms.perm-already-has", group.getName());
				}
			} else if(args[1].equalsIgnoreCase("delperm")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> delperm <permission>");
					return;
				}

				if(group.removePermission(args[2])) {
					sender.sendMessage("perms.perm-removed", args[2], group.getName());
				} else {
					sender.sendMessage("perms.perm-doesnt-have", group.getName());
				}
			} else if(args[1].equalsIgnoreCase("setprefix")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> setprefix <prefix/remove>");
					return;
				}

				args[2] = args[2].replaceAll("_", " ");
				if(args[2].equalsIgnoreCase("remove")) {
					group.removePrefix();
					sender.sendMessage("perms.prefix-removed", group.getName());
				} else {
					group.setPrefix(args[2]);
					sender.sendMessage("perms.prefix-put", group.getName(), ChatColor.translateAlternateColorCodes('&', args[2]));
				}
			} else if(args[1].equalsIgnoreCase("setsuffix")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> setsuffix <suffix/remove>");
					return;
				}

				args[2] = args[2].replaceAll("_", " ");
				if(args[2].equalsIgnoreCase("remove")) {
					group.removeSuffix();
					sender.sendMessage("perms.suffix-removed", group.getName());
				} else {
					group.setSuffix(args[2]);
					sender.sendMessage("perms.suffix-put", group.getName(), ChatColor.translateAlternateColorCodes('&', args[2]));
				}
			} else if(args[1].equalsIgnoreCase("addinherit")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> addinherit <group>");
					return;
				}

				PermissionGroup g = this.module.getPermsManager().getWorld(world).getGroup(args[2]);
				if(g == null) {
					sender.sendMessage("perms.group-not-found");
					return;
				}

				group.addInheritance(g);
				sender.sendMessage("perms.added-inheritance", group.getName(), g.getName());
			} else if(args[1].equalsIgnoreCase("removeinherit")) {
				if(args.length < 3) {
					sender.sendMessage("generic.usage", "/" + command + " <group> removeinherit <group>");
					return;
				}

				PermissionGroup g = this.module.getPermsManager().getWorld(world).getGroup(args[2]);
				if(g == null) {
					sender.sendMessage("perms.group-not-found");
					return;
				}

				group.removeInheritance(g);
				sender.sendMessage("perms.removed-inheritance", group.getName(), g.getName());
			} else {
				sender.sendMessage("generic.invalid-sub", "create, delete, addperm, delperm, setprefix, setsuffix, addinherit, removeinherit");
			}
		}
	}

	@Command(aliases = {"defaultgroup", "defgroup"}, desc = "Sets the default group in your current world.", usage = "<group>", min = 1, permission = PermsPermissions.MANAGE)
	public void defaultgroup(CommandSender sender, String command, String args[]) {
		String world = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
		PermissionGroup group = this.module.getPermsManager().getWorld(world).getGroup(args[0]);
		if(group == null) {
			sender.sendMessage("perms.group-not-found");
			return;
		}

		this.module.getPermsManager().getWorld(world).setDefaultGroup(group);
		sender.sendMessage("perms.set-default-group", world, group.getName());
	}
	
	@Command(aliases = {"spawn"}, desc = "Teleports you to spawn.", usage = "[world]", permission = PermsPermissions.SPAWN, console = false, commandblock = false)
	public void spawn(CommandSender sender, String command, String args[]) {
		Player p = (Player) BukkitCommandSender.unwrap(sender);
		World w = Bukkit.getServer().getWorld(this.module.getManager().getDefaultWorld());
		if(args.length > 0) {
			w = Bukkit.getServer().getWorld(args[0]);
			if(w == null) {
				sender.sendMessage("generic.invalid-world");
				return;
			}
		}
		
		PermissionWorld world = this.module.getPermsManager().getWorldIfGroups(w.getName());
		if(world == null) {
			if(!this.module.getPermsManager().hasGlobalSpawn(w.getName())) {
				this.module.getPermsManager().setGlobalSpawn(w.getName(), w.getSpawnLocation());
			}
			
			p.teleport(this.module.getPermsManager().getGlobalSpawn(w.getName()));
			sender.sendMessage("perms.spawn-teleport");
			return;
		}
		
		world.addIfMissing(p.getName());
		PermissionPlayer player = world.getPlayer(p.getName());
		Location groupSpawn = null;
		for(PermissionGroup g : player.getGroups()) {
			if(this.module.getPermsManager().hasGroupSpawn(w.getName(), g.getName())) {
				groupSpawn = this.module.getPermsManager().getGroupSpawn(w.getName(), g.getName());
				break;
			}
		}
		
		if(groupSpawn == null) {
			if(!this.module.getPermsManager().hasGlobalSpawn(w.getName())) {
				this.module.getPermsManager().setGlobalSpawn(w.getName(), w.getSpawnLocation());
			}
			
			p.teleport(this.module.getPermsManager().getGlobalSpawn(w.getName()));
		} else {
			p.teleport(groupSpawn);
		}
		
		sender.sendMessage("perms.spawn-teleport");
	}
	
	@Command(aliases = {"setspawn"}, desc = "Sets the spawn of the world you are in.", usage = "[group]", permission = PermsPermissions.MANAGE, console = false, commandblock = false)
	public void setspawn(CommandSender sender, String command, String args[]) {
		Player player = (Player) BukkitCommandSender.unwrap(sender);
		if(args.length > 0) {
			PermissionWorld world = this.module.getPermsManager().getWorld(player.getWorld().getName());
			PermissionGroup group = world.getGroup(args[0]);
			if(group == null) {
				sender.sendMessage("perms.group-not-found");
				return;
			}
			
			this.module.getPermsManager().setGroupSpawn(world.getName(), group.getName(), player.getLocation());
			sender.sendMessage("perms.put-spawn", group.getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		} else {
			this.module.getPermsManager().setGlobalSpawn(player.getWorld().getName(), player.getLocation());
			player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			sender.sendMessage("perms.put-spawn", player.getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		}
	}
	
}
