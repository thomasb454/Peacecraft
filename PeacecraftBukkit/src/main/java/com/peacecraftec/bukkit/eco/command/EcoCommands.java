package com.peacecraftec.bukkit.eco.command;

import com.peacecraftec.bukkit.eco.EcoPermissions;
import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.bukkit.eco.core.EcoPlayer;
import com.peacecraftec.bukkit.eco.core.EcoWorld;
import com.peacecraftec.module.cmd.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.*;

public class EcoCommands {

    private PeacecraftEco module;

    public EcoCommands(PeacecraftEco module) {
        this.module = module;
    }

    @Command(aliases = { "economy", "eco" }, desc = "economy.command.economy", usage = "<give/take/set> <player> <amount>", min = 3, permission = EcoPermissions.MANAGE)
    public void eco(CommandSender sender, String command, String args[]) {
        if(args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("set")) {
            String w = sender instanceof Player ? ((Player) sender).getWorld().getName() : this.module.getManager().getDefaultWorld();
            EcoWorld world = this.module.getEcoManager().getWorld(w);
            EcoPlayer player = world.getPlayer(args[1]);
            if(player == null) {
                sendMessage(sender, "economy.player-not-found");
                return;
            }

            double amount = 0;
            try {
                amount = Double.parseDouble(args[2]);
            } catch(NumberFormatException e) {
                sendMessage(sender, "economy.invalid-amount");
                return;
            }

            if(amount < 0) {
                sendMessage(sender, "economy.negative");
                return;
            }

            if(args[0].equalsIgnoreCase("give")) {
                player.addBalance(amount);
                sendMessage(sender, "economy.add-to-account", this.module.getEcoManager().format(amount), player.getName());
            } else if(args[0].equalsIgnoreCase("take")) {
                player.removeBalance(amount);
                sendMessage(sender, "economy.remove-from-account", this.module.getEcoManager().format(amount), player.getName());
            } else if(args[0].equalsIgnoreCase("set")) {
                player.setBalance(amount);
                sendMessage(sender, "economy.set-balance", this.module.getEcoManager().format(amount), player.getName());
            }
        } else {
            sendMessage(sender, "internal.usage", "/" + command + " <give/take/set> <player> <amount>");
        }
    }

    @Command(aliases = { "balance", "bal", "money" }, desc = "economy.command.balance", usage = "[player]", permission = EcoPermissions.BALANCE)
    public void balance(CommandSender sender, String command, String args[]) {
        Player player = null;
        if(args.length > 0) {
            if(!sender.hasPermission(EcoPermissions.BALANCE_OTHERS)) {
                sendMessage(sender, "economy.noperm-other-balance");
                return;
            }

            List<Player> players = matchPlayer(args[0]);
            if(players.size() == 0) {
                sendMessage(sender, "internal.player-not-found");
                return;
            } else if(players.size() > 1) {
                sendMessage(sender, "internal.multiple-players");
                return;
            } else {
                player = players.get(0);
            }
        } else {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            player = (Player) sender;
        }

        String w = sender instanceof Player ? ((Player) sender).getWorld().getName() : this.module.getManager().getDefaultWorld();
        EcoWorld world = this.module.getEcoManager().getWorld(w);
        EcoPlayer p = world.getPlayer(player.getName());
        if(p == null) {
            sendMessage(sender, "internal.player-not-found");
            return;
        }

        sendMessage(sender, "economy.balance", getDisplayName(player), this.module.getEcoManager().format(p.getBalance()));
    }

    @Command(aliases = { "pay" }, desc = "economy.command.pay", usage = "<player> <amount>", min = 2, permission = EcoPermissions.PAY, console = false, commandblock = false)
    public void pay(CommandSender sender, String command, String args[]) {
        EcoWorld world = this.module.getEcoManager().getWorld(((Player) sender).getWorld().getName());
        EcoPlayer from = world.getPlayer(sender.getName());
        Player playerto = null;
        EcoPlayer to = null;
        List<Player> players = matchPlayer(args[0]);
        if(players.size() == 0) {
            to = world.getPlayer(args[0]);
            if(to == null) {
                sendMessage(sender, "internal.player-not-found");
                return;
            }
        } else if(players.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        } else {
            playerto = players.get(0);
            to = world.getPlayer(playerto.getName());
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(args[1]);
            if((double) Math.round(amount * 100) / 100 == 0) {
                sendMessage(sender, "economy.invalid-amount");
                return;
            }
        } catch(NumberFormatException e) {
            sendMessage(sender, "economy.invalid-amount");
            return;
        }

        if(amount < 0) {
            sendMessage(sender, "economy.negative");
            return;
        }

        if(from.getBalance() < amount) {
            sendMessage(sender, "economy.not-enough-money");
            return;
        }

        String dname = to.getName();
        if(playerto != null) {
            dname = ChatColor.RESET + getDisplayName(playerto) + ChatColor.RESET + ChatColor.GREEN;
        }

        to.addBalance(amount);
        from.removeBalance(amount);
        sendMessage(sender, "economy.sent-money", this.module.getEcoManager().format(amount), dname);
        if(playerto != null) {
            sendMessage(playerto, "economy.received-money", this.module.getEcoManager().format(amount), ChatColor.RESET + getDisplayName(sender));
        }
    }

