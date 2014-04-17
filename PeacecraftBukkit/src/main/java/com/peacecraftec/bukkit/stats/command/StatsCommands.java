package com.peacecraftec.bukkit.stats.command;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
import com.peacecraftec.bukkit.stats.StatsPermissions;
import com.peacecraftec.util.StringUtil;

public class StatsCommands extends Executor {
	
	private PeacecraftStats module;
	
	public StatsCommands(PeacecraftStats module) {
		this.module = module;
	}
	
	@Command(aliases = {"stats"}, desc = "Displays the stats of a player.", usage = "[player]", permission = StatsPermissions.STATS, console = false, commandblock = false)
	public void stats(CommandSender sender, String command, String args[]) {
		String player = sender.getName();
		if(args.length > 0) player = args[0];
		sender.sendMessage("stats.command-header", player);
		Map<String, Double> stats = this.module.getStatSystem().getAll(player);
		Set<String> keys = stats.keySet();
		Set<String> covered = new HashSet<String>();
		for(String stat : keys) {
			if(covered.contains(stat)) continue;
			String category = stat.substring(0, stat.indexOf("."));
			String catname = category.replaceAll("_", " ");
			sender.sendMessage(ChatColor.YELLOW + "--- " + ChatColor.GOLD + StringUtil.capitalizeAll(catname) + ChatColor.YELLOW + " ---");
			sender.sendMessage(ChatColor.BLUE + StringUtil.capitalizeAll(stat.substring(stat.indexOf(".") + 1).replaceAll("_", " ")) + ": " + ChatColor.GREEN + this.get(stats, player, stat));
			covered.add(stat);
			for(String key : keys) {
				if(covered.contains(key)) continue;
				if(key.substring(0, key.indexOf(".")).equals(category)) {
					sender.sendMessage(ChatColor.BLUE + StringUtil.capitalizeAll(key.substring(key.indexOf(".") + 1).replaceAll("_", " ")) + ": " + ChatColor.GREEN + this.get(stats, player, key));
					covered.add(key);
				}
			}
		}
	}
	
	private String get(Map<String, Double> stats, String player, String stat) {
		if(stat.startsWith("login.")) {
			if(stat.equals("login.playtime")) {
				long time = this.module.getPlayTime(player);
				long weeks = time / (1000 * 60 * 60 * 24 * 7);
				if(weeks >= 1) {
					time -= weeks * (1000 * 60 * 60 * 24 * 7);
				} else {
					weeks = 0;
				}
				
				long days = time / (1000 * 60 * 60 * 24);
				if(days >= 1) {
					time -= days * (1000 * 60 * 60 * 24);
				} else {
					days = 0;
				}
				
				long hours = time / (1000 * 60 * 60);
				if(hours >= 1) {
					time -= hours * (1000 * 60 * 60);
				} else {
					hours = 0;
				}
				
				long minutes = time / (1000 * 60);
				if(minutes >= 1) {
					time -= minutes * (1000 * 60);
				} else {
					minutes = 0;
				}
				
				long seconds = time / 1000;
				StringBuilder ret = new StringBuilder();
				if(weeks > 0) {
					ret.append(weeks + "w");
				}
				
				if(days > 0) {
					ret.append(days + "d");
				}
				
				if(hours > 0) {
					ret.append(hours + "h");
				}
				
				if(minutes > 0) {
					ret.append(minutes + "m");
				}
				
				if(seconds > 0) {
					ret.append(seconds + "s");
				}
				
				return ret.toString();
			} else {
				return new Date((long) stats.get(stat).doubleValue()).toString();
			}
		} else {
			return String.valueOf(stats.get(stat));
		}
	}
	
}
