package com.peacecraftec.bukkit.restrictions.listener;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.restrictions.PeacecraftRestrictions;
import com.peacecraftec.bukkit.restrictions.RestrictionsPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class RestrictionsListener implements Listener {

	private PeacecraftRestrictions module;

	public RestrictionsListener(PeacecraftRestrictions module) {
		this.module = module;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(event.getBlock().getType() == Material.BEDROCK && !event.getPlayer().hasPermission(RestrictionsPermissions.BEDROCK)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().getType() == Material.BEDROCK && !event.getPlayer().hasPermission(RestrictionsPermissions.BEDROCK)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo().clone();
		boolean moved = false;
		int maxX = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() + ".x");
		int maxZ = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() +".z");
		if(to.getX() >= maxX) {
			to.setX(maxX - 2);
			moved = true;
		}
		
		if(to.getX() <= -maxX) {
			to.setX(-maxX + 2);
			moved = true;
		}
		
		if(to.getZ() >= maxZ) {
			to.setZ(maxZ - 2);
			moved = true;
		}
		
		if(to.getZ() <= -maxZ) {
			to.setZ(-maxZ + 2);
			moved = true;
		}
		
		if(moved) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("restrictions.border.reached");
			event.setFrom(to);
			event.setTo(to);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		this.onPlayerMove(event);
	}
	
	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".x", 4000);
		this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".z", 4000);
		this.module.getConfig().save();
	}

	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		event.setFire(false);
		event.setRadius(0);
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.setYield(0);
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if(event.getBlock().getType() == Material.FIRE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}
	
}