package com.peacecraftec.bukkit.eco.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.bukkit.eco.EcoPermissions;
import com.peacecraftec.bukkit.eco.core.EcoPlayer;
import com.peacecraftec.bukkit.eco.core.EcoWorld;

public class EcoCommands extends Executor {
	
	private PeacecraftEco module;
	
	public EcoCommands(PeacecraftEco module) {
		this.module = module;
	}
	
	@Command(aliases = {"eco", "economy"}, desc = "Manages the economy module.", usage = "<give/take/set> <player> <amount>", min = 3, permission = EcoPermissions.MANAGE)
	public void eco(CommandSender sender, String command, String args[]) {
		if(args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("set")) {
			String w = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
			EcoWorld world = this.module.getEcoManager().getWorld(w);
			EcoPlayer player = world.getPlayer(args[1]);
			if(player == null) {
				sender.sendMessage("economy.player-not-found");
				return;
			}
			
			double amount = 0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch(NumberFormatException e) {
				sender.sendMessage("economy.invalid-amount");
				return;
			}
			
			if(amount < 0) {
				sender.sendMessage("economy.negative");
				return;
			}
			
			if(args[0].equalsIgnoreCase("give")) {
				player.addBalance(amount);
				sender.sendMessage("economy.add-to-account", this.module.getEcoManager().format(amount), player.getName());
			} else if(args[0].equalsIgnoreCase("take")) {
				player.removeBalance(amount);
				sender.sendMessage("economy.remove-from-account", this.module.getEcoManager().format(amount), player.getName());
			} else if(args[0].equalsIgnoreCase("set")) {
				player.setBalance(amount);
				sender.sendMessage("economy.set-balance", this.module.getEcoManager().format(amount), player.getName());
			}
		} else {
			sender.sendMessage("generic.usage", "/" + command + " <give/take/set> <player> <amount>");
		}
	}
	
	@Command(aliases = {"balance", "bal", "money"}, desc = "Shows how much money you have.", usage = "[player]", permission = EcoPermissions.BALANCE)
	public void balance(CommandSender sender, String command, String args[]) {
		Player player = null;
		if(args.length > 0) {
			if(!sender.hasPermission(EcoPermissions.BALANCE_OTHERS)) {
				sender.sendMessage("economy.noperm-other-balance");
				return;
			}
			
			List<PlayerSender> players = this.module.getManager().matchPlayerSender(args[0]);
			if(players.size() == 0) {
				sender.sendMessage("generic.player-not-found");
				return;
			} else if(players.size() > 1) {
				sender.sendMessage("generic.multiple-players");
				return;
			} else {
				player = (Player) BukkitCommandSender.unwrap(players.get(0));
			}
		} else {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			player = (Player) BukkitCommandSender.unwrap(sender);
		}
		
		String w = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
		EcoWorld world = this.module.getEcoManager().getWorld(w);
		EcoPlayer p = world.getPlayer(player.getName());
		if(p == null) {
			sender.sendMessage("generic.player-not-found");
			return;
		}
		
		sender.sendMessage("economy.balance", player.getDisplayName(), this.module.getEcoManager().format(p.getBalance()));
	}
	
