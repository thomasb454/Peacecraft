package com.peacecraftec.bukkit.lots.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.lots.Lot;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.Town;

public class LotBlockListener implements Listener {

	private PeacecraftLots module;
	
	public LotBlockListener(PeacecraftLots plugin) {
		this.module = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Lot lot = this.module.getLot(event.getBlock().getLocation());
		Town town = this.module.getTown(event.getBlock().getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Lot lot = this.module.getLot(event.getBlock().getLocation());
		Town town = this.module.getTown(event.getBlock().getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		Lot lot = this.module.getLot(event.getBlock().getLocation());
		Town town = this.module.getTown(event.getBlock().getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getPlayer() != null) {
			Lot lot = this.module.getLot(event.getBlock().getLocation());
			Town town = this.module.getTown(event.getBlock().getLocation());
			if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
				BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		Lot from = this.module.getLot(event.getBlock().getLocation());
		Lot to = this.module.getLot(event.getToBlock().getLocation());
		if((from != null && to == null) || (from == null && to != null) || (from != to && from != null && to != null)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		Lot from = this.module.getLot(event.getBlock().getLocation());
		Lot to = this.module.getLot(event.getBlock().getRelative(event.getDirection()).getLocation());
		Town toTown = this.module.getTown(event.getBlock().getRelative(event.getDirection()).getLocation());
		Town fromTown = this.module.getTown(event.getBlock().getLocation());
		if((from != null && to == null) || (from == null && to != null) || (to == null && fromTown == null && toTown != null)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		Location toLoc = event.getBlock().getRelative(event.getDirection()).getLocation();
		Lot from = this.module.getLot(event.getRetractLocation());
		Lot to = this.module.getLot(toLoc);
		Town fromTown = this.module.getTown(event.getRetractLocation());
		Town toTown = this.module.getTown(event.getBlock().getRelative(event.getDirection()).getLocation());
		if((from != null && to == null) || (from == null && to != null) || (from == null && toTown == null && fromTown != null)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		Lot lot = this.module.getLot(event.getBlock().getLocation());
		Town town = this.module.getTown(event.getBlock().getLocation());
		if(lot != null || town != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		Lot from = this.module.getLot(event.getSource().getLocation());
		Lot to = this.module.getLot(event.getBlock().getLocation());
		Town fromTown = this.module.getTown(event.getSource().getLocation());
		Town toTown = this.module.getTown(event.getBlock().getLocation());
		if(event.getBlock().getType() == Material.FIRE && (from != null || to != null || fromTown != null || toTown != null)) {
			event.setCancelled(true);
		}
	}
	
}
