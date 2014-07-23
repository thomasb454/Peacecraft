package com.peacecraftec.bukkit.restrictions.listener;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.restrictions.PeacecraftRestrictions;
import com.peacecraftec.bukkit.restrictions.RestrictionsPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
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
		int minX = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() + ".minX");
		int minZ = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() +".minZ");
		int maxX = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() + ".maxX");
		int maxZ = this.module.getConfig().getInteger("border." + event.getTo().getWorld().getName() +".maxZ");
		if(to.getX() >= maxX) {
			to.setX(maxX - 2);
			moved = true;
		}
		
		if(to.getX() <= minX) {
			to.setX(minX + 2);
			moved = true;
		}
		
		if(to.getZ() >= maxZ) {
			to.setZ(maxZ - 2);
			moved = true;
		}
		
		if(to.getZ() <= minZ) {
			to.setZ(minZ + 2);
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
		if(this.module.getConfig().contains("border." + event.getWorld().getName() + ".x")) {
			int x = this.module.getConfig().getInteger("border." + event.getWorld().getName() + ".x");
			int z = this.module.getConfig().getInteger("border." + event.getWorld().getName() +".z");
			this.module.getConfig().setValue("border." + event.getWorld().getName() + ".minX", -x);
			this.module.getConfig().setValue("border." + event.getWorld().getName() + ".minZ", -z);
			this.module.getConfig().setValue("border." + event.getWorld().getName() + ".maxX", x);
			this.module.getConfig().setValue("border." + event.getWorld().getName() + ".maxZ", z);
			this.module.getConfig().remove("border." + event.getWorld().getName() +".x");
			this.module.getConfig().remove("border." + event.getWorld().getName() +".z");
		} else {
			this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".minX", -4000);
			this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".minZ", -4000);
			this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".maxX", 4000);
			this.module.getConfig().applyDefault("border." + event.getWorld().getName() + ".maxZ", 4000);
		}

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
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player && event.getEntity().getWorld().getName().equals("minigames") || event.getEntity().getWorld().getName().equals("hub")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(event.getWorld().getName().equals("hub") || event.getWorld().getName().equals("minigames")) {
			event.setCancelled(true);
		}
	}
	
}