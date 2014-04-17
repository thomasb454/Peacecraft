package com.peacecraftec.bukkit.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.peacecraftec.bukkit.core.CorePermissions;
import com.peacecraftec.bukkit.core.PeacecraftCore;

public class CoreListener implements Listener {

	private PeacecraftCore module;
	
	public CoreListener(PeacecraftCore module) {
		this.module = module;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.module.getManager().setUserPair(event.getPlayer().getUniqueId(), event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage().toLowerCase();
		if((msg.startsWith("/plugins ") || msg.equals("/plugins") || msg.startsWith("/pl ") || msg.equals("/pl")) && !event.getPlayer().hasPermission(CorePermissions.PLUGINS)) {
			event.setCancelled(true);
		}
	}
	
}
