package com.peacecraftec.bukkit.internal.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EssentialsAPI {

	private static boolean hasEss() {
		return getEss() != null;
	}
	
	private static Plugin getEss() {
		return Bukkit.getServer().getPluginManager().getPlugin("Essentials");
	}
	
	// TODO: uuids
	public static boolean isMuted(String player) {
		if(!hasEss()) {
			return false;
		}
		
		if(((com.earth2me.essentials.Essentials) getEss()).getUser(player) != null) {
			return ((com.earth2me.essentials.Essentials) getEss()).getUser(player).isMuted();
		}
		
		return false;
	}
	
	public static void setMuted(String player, boolean muted) {
		if(!hasEss()) {
			return;
		}
		
		if(((com.earth2me.essentials.Essentials) getEss()).getUser(player) != null) {
			((com.earth2me.essentials.Essentials) getEss()).getUser(player).setMuted(muted);
		}
	}
	
	public static boolean isBanned(String player) {
		if(!hasEss()) {
			return false;
		}
		
		if(((com.earth2me.essentials.Essentials) getEss()).getUser(player) != null) {
			return ((com.earth2me.essentials.Essentials) getEss()).getUser(player).isBanned();
		}
		
		return false;
	}
	
	public static void setBanned(String player, boolean banned) {
		if(!hasEss()) {
			return;
		}
		
		if(((com.earth2me.essentials.Essentials) getEss()).getUser(player) != null) {
			((com.earth2me.essentials.Essentials) getEss()).getUser(player).setBanned(banned);
		}
	}
	
}
