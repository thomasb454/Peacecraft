package com.peacecraftec.bukkit.lots.command;

import com.peacecraftec.bukkit.internal.selection.Selection;
import com.peacecraftec.bukkit.internal.selection.Selector;
import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.core.Lot;
import com.peacecraftec.bukkit.lots.core.Town;
import com.peacecraftec.module.cmd.Command;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class LotCommands {

    private PeacecraftLots module;

    public LotCommands(PeacecraftLots plugin) {
        this.module = plugin;
    }

    @Command(aliases = { "towns" }, desc = "lots.command.towns", permission = LotPermissions.OWN_LOT)
    public void towns(CommandSender sender, String command, String args[]) {
        sendMessage(sender, "lots.town-available");
        for(Town town : this.module.getLotManager().getTowns()) {
            if(!this.module.getConfig().getBoolean("show-all-worlds-towns", true) && sender instanceof Player && !town.getWorld().getName().equals(((Player) sender).getWorld().getName())) {
                continue;
            }

            sendMessage(sender, "lots.town-available-entry", town.getName());
        }
    }

    @Command(aliases = { "lots" }, desc = "lots.command.lots", usage = "<town/lotId>", min = 1, permission = LotPermissions.OWN_LOT)
    public void lots(CommandSender sender, String command, String args[]) {
        try {
            int id = Integer.parseInt(args[0]);
            Lot lot = this.module.getLotManager().getLot(id);
            if(lot == null) {
                sendMessage(sender, "lots.lot-doesnt-exist", id);
                return;
            }

            String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
            sendMessage(sender, key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
            return;
        } catch(NumberFormatException e) {
        }

        Town town = this.module.getLotManager().looseGetTown(args[0]);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", args[0]);
            return;
        }

        if(!sender.hasPermission(town.getPermission())) {
            sendMessage(sender, "lots.town-no-perms", args[0]);
            return;
        }

        sendMessage(sender, "lots.lot-available", args[0]);
        List<Lot> sorted = this.module.getLotManager().getTownLots(town.getName());
        Collections.sort(sorted, new Comparator<Lot>() {
            @Override
            public int compare(Lot l1, Lot l2) {
                if(l1.getId() > l2.getId()) {
                    return 1;
                } else if(l1.getId() < l2.getId()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for(Lot lot : sorted) {
            if(lot.isForSale()) {
                String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
                sendMessage(sender, key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
            }
        }
    }

    @Command(aliases = { "mylots" }, desc = "lots.command.mylots", permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void mylots(CommandSender sender, String command, String args[]) {
        List<Lot> lots = this.module.getLotManager().getPlayerLots(sender.getName());
        Collections.sort(lots, new Comparator<Lot>() {
            @Override
            public int compare(Lot l1, Lot l2) {
                if(l1.getId() > l2.getId()) {
                    return 1;
                } else if(l1.getId() < l2.getId()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        if(lots.size() == 0) {
            sendMessage(sender, "lots.lot-none-owned");
            return;
        }

        sendMessage(sender, "lots.lot-your-lots");
        for(Lot lot : lots) {
            String key = "lots.lot-info-" + (lot.isForSale() ? "sale" : "no-sale") + "-" + (lot.getOwner() != null && !lot.getOwner().equals("") ? "owned" : "unowned");
            sendMessage(sender, key, lot.getId(), lot.getSize(), lot.getX1(), lot.getY1(), lot.getZ1(), lot.getX2(), lot.getY2(), lot.getZ2(), lot.getTown().getName(), lot.getOwner() != null ? lot.getOwner() : "", VaultAPI.getEconomy().format(lot.getPrice()));
        }
    }

    @Command(aliases = { "buylot" }, desc = "lots.command.buylot", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void buylot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        Town town = lot.getTown();
        if(!sender.hasPermission(town.getPermission())) {
            sendMessage(sender, "lots.town-no-perms", args[0]);
            return;
        }

        if(!lot.isForSale()) {
            sendMessage(sender, "lots.lot-not-for-sale", args[0]);
            return;
        }

        int count = 0;
        for(Lot l : this.module.getLotManager().getPlayerLots(sender.getName())) {
            if(l.getTown().getName().equals(lot.getTown().getName())) {
                count++;
            }
        }

        int max = this.module.getConfig().getInteger("max-town-lots-per-player", 1);
        if(count > max && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
            sendMessage(sender, "lot.lot-cant-have-more", max);
            return;
        }

        double money = VaultAPI.getEconomy().getBalance(sender.getName());
        double price = lot.getPrice();
        if(money < price) {
            sendMessage(sender, "lots.lot-not-enough-money");
            return;
        }

        VaultAPI.getEconomy().withdrawPlayer(sender.getName(), price);
        if(lot.getOwner() != null && !lot.getOwner().equals(""))
            VaultAPI.getEconomy().depositPlayer(lot.getOwner(), price);
        lot.setForSale(false);
        lot.setPrice(0);
        lot.setOwner(sender.getName());
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-purchased", lot.getId(), VaultAPI.getEconomy().format(price));
    }

    @Command(aliases = { "abandonlot" }, desc = "lots.command.abandonlot", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void abandonlot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
            sendMessage(sender, "lots.lot-not-owned", args[0]);
            return;
        }

        lot.setForSale(true);
        lot.setPrice(0);
        lot.setOwner(null);
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-abandoned", args[0]);
    }

    @Command(aliases = { "selllot", "sellot" }, desc = "lots.command.selllot", usage = "<lot> <price>", min = 2, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void selllot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
            sendMessage(sender, "lots.lot-not-owned", args[0]);
            return;
        }

        int price = 0;
        try {
            price = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.invalid-price", args[0]);
            return;
        }

        if(price < 0) {
            sendMessage(sender, "lots.price-cant-be-negative");
            return;
        }

        lot.setForSale(true);
        lot.setPrice(price);
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-now-for-sale", args[0], VaultAPI.getEconomy().format(lot.getPrice()));
    }

    @Command(aliases = { "cancelsale" }, desc = "lots.command.cancelsale", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void cancelsale(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
            sendMessage(sender, "lots.lot-not-owned", args[0]);
            return;
        }

        if(!lot.isForSale()) {
            sendMessage(sender, "lots.lot-not-for-sale", args[0]);
            return;
        }

        lot.setForSale(false);
        lot.setPrice(0);
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-no-longer-for-sale", args[0]);
    }

    @Command(aliases = { "setowner" }, desc = "lots.command.setowner", usage = "<lot> <owner>", min = 2, permission = LotPermissions.MANAGE_LOTS)
    public void setowner(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        String old = lot.getOwner();
        lot.setOwner(args[1]);
        lot.setForSale(false);
        lot.setPrice(0);
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-transfered", args[0], old, lot.getOwner());
    }

    @Command(aliases = { "builders" }, desc = "lots.command.builders", usage = "<lot> <command> [args]", min = 2, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void builders(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        if((lot.getOwner() == null || !lot.getOwner().equalsIgnoreCase(sender.getName())) && !sender.hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
            sendMessage(sender, "lots.lot-not-owned", args[0]);
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

            sendMessage(sender, build.length() > 0 ? "lots.lot-builders" : "lots.lot-no-builders", build.toString());
        } else if(args[1].equalsIgnoreCase("add")) {
            if(args.length < 3) {
                sendMessage(sender, "lots.lot-specify-builder-add");
                return;
            }

            List<String> builders = lot.getBuilders();
            if(builders.contains(args[2])) {
                sendMessage(sender, "lots.lot-already-builder");
                return;
            }

            lot.addBuilder(args[2]);
            this.module.getLotManager().saveLot(lot);
            sendMessage(sender, "lots.lot-added-builder", args[2]);
        } else if(args[1].equalsIgnoreCase("remove")) {
            if(args.length < 3) {
                sendMessage(sender, "lots.lot-specify-builder-remove");
                return;
            }

            List<String> builds = lot.getBuilders();
            if(!builds.contains(args[2])) {
                sendMessage(sender, "lots.lot-not-builder");
                return;
            }

            lot.removeBuilder(args[2]);
            this.module.getLotManager().saveLot(lot);
            sendMessage(sender, "lots.lot-removed-builder", args[2]);
        } else {
            sendMessage(sender, "internal.invalid-sub", "list, add, remove");
        }
    }

    @Command(aliases = { "tplot", "lottp", "ltp", "tpl" }, desc = "lots.command.tplot", usage = "<lot>", min = 1, permission = LotPermissions.OWN_LOT, console = false, commandblock = false)
    public void tplot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        if(lot.getOwner() != null && !lot.getOwner().equals("") && !lot.getOwner().equals(sender.getName())) {
            sendMessage(sender, "lots.lot-not-owned", args[0]);
            return;
        }

        World world = lot.getTown().getWorld();
        int x = (lot.getX1() + lot.getX2()) / 2;
        int z = (lot.getZ1() + lot.getZ2()) / 2;
        int y = world.getHighestBlockYAt(x, z);
        ((Player) sender).teleport(new Location(world, x, y, z));
        sendMessage(sender, "lots.lot-teleported-to", args[0]);
    }

    @Command(aliases = { "createlot" }, desc = "lots.command.createlot", usage = "<town> [3d]", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
    public void createlot(CommandSender sender, String command, String args[]) {
        Town town = this.module.getLotManager().getTown(args[0]);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", args[0]);
            return;
        }

        Selection select = Selector.get().getSelection((Player) sender);
        if(select == null) {
            sendMessage(sender, "internal.select-area");
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
        int newId = this.module.getLotManager().getNextLotId();
        Lot lot = new Lot(this.module, town, newId, select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockY(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockY(), select.getSecondPoint().getBlockZ(), size, 0, true, null, new ArrayList<UUID>());
        this.module.getLotManager().addLot(lot);
        sendMessage(sender, "lots.lot-created", lot.getId(), lot.getSize());
    }

    @Command(aliases = { "deletelot" }, desc = "lots.command.deletelot", usage = "<lot>", min = 1, permission = LotPermissions.MANAGE_LOTS)
    public void deletelot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        this.module.getLotManager().removeLot(lot);
        sendMessage(sender, "lots.lot-deleted", args[0]);
    }

    @Command(aliases = { "createtown" }, desc = "lots.command.createtown", usage = "<name> <priceperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
    public void createtown(CommandSender sender, String command, String args[]) {
        String name = args[0];
        double priceperblock = 0;
        try {
            priceperblock = Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.town-invalid-ppb", args[1]);
            return;
        }

        Town existing = this.module.getLotManager().getTown(name);
        if(existing != null) {
            sendMessage(sender, "lots.town-already-exists", name);
            return;
        }

        Selection select = Selector.get().getSelection((Player) sender);
        if(select == null) {
            sendMessage(sender, "internal.select-area");
            return;
        }

        Town town = new Town(name, select.getFirstPoint().getWorld().getName(), LotPermissions.OWN_LOT, select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockZ(), priceperblock, 0);
        this.module.getLotManager().addTown(town);
        sendMessage(sender, "lots.town-created", town.getName());
    }

    @Command(aliases = { "setlot" }, desc = "lots.command.setlot", usage = "<lot> [3d]", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
    public void setlot(CommandSender sender, String command, String args[]) {
        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.lot-invalid-id", args[0]);
            return;
        }

        Lot lot = this.module.getLotManager().getLot(id);
        if(lot == null) {
            sendMessage(sender, "lots.lot-doesnt-exist", args[0]);
            return;
        }

        Selection select = Selector.get().getSelection((Player) sender);
        if(select == null) {
            sendMessage(sender, "internal.select-area");
            return;
        }

        if(args.length < 2) {
            select.getFirstPoint().setY(0);
            select.getSecondPoint().setY(255);
        }

        lot.setBounds(select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockY(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockY(), select.getSecondPoint().getBlockZ());
        this.module.getLotManager().saveLot(lot);
        sendMessage(sender, "lots.lot-redefined", args[0]);
    }

    @Command(aliases = { "settown" }, desc = "lots.command.settown", usage = "<town>", min = 1, permission = LotPermissions.MANAGE_LOTS, console = false, commandblock = false)
    public void settown(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        Selection select = Selector.get().getSelection((Player) sender);
        if(select == null) {
            sendMessage(sender, "internal.select-area");
            return;
        }

        town.setBounds(select.getFirstPoint().getBlockX(), select.getFirstPoint().getBlockZ(), select.getSecondPoint().getBlockX(), select.getSecondPoint().getBlockZ());
        this.module.getLotManager().saveTown(town);
        sendMessage(sender, "lots.town-redefined", town.getName());
    }

    @Command(aliases = { "setpriceperblock", "setppb" }, desc = "lots.command.setpriceperblock", usage = "<town> <priceperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS)
    public void setppb(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        double ppb = 0;
        try {
            ppb = Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.town-invalid-ppb", args[1]);
            return;
        }


        town.setPricePerBlock(ppb);
        this.module.getLotManager().saveTown(town);
        sendMessage(sender, "lots.town-ppb-changed", town.getName(), args[1]);
    }

    @Command(aliases = { "setrentperblock", "setrpb" }, desc = "lots.command.setrentperblock", usage = "<town> <rentperblock>", min = 2, permission = LotPermissions.MANAGE_LOTS)
    public void setrpb(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        double rpb = 0;
        try {
            rpb = Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            sendMessage(sender, "lots.town-invalid-rpb", args[1]);
            return;
        }


        town.setRentPerBlock(rpb);
        this.module.getLotManager().saveTown(town);
        sendMessage(sender, "lots.town-rpb-changed", town.getName(), args[1]);
    }

    @Command(aliases = { "renametown" }, desc = "lots.command.renametown", usage = "<town> <name>", min = 2, permission = LotPermissions.MANAGE_LOTS)
    public void renametown(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        String old = town.getName();
        town.setName(args[1]);
        this.module.getLotManager().saveTown(town);
        sendMessage(sender, "lots.town-name-changed", old, args[1]);
    }

    @Command(aliases = { "deletetown" }, desc = "lots.command.deletetown", usage = "<name>", min = 1, permission = LotPermissions.MANAGE_LOTS)
    public void deletetown(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        for(Lot lot : this.module.getLotManager().getTownLots(town.getName())) {
            this.module.getLotManager().removeLot(lot);
        }

        this.module.getLotManager().removeTown(town);
        sendMessage(sender, "lots.town-deleted", town.getName());
    }

    @Command(aliases = { "settownperm", "townperm" }, desc = "lots.command.settownperm", usage = "<town> <permission>", min = 2, permission = LotPermissions.MANAGE_LOTS)
    public void settownperm(CommandSender sender, String command, String args[]) {
        String name = args[0];
        Town town = this.module.getLotManager().getTown(name);
        if(town == null) {
            sendMessage(sender, "lots.town-doesnt-exist", name);
            return;
        }

        String old = town.getPermission();
        town.setPermission(args[1]);
        this.module.getLotManager().saveTown(town);
        sendMessage(sender, "lots.town-perm-changed", old, args[1]);
    }

}
