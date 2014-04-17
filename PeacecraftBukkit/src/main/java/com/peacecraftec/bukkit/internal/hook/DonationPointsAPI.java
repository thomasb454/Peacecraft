package com.peacecraftec.bukkit.internal.hook;

import org.bukkit.Bukkit;

public class DonationPointsAPI {

	private static boolean hasDonationPoints() {
		return Bukkit.getServer().getPluginManager().getPlugin("DonationPoints") != null;
	}
	
	// TODO: uuids
	public static void addPoints(String player, double points) {
		if(!hasDonationPoints()) {
			return;
		}
		
		if(!com.mistphizzle.donationpoints.plugin.Methods.hasAccount(player)) {
			com.mistphizzle.donationpoints.plugin.Methods.createAccount(player);
		}
		
		com.mistphizzle.donationpoints.plugin.Methods.addPoints(points, player);
	}
	
}
