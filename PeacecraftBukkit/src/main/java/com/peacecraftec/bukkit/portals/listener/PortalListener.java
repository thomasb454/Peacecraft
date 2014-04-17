package com.peacecraftec.bukkit.portals.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.portals.PeacecraftPortals;

public class PortalListener implements Listener {

	private PeacecraftPortals module;

	public PortalListener(PeacecraftPortals module) {
		this.module = module;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(event.getTo() != null) {
			String portal = this.module.getPortalManager().getPortal(event.getTo());
			if(portal != null) {
				String destPortal = this.module.getPortalManager().getDestPortal(portal);
				if(destPortal != null) {
					if(!this.module.getPortalManager().isPortal(destPortal)) {
						BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("portals.dest-doesnt-exist");
						return;
					}
	
					Location dest = this.module.getPortalManager().getPortalPoint(destPortal);
					event.getPlayer().teleport(dest);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		String portal = this.module.getPortalManager().getPortal(event.getFrom());
		if(portal != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityPortal(EntityPortalEvent event) {
		if(event.getEntity() instanceof Player) {
			return;
		}
		
		String portal = this.module.getPortalManager().getPortal(event.getFrom());
		if(portal != null) {
			event.setCancelled(true);
			String destPortal = this.module.getPortalManager().getDestPortal(portal);
			if(destPortal != null) {
				if(!this.module.getPortalManager().isPortal(destPortal)) {
					return;
				}

				Location dest = this.module.getPortalManager().getPortalPoint(destPortal);
				event.getEntity().teleport(dest);
			}
		}
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.getChangedType() == Material.PORTAL || event.getBlock().getType() == Material.PORTAL) {
			String portal = this.module.getPortalManager().getPortal(event.getBlock().getLocation());
			if(portal != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(event.getBlock().getType() == Material.WATER) {
			String portal = this.module.getPortalManager().getPortal(event.getBlock().getLocation());
			if(portal != null) {
				event.setCancelled(true);
			}
		}
	}

}