	@Command(aliases = {"pay"}, desc = "Pays another player.", usage = "<player> <amount>", min = 2, permission = EcoPermissions.PAY, console = false, commandblock = false)
	public void pay(CommandSender sender, String command, String args[]) {
		EcoWorld world = this.module.getEcoManager().getWorld(((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName());
		EcoPlayer from = world.getPlayer(sender.getName());
		PlayerSender playerto = null;
		EcoPlayer to = null;
		List<PlayerSender> players = this.module.getManager().matchPlayerSender(args[0]);
		if(players.size() == 0) {
			to = world.getPlayer(args[0]);
			if(to == null) {
				sender.sendMessage("generic.player-not-found");
				return;
			}
		} else if(players.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		} else {
			playerto = players.get(0);
			to = world.getPlayer(playerto.getName());
		}
		
		double amount = 0;
		try {
			amount = Double.parseDouble(args[1]);
			if((double) Math.round(amount * 100) / 100 == 0) {
				sender.sendMessage("economy.invalid-amount");
				return;
			}
		} catch(NumberFormatException e) {
			sender.sendMessage("economy.invalid-amount");
			return;
		}
		
		if(amount < 0) {
			sender.sendMessage("economy.negative");
			return;
		}
		
		if(from.getBalance() < amount) {
			sender.sendMessage("economy.not-enough-money");
			return;
		}
		
		String dname = to.getName();
		if(playerto != null) {
			dname = ChatColor.RESET + playerto.getDisplayName() + ChatColor.RESET + ChatColor.GREEN;
		}
		
		to.addBalance(amount);
		from.removeBalance(amount);
		sender.sendMessage("economy.sent-money", this.module.getEcoManager().format(amount), dname);
		if(playerto != null) {
			playerto.sendMessage("economy.received-money", this.module.getEcoManager().format(amount), ChatColor.RESET + sender.getDisplayName());
		}
	}
	
	@Command(aliases = {"sell"}, desc = "Sells an item.", usage = "<item/hand> [quantity]", min = 1, permission = EcoPermissions.SELL, console = false, commandblock = false)
	public void sell(CommandSender sender, String command, String args[]) {
		Player player = (Player) BukkitCommandSender.unwrap(sender);
		EcoPlayer eco = this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(player.getName());
		Material mat = null;
		boolean hand = false;
		if(args[0].equalsIgnoreCase("hand")) {
			hand = true;
			mat = player.getInventory().getItemInHand() != null ? player.getInventory().getItemInHand().getType() : null;
			if(mat == null || mat == Material.AIR) {
				sender.sendMessage("economy.cant-sell-air");
				return;
			}
		} else {
			hand = false;
			mat = Material.matchMaterial(args[0]);
			if(mat == null) {
				sender.sendMessage("economy.invalid-item");
				return;
			}
			
			if(!player.getInventory().contains(mat)) {
				sender.sendMessage("economy.none-to-sell");
				return;
			}
		}
		
		if(!this.module.getEcoManager().hasWorth(mat.name().toLowerCase())) {
			sender.sendMessage("economy.cannot-sell");
			return;
		}
		
		double cost = this.module.getEcoManager().getWorth(mat.name().toLowerCase());
		int amount = -1;
		if(args.length > 1) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				sender.sendMessage("economy.invalid-amount");
				return;
			}
			
			if(amount < 0) {
				sender.sendMessage("economy.invalid-amount");
				return;
			}
		}
		
		if(hand) {
			if(amount == -1) {
				amount = player.getInventory().getItemInHand().getAmount();
			}
			
			if(player.getInventory().getItemInHand().getAmount() < amount) {
				sender.sendMessage("economy.not-enough-item");
				return;
			}
			
			cost *= amount;
			if(player.getInventory().getItemInHand().getAmount() == amount) {
				player.getInventory().setItemInHand(null);
			} else {
				player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - amount);
			}
		} else {
			int total = 0;
			Map<Integer, ? extends ItemStack> items = player.getInventory().all(mat);
			if(items != null) {
				for(ItemStack item : items.values()) {
					total += item.getAmount();
				}
			}
			
			if(amount == -1) {
				amount = total;
			}
			
			if(amount > total) {
				sender.sendMessage("economy.not-enough-item");
				return;
			}
			
			cost *= amount;
			int remaining = amount;
			for(int slot : items.keySet()) {
				ItemStack item = items.get(slot);
				if(remaining < item.getAmount()) {
					item.setAmount(item.getAmount() - remaining);
					remaining = 0;
					break;
				} else if(remaining == item.getAmount()) {
					player.getInventory().setItem(slot, null);
					remaining = 0;
					break;
				} else {
					player.getInventory().setItem(slot, null);
					remaining -= item.getAmount();
				}
			}
		}
		
		eco.addBalance(cost);
		sender.sendMessage("economy.sold-item", amount, mat.name() + (amount > 1 ? "s" : ""), this.module.getEcoManager().format(cost));
	}
	
	@Command(aliases = {"iteminfo"}, desc = "Gets info on an item.", usage = "[name/id]", permission = EcoPermissions.ITEM_INFO)
	public void iteminfo(CommandSender sender, String command, String args[]) {
		Material mat = null;
		short data = 0;
		if(args.length == 0) {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			ItemStack item = ((Player) BukkitCommandSender.unwrap(sender)).getItemInHand();
			if(item == null) {
				mat = Material.AIR;
			} else {
				mat = item.getType();
				data = item.getDurability();
			}
		} else if(args[0].contains(":")) {
			try {
				String split[] = args[0].split(":");
				mat = Material.matchMaterial(split[0]);
				data = Short.parseShort(split[1]);
			} catch(Exception e) {
				sender.sendMessage("economy.invalid-item");
			}
		} else {
			mat = Material.matchMaterial(args[0]);
		}
		
		if(mat == null) {
			sender.sendMessage("economy.invalid-item");
		}
		
		sender.sendMessage("economy.iteminfo-name", mat.name() + (data != 0 ? ":" + data : ""));
	}
	
	@Command(aliases = {"balancetop", "baltop", "moneytop"}, desc = "Shows the top ten balances.", permission = EcoPermissions.BALANCE)
	public void balancetop(CommandSender sender, String command, String args[]) {
		String w = sender instanceof PlayerSender ? ((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName() : this.module.getManager().getDefaultWorld();
		EcoWorld world = this.module.getEcoManager().getWorld(w);
		List<EcoPlayer> players = world.getPlayers();
		Collections.sort(players, new BalanceComparator());
		sender.sendMessage("economy.top-ten-header");
		for(int count = 0; count < Math.min(10, players.size()); count++) {
			EcoPlayer player = players.get(count);
			sender.sendMessage(ChatColor.GRAY + player.getName() + ChatColor.GOLD + " - " + ChatColor.GRAY + this.module.getEcoManager().format(player.getBalance()));
		}
	}
	
}
