package com.peacecraftec.bukkit.internal.hook.selection;

import org.bukkit.entity.Player;

public class WorldEditSelector implements Selector {

	private com.sk89q.worldedit.bukkit.WorldEditPlugin plugin;
	
	public WorldEditSelector(com.sk89q.worldedit.bukkit.WorldEditPlugin plugin) {
		this.plugin = plugin;
	}
	
	public Selection getSelection(Player player) {
		com.sk89q.worldedit.bukkit.selections.Selection select = this.plugin.getSelection(player);
		if(select != null) {
			return new Selection(select.getMinimumPoint(), select.getMaximumPoint());
		}
		
		return null;
	}
	
}
