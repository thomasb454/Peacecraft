package com.peacecraftec.bukkit.lots.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

import com.peacecraftec.bukkit.lots.Lot;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.Town;

public class LotEntityListener implements Listener {

	private PeacecraftLots module;
	
	public LotEntityListener(PeacecraftLots plugin) {
		this.module = plugin;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getEntity() != null && event.getSpawnReason() != SpawnReason.CUSTOM && event.getSpawnReason() != SpawnReason.SPAWNER_EGG && event.getSpawnReason() != SpawnReason.BREEDING && event.getSpawnReason() != SpawnReason.BUILD_IRONGOLEM && event.getSpawnReason() != SpawnReason.BUILD_SNOWMAN && !(event.getEntity() instanceof IronGolem)) {
			Lot lot = this.module.getLot(event.getEntity().getLocation());
			Town town = this.module.getTown(event.getEntity().getLocation());
			if(lot != null || town != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() != null) {
			Lot lot = this.module.getLot(event.getEntity().getLocation());
			Town town = this.module.getTown(event.getEntity().getLocation());
			if(event.getEntity() instanceof Player && (lot != null || town != null)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.getEntity() != null) {
			Lot lot = this.module.getLot(event.getEntity().getLocation());
			Town town = this.module.getTown(event.getEntity().getLocation());
			if(lot != null || town != null) {
				event.setYield(0);
				event.setCancelled(true);
			} else {
				for(Block block : event.blockList()) {
					Lot l = this.module.getLot(block.getLocation());
					Town t = this.module.getTown(block.getLocation());
					if(l != null || t != null) {
						event.setYield(0);
						event.setCancelled(true);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof Player) {
			Lot lot = this.module.getLot(event.getTarget().getLocation());
			Town town = this.module.getTown(event.getTarget().getLocation());
			if(lot != null || town != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHangingPlace(HangingPlaceEvent event) {
		Lot lot = this.module.getLot(event.getEntity().getLocation());
		Town town = this.module.getTown(event.getEntity().getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHangingBreak(HangingBreakEvent event) {
		if (event instanceof HangingBreakByEntityEvent) {
			Entity attacker = ((HangingBreakByEntityEvent) event).getRemover();
			if (attacker instanceof Player) {
				Player player = (Player) attacker;
				Lot lot = this.module.getLot(event.getEntity().getLocation());
				Town town = this.module.getTown(event.getEntity().getLocation());
				if(((lot != null && !lot.canBuild(player)) || (lot == null && town != null)) && !player.hasPermission(LotPermissions.BUILD_ANYWHERE)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
}
