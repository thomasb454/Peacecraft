package com.peacecraftec.bukkit.internal.hook.selection;

import org.bukkit.entity.Player;

public class EmptySelector implements Selector {

	@Override
	public Selection getSelection(Player player) {
		return null;
	}

}
