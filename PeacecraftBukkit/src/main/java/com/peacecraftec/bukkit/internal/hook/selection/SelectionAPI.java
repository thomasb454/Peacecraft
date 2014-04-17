package com.peacecraftec.bukkit.internal.hook.selection;

import org.bukkit.Bukkit;

public class SelectionAPI {

	public static Selector get() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
			return new WorldEditSelector((com.sk89q.worldedit.bukkit.WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"));
		}
		
		return new EmptySelector();
	}
	
}
