package com.peacecraftec.bukkit.lots.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.peacecraftec.bukkit.internal.hook.VaultAPI;
import com.peacecraftec.bukkit.internal.hook.selection.Selection;
import com.peacecraftec.bukkit.internal.hook.selection.SelectionAPI;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.lots.Lot;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.Town;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;

public class LotCommands extends Executor {

	private PeacecraftLots module;
	
	public LotCommands(PeacecraftLots plugin) {
		this.module = plugin;
	}
	
	@Command(aliases = {"towns"}, desc = "Lists all existing towns.", permission = LotPermissions.OWN_LOT)
	public void towns(CommandSender sender, String command, String args[]) {
		sender.sendMessage("lots.town-available");
		for(Town town : this.module.getTowns()) {
			if(!this.module.getConfig().getBoolean("show-all-worlds-towns", true) && sender instanceof PlayerSender && !town.getWorld().getName().equals(((Player) BukkitCommandSender.unwrap(sender)).getWorld().getName())) {
				continue;
			}
			
			sender.sendMessage("lots.town-available-entry", town.getName());
		}
	}
	
	@Command(aliases = {"lots"}, desc = "Lists info about the given lot or lots in the given town.", usage = "<town/lotId>", min = 1, permission = LotPermissions.OWN_LOT)
	public void lots(CommandSender sender, String command, String args[]) {
		try {
			int id = Integer.parseInt(args[0]);
			Lot lot = this.module.getLot(id);
			if(lot == null) {
				sender.sendMessage("lots.lot-doesnt-exist", id);
				return;
			}
			
			String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
			sender.sendMessage(key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
			return;
		} catch(NumberFormatException e) {
		}
		
		Town town = this.module.looseGetTown(args[0]);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", args[0]);
			return;
		}
		
		if(!sender.hasPermission(town.getPermission())) {
			sender.sendMessage("lots.town-no-perms", args[0]);
			return;
		}
		
		sender.sendMessage("lots.lot-available", args[0]);
		List<Lot> sorted = this.module.getTownLots(town.getName());
		Collections.sort(sorted, new LotSorter());
		for(Lot lot : sorted) {
			if(lot.isForSale()) {
				String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
				sender.sendMessage(key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
			}
		}
	}
	
	@Command(aliases = {"mylots"}, desc = "Lists info about lots that you own.", permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void mylots(CommandSender sender, String command, String args[]) {
		List<Lot> lots = this.module.getPlayerLots(sender.getName());
		Collections.sort(lots, new LotSorter());
		if(lots.size() == 0) {
			sender.sendMessage("lots.lot-none-owned");
			return;
		}
		
		sender.sendMessage("lots.lot-your-lots");
		for(Lot lot : lots) {
			String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
			sender.sendMessage(key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
		}
	}
	
	@Command(aliases = {"buylot"}, desc = "Buys the given lot.", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void buylot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		Town town = lot.getTown();
		if(!sender.hasPermission(town.getPermission())) {
			sender.sendMessage("lots.town-no-perms", args[0]);
			return;
		}
		
		if(!lot.isForSale()) {
			sender.sendMessage("lots.lot-not-for-sale", args[0]);
			return;
		}
		
		int count = 0;
		for(Lot l : this.module.getPlayerLots(sender.getName())) {
			if(l.getTown().getName().equals(lot.getTown().getName())) {
				count++;
			}
		}
		
		int max = this.module.getConfig().getInteger("max-town-lots-per-player", 1);
		if(count > max && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
			sender.sendMessage("lot.lot-cant-have-more", max);
			return;
		}
		
		double money = VaultAPI.getEconomy().getBalance(sender.getName());
		double price = lot.getPrice();
		if(money < price) {
			sender.sendMessage("lots.lot-not-enough-money");
			return;
		}
		
		VaultAPI.getEconomy().withdrawPlayer(sender.getName(), price);
		if(lot.getOwner() != null && !lot.getOwner().equals("")) VaultAPI.getEconomy().depositPlayer(lot.getOwner(), price);
		lot.setForSale(false);
		lot.setPrice(0);
		lot.setOwner(sender.getName());
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-purchased", lot.getId(), VaultAPI.getEconomy().format(price));
	}
	
	@Command(aliases = {"abandonlot"}, desc = "Abandons the given lot.", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void abandonlot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
			sender.sendMessage("lots.lot-not-owned", args[0]);
			return;
		}
		
		lot.setForSale(true);
		lot.setPrice(0);
		lot.setOwner(null);
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-abandoned", args[0]);
	}
	
	@Command(aliases = {"selllot", "sellot"}, desc = "Sells the given lot.", usage = "<lot> <price>", min = 2, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void selllot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
			sender.sendMessage("lots.lot-not-owned", args[0]);
			return;
		}
		
		int price = 0;
		try {
			price = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.invalid-price", args[0]);
			return;
		}
		
		if(price < 0) {
			sender.sendMessage("lots.price-cant-be-negative");
			return;
		}
		
		lot.setForSale(true);
		lot.setPrice(price);
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-now-for-sale", args[0], VaultAPI.getEconomy().format(lot.getPrice()));
	}
	
	@Command(aliases = {"cancelsale"}, desc = "Cancels the sale of the given lot.", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void cancelsale(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
			sender.sendMessage("lots.lot-not-owned", args[0]);
			return;
		}
		
		if(!lot.isForSale()) {
			sender.sendMessage("lots.lot-not-for-sale", args[0]);
			return;
		}
		
		lot.setForSale(false);
		lot.setPrice(0);
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-no-longer-for-sale", args[0]);
	}
	
	@Command(aliases = {"setowner"}, desc = "Sets the owner of the given lot.", usage = "<lot> <owner>", min = 2, permission = LotPermissions.MANAGE_LOTS)
	public void setowner(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		String old = lot.getOwner();
		lot.setOwner(args[1]);
		lot.setForSale(false);
		lot.setPrice(0);
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-transfered", args[0], old, lot.getOwner());
	}
	
	@Command(aliases = {"builders"}, desc = "Allows you to manage builders on your lots.", usage = "<lot> <command> [args]", min = 2, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void builders(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
			sender.sendMessage("lots.lot-not-owned", args[0]);
			return;
		}
		
		if(args[1].equalsIgnoreCase("list")) {
			StringBuilder build = new StringBuilder();
			for(String builder : lot.getBuilders()) {
				if(build.length() > 0) {
					build.append(", ");
				}

				build.append(builder);
			}

			sender.sendMessage(build.length() > 0 ? "lots.lot-builders" : "lots.lot-no-builders", build.toString());
		} else if(args[1].equalsIgnoreCase("add")) {
			if(args.length < 3) {
				sender.sendMessage("lots.lot-specify-builder-add");
				return;
			}

			List<String> builders = lot.getBuilders();
			if(builders.contains(args[2])) {
				sender.sendMessage("lots.lot-already-builder");
				return;
			}

			lot.addBuilder(args[2]);
			this.module.saveLot(lot);
			sender.sendMessage("lots.lot-added-builder", args[2]);
		} else if(args[1].equalsIgnoreCase("remove")) {
			if(args.length < 3) {
				sender.sendMessage("lots.lot-specify-builder-remove");
				return;
			}

			List<String> builds = lot.getBuilders();
			if(!builds.contains(args[2])) {
				sender.sendMessage("lots.lot-not-builder");
				return;
			}

			lot.removeBuilder(args[2]);
			this.module.saveLot(lot);
			sender.sendMessage("lots.lot-removed-builder", args[2]);
		} else {
			sender.sendMessage("generic.invalid-sub", "list, add, remove");
		}
	}
	
	@Command(aliases = {"tplot", "lottp", "ltp", "tpl"}, desc = "Teleports you to an empty lot or one owned by you.", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
	public void tplot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		if(lot.getOwner() != null && !lot.getOwner().equals("") && !lot.getOwner().equals(sender.getName())) {
			sender.sendMessage("lots.lot-not-owned", args[0]);
			return;
		}
		
		World world = lot.getTown().getWorld();
		int x = (lot.getX1() + lot.getX2()) / 2;
		int z = (lot.getZ1() + lot.getZ2()) / 2;
		int y = world.getHighestBlockYAt(x, z);
		((Player) BukkitCommandSender.unwrap(sender)).teleport(new Location(world, x, y, z));
		sender.sendMessage("lots.lot-teleported-to", args[0]);
	}
	
	@Command(aliases = {"createlot"}, desc = "Creates a lot in the selected area.", usage = "<town> [3d]", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
	public void createlot(CommandSender sender, String command, String args[]) {
		Town town = this.module.getTown(args[0]);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", args[0]);
			return;
		}
		
		Selection select = SelectionAPI.get().getSelection((Player) BukkitCommandSender.unwrap(sender));
		if(select == null) {
			sender.sendMessage("generic.select-area");
			return;
		}
		
		if(args.length < 2 || !args[1].equalsIgnoreCase("3d")) {
			select.getFirstPoint().setY(0);
			select.getSecondPoint().setY(255);
		}
		
		int width = select.getSecondPoint().getBlockX() - select.getFirstPoint().getBlockX() + 1;
		int height = select.getSecondPoint().getBlockY() - select.getFirstPoint().getBlockY() + 1;
		int depth = select.getSecondPoint().getBlockZ() - select.getFirstPoint().getBlockZ() + 1;
		int size = width * height * depth;
		int newId = this.module.getNextLotId();
		Lot lot = new Lot(this.module, town, newId, select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockY(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockY(), select.getSecondPoint().getBlockZ(), size, 0, true, null, new ArrayList<UUID>());
		this.module.addLot(lot);
		sender.sendMessage("lots.lot-created", lot.getId(), lot.getSize());
	}
	
	@Command(aliases = {"deletelot"}, desc = "Deletes the specified lot.", usage = "<lot>", min = 1, permission = LotPermissions.MANAGE_LOTS)
	public void deletelot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		this.module.removeLot(lot);
		sender.sendMessage("lots.lot-deleted", args[0]);
	}
	
	@Command(aliases = {"createtown"}, desc = "Creates a town in the selected area.", usage = "<name> <priceperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
	public void createtown(CommandSender sender, String command, String args[]) {
		String name = args[0];
		double priceperblock = 0;
		try {
			priceperblock = Double.parseDouble(args[1]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.town-invalid-ppb", args[1]);
			return;
		}
		
		Town existing = this.module.getTown(name);
		if(existing != null) {
			sender.sendMessage("lots.town-already-exists", name);
			return;
		}
		
		Selection select = SelectionAPI.get().getSelection((Player) BukkitCommandSender.unwrap(sender));
		if(select == null) {
			sender.sendMessage("generic.select-area");
			return;
		}
		
		Town town = new Town(name, select.getFirstPoint().getWorld().getName(), LotPermissions.OWN_LOT, select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockZ(), priceperblock, 0);
		this.module.addTown(town);
		sender.sendMessage("lots.town-created", town.getName());
	}
	
	@Command(aliases = {"setlot"}, desc = "Resets the area of the lot to the current selection.", usage = "<lot> [3d]", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
	public void setlot(CommandSender sender, String command, String args[]) {
		int id = 0;
		try {
			id = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.lot-invalid-id", args[0]);
			return;
		}
		
		Lot lot = this.module.getLot(id);
		if(lot == null) {
			sender.sendMessage("lots.lot-doesnt-exist", args[0]);
			return;
		}
		
		Selection select = SelectionAPI.get().getSelection((Player) BukkitCommandSender.unwrap(sender));
		if(select == null) {
			sender.sendMessage("generic.select-area");
			return;
		}
		
		if(args.length < 2) {
			select.getFirstPoint().setY(0);
			select.getSecondPoint().setY(255);
		}
		
		lot.setBounds(select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockY(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockY(), select.getSecondPoint().getBlockZ());
		this.module.saveLot(lot);
		sender.sendMessage("lots.lot-redefined", args[0]);
	}
	
	@Command(aliases = {"settown"}, desc = "Resets the are of the town to the current selection.", usage = "<town>", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
	public void settown(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		Selection select = SelectionAPI.get().getSelection((Player) BukkitCommandSender.unwrap(sender));
		if(select == null) {
			sender.sendMessage("generic.select-area");
			return;
		}
		
		town.setBounds(select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockZ());
		this.module.saveTown(town);
		sender.sendMessage("lots.town-redefined", town.getName());
	}
	
	@Command(aliases = {"setppb", "setpriceperblock"}, desc = "Sets the price per block of a town.", usage = "<town> <priceperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS)
	public void setppb(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		double ppb = 0;
		try {
			ppb = Double.parseDouble(args[1]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.town-invalid-ppb", args[1]);
			return;
		}
		
		
		town.setPricePerBlock(ppb);
		this.module.saveTown(town);
		sender.sendMessage("lots.town-ppb-changed", town.getName(), args[1]);
	}
	
	@Command(aliases = {"setrpb", "setrentperblock"}, desc = "Sets the rent per block of a town.", usage = "<town> <rentperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS)
	public void setrpb(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		double rpb = 0;
		try {
			rpb = Double.parseDouble(args[1]);
		} catch(NumberFormatException e) {
			sender.sendMessage("lots.town-invalid-rpb", args[1]);
			return;
		}
		
		
		town.setRentPerBlock(rpb);
		this.module.saveTown(town);
		sender.sendMessage("lots.town-rpb-changed", town.getName(), args[1]);
	}
	
	@Command(aliases = {"renametown"}, desc = "Renames a town.", usage = "<town> <name>", min = 2, permission = LotPermissions.MANAGE_LOTS)
	public void renametown(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		String old = town.getName();
		town.setName(args[1]);
		this.module.saveTown(town);
		sender.sendMessage("lots.town-name-changed", old, args[1]);
	}
	
	@Command(aliases = {"deletetown"}, desc = "Deletes the specified town.", usage = "<name>", min = 1, permission = LotPermissions.MANAGE_LOTS)
	public void deletetown(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		for(Lot lot : this.module.getTownLots(town.getName())) {
			this.module.removeLot(lot);
		}
		
		this.module.removeTown(town);
		sender.sendMessage("lots.town-deleted", town.getName());
	}
	
	@Command(aliases = {"settownperm", "townperm"}, desc = "Sets the permission required for accessing a town.", usage = "<town> <permission>", min = 2, permission = LotPermissions.MANAGE_LOTS)
	public void settownperm(CommandSender sender, String command, String args[]) {
		String name = args[0];
		Town town = this.module.getTown(name);
		if(town == null) {
			sender.sendMessage("lots.town-doesnt-exist", name);
			return;
		}
		
		String old = town.getPermission();
		town.setPermission(args[1]);
		this.module.saveTown(town);
		sender.sendMessage("lots.town-perm-changed", old, args[1]);
	}
	
}
