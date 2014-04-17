package com.peacecraftec.bukkit.internal.hook.selection;

import org.bukkit.entity.Player;

public interface Selector {
	
	public Selection getSelection(Player player);
	
}
