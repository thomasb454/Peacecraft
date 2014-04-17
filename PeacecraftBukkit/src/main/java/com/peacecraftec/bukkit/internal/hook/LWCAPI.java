package com.peacecraftec.bukkit.internal.hook;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class LWCAPI {

	private static boolean hasLWC() {
		return getLWC() != null;
	}
	
	private static Plugin getLWC() {
		return Bukkit.getServer().getPluginManager().getPlugin("LWC");
	}
	
	public static boolean hasAccess(Block chest) {
		if(!hasLWC()) {
			return true;
		}
		
		com.griefcraft.lwc.LWC lwc = ((com.griefcraft.lwc.LWCPlugin) getLWC()).getLWC();
		com.griefcraft.model.Protection prot = lwc.findProtection(chest);
		if(prot != null && prot.getType() != com.griefcraft.model.Protection.Type.PUBLIC) {
			return false;
		}

		return true;
	}
	
}
