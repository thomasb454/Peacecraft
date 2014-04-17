package com.peacecraftec.bukkit.lots.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.peacecraftec.bukkit.internal.hook.VaultAPI;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.lots.Lot;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.Town;

public class LotPlayerListener implements Listener {

	private PeacecraftLots module;
	
	public LotPlayerListener(PeacecraftLots plugin) {
		this.module = plugin;
	}
	
	// CONVERSION CODE
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.module.convert(event.getPlayer().getName());
	}
	// END CONVERSION CODE
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			Lot lot = this.module.getLot(block.getLocation());
			if((event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ((Sign) event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase("[BuyLot]") && lot != null && lot.isForSale()) {
				Town town = lot.getTown();
				if(!event.getPlayer().hasPermission(town.getPermission())) {
					BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.town-no-perms", lot.getId());
					return;
				}
				
				int count = 0;
				for(Lot l : this.module.getPlayerLots(event.getPlayer().getName())) {
					if(l.getTown().getName().equals(lot.getTown().getName())) {
						count++;
					}
				}
				
				int max = this.module.getConfig().getInteger("max-town-lots-per-player", 1);
				if(count > max && !event.getPlayer().hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
					BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lot.lot-cant-have-more", max);
					return;
				}
				
				double money = VaultAPI.getEconomy().getBalance(event.getPlayer().getName());
				double price = lot.getPrice();
				if(money < price) {
					BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.lot-not-enough-money");
					return;
				}
				
				VaultAPI.getEconomy().withdrawPlayer(event.getPlayer().getName(), price);
				if(lot.getOwner() != null && !lot.getOwner().equals("")) {
					VaultAPI.getEconomy().depositPlayer(lot.getOwner(), price);
				}
				
				lot.setForSale(false);
				lot.setPrice(0);
				lot.setOwner(event.getPlayer().getName());
				this.module.saveLot(lot);
				BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.lot-purchased", lot.getId(), VaultAPI.getEconomy().format(price) + " " + (lot.getPrice() <= 1 ? VaultAPI.getEconomy().currencyNameSingular() : VaultAPI.getEconomy().currencyNamePlural()));
				event.setCancelled(true);
				return;
			}
			
			Town town = this.module.getTown(block.getLocation());
			if(block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN && block.getType() != Material.ENDER_CHEST && block.getType() != Material.WORKBENCH && block.getType() != Material.ENCHANTMENT_TABLE && ((lot != null && !lot.canInteract(event.getPlayer())) || (lot == null && town != null && (event.getItem() == null || event.getItem().getType() == null || (event.getItem().getType() != Material.LEVER && event.getItem().getType() != Material.STONE_BUTTON && event.getItem().getType() != Material.WOOD_BUTTON)))) && !event.getPlayer().hasPermission(LotPermissions.DO_ANYTHING)) {
				BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Hanging) {
			Lot lot = this.module.getLot(event.getRightClicked().getLocation());
			Town town = this.module.getTown(event.getRightClicked().getLocation());
			if(((lot != null && !lot.canInteract(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.DO_ANYTHING)) {
				BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		Lot lot = this.module.getLot(block.getLocation());
		Town town = this.module.getTown(block.getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Block block = event.getBlockClicked();
		Lot lot = this.module.getLot(block.getLocation());
		Town town = this.module.getTown(block.getLocation());
		if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.cannot-do");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Lot from = this.module.getLot(event.getFrom());
		Lot to = this.module.getLot(event.getTo());
		if(from == null && to != null) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.lot-entered-" + (to.getOwner() != null && !to.getOwner().equals("") ? "owned" : "unowned"), to.getId(), (to.getOwner() != null ? to.getOwner() : ""));
		} else if(from != null && to == null) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.lot-left", from.getId());
		}
		
		Town frmTown = this.module.getTown(event.getFrom());
		Town toTown = this.module.getTown(event.getTo());
		if(frmTown == null && toTown != null) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.town-entered", toTown.getName());
		} else if(frmTown != null && toTown == null) {
			BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("lots.town-left", frmTown.getName());
		}
	}
	
}