    @Command(aliases = { "sell" }, desc = "economy.command.sell", usage = "<item/hand> [quantity]", min = 1, permission = EcoPermissions.SELL, console = false, commandblock = false)
    public void sell(CommandSender sender, String command, String args[]) {
        Player player = (Player) sender;
        EcoPlayer eco = this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(player.getName());
        Material mat = null;
        boolean hand = false;
        if(args[0].equalsIgnoreCase("hand")) {
            hand = true;
            mat = player.getInventory().getItemInHand() != null ? player.getInventory().getItemInHand().getType() : null;
            if(mat == null || mat == Material.AIR) {
                sendMessage(sender, "economy.cant-sell-air");
                return;
            }
        } else {
            hand = false;
            mat = Material.matchMaterial(args[0]);
            if(mat == null) {
                sendMessage(sender, "economy.invalid-item");
                return;
            }

            if(!player.getInventory().contains(mat)) {
                sendMessage(sender, "economy.none-to-sell");
                return;
            }
        }

        if(!this.module.getEcoManager().hasWorth(mat.name().toLowerCase())) {
            sendMessage(sender, "economy.cannot-sell");
            return;
        }

        double cost = this.module.getEcoManager().getWorth(mat.name().toLowerCase());
        int amount = -1;
        if(args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                sendMessage(sender, "economy.invalid-amount");
                return;
            }

            if(amount < 0) {
                sendMessage(sender, "economy.invalid-amount");
                return;
            }
        }

        if(hand) {
            if(amount == -1) {
                amount = player.getInventory().getItemInHand().getAmount();
            }

            if(player.getInventory().getItemInHand().getAmount() < amount) {
                sendMessage(sender, "economy.not-enough-item");
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
                sendMessage(sender, "economy.not-enough-item");
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
        sendMessage(sender, "economy.sold-item", amount, mat.name() + (amount > 1 ? "s" : ""), this.module.getEcoManager().format(cost));
    }

    @Command(aliases = { "iteminfo" }, desc = "economy.command.iteminfo", usage = "[name/id]", permission = EcoPermissions.ITEM_INFO)
    public void iteminfo(CommandSender sender, String command, String args[]) {
        Material mat = null;
        short data = 0;
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            ItemStack item = ((Player) sender).getItemInHand();
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
                sendMessage(sender, "economy.invalid-item");
            }
        } else {
            mat = Material.matchMaterial(args[0]);
        }

        if(mat == null) {
            sendMessage(sender, "economy.invalid-item");
        }

        sendMessage(sender, "economy.iteminfo-name", mat.name() + (data != 0 ? ":" + data : ""));
    }

    @Command(aliases = { "balancetop", "baltop", "moneytop" }, desc = "economy.command.balancetop", permission = EcoPermissions.BALANCE)
    public void balancetop(CommandSender sender, String command, String args[]) {
        String w = sender instanceof Player ? ((Player) sender).getWorld().getName() : this.module.getManager().getDefaultWorld();
        EcoWorld world = this.module.getEcoManager().getWorld(w);
        List<EcoPlayer> players = world.getPlayers();
        Collections.sort(players, new Comparator<EcoPlayer>() {
            @Override
            public int compare(EcoPlayer p1, EcoPlayer p2) {
                if(p2.getBalance() > p1.getBalance()) {
                    return 1;
                } else if(p2.getBalance() < p1.getBalance()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        sendMessage(sender, "economy.top-ten-header");
        for(int count = 0; count < Math.min(10, players.size()); count++) {
            EcoPlayer player = players.get(count);
            sendMessage(sender, ChatColor.GRAY + player.getName() + ChatColor.GOLD + " - " + ChatColor.GRAY + this.module.getEcoManager().format(player.getBalance()));
        }
    }

}
