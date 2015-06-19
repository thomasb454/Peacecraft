package com.peacecraftec.bukkit.core.command;

import com.peacecraftec.bukkit.core.CorePermissions;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.bukkit.core.tp.TpRequest;
import com.peacecraftec.bukkit.core.tp.TpRequestType;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.cmd.Command;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.util.ChatPaginator;

import java.util.*;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.*;

public class CoreCommands {
    private PeacecraftCore module;

    public CoreCommands(PeacecraftCore module) {
        this.module = module;
    }

    @Command(aliases = { "modules" }, desc = "core.command.modules", usage = "<list/reload/enable/disable> [modules]", min = 1, permission = CorePermissions.MANAGE_MODULES)
    public void modules(CommandSender sender, String command, String args[]) {
        if(args[0].equals("list")) {
            sendMessage(sender, "core.modules-list", this.module.getManager().listString());
        } else if(args[0].equals("reload") || args[0].equals("enable") || args[0].equals("disable")) {
            if(args.length < 2) {
                sendMessage(sender, "core.specify-modules");
                return;
            }

            List<String> modules = new ArrayList<String>();
            if(args[1].equalsIgnoreCase("all")) {
                modules.addAll(this.module.getManager().getModuleTypes());
            } else {
                for(String arg : Arrays.copyOfRange(args, 1, args.length)) {
                    modules.add(arg);
                }
            }

            if(args[0].equals("reload")) {
                for(String name : modules) {
                    Module m = this.module.getManager().getModule(name);
                    if(m != null) {
                        try {
                            m.reload();
                            sendMessage(sender, "core.reloaded-module", m.getName());
                        } catch(Throwable t) {
                            sendMessage(sender, "core.fail-reload-module", m.getName());
                            this.module.getLogger().severe("Failed to reload module \"" + m.getName() + "\"!");
                            t.printStackTrace();
                            this.module.getManager().unload(name);
                        }
                    } else {
                        sendMessage(sender, "core.unknown-module", name);
                    }
                }
            } else if(args[0].equals("enable") || args[0].equals("disable")) {
                boolean enable = args[0].equals("enable");
                for(String name : modules) {
                    if(!this.module.getManager().getModuleTypes().contains(name)) {
                        sendMessage(sender, "core.unknown-module", name);
                        continue;
                    }

                    this.module.getManager().getCoreConfig().setValue("modules." + name.toLowerCase(), enable);
                    if(enable) {
                        if(!this.module.getManager().isEnabled(name)) {
                            this.module.getManager().load(name);
                        }

                        sendMessage(sender, "core.enabled-module", name);
                    } else {
                        if(this.module.getManager().isEnabled(name)) {
                            this.module.getManager().unload(name);
                        }

                        sendMessage(sender, "core.disabled-module", name);
                    }
                }

                this.module.getManager().getCoreConfig().save();
            }
        } else {
            sendMessage(sender, "internal.invalid-sub", "list, reload, enable, disable");
        }
    }

    @Command(aliases = { "permissions", "perms", "listperms" }, desc = "core.command.permissions", usage = "[startswith] [page]", permission = CorePermissions.PERMISSIONS)
    public void permissions(CommandSender sender, String command, String args[]) {
        String startsWith = "";
        int pageNumber = 1;
        if(args.length > 0) {
            if(NumberUtils.isDigits(args[args.length - 1])) {
                startsWith = args.length > 1 ? args[0] : "";
                try {
                    pageNumber = Integer.parseInt(args[args.length - 1]);
                    if(pageNumber < 0) {
                        sendMessage(sender, "core.invalid-page");
                        return;
                    } else if(pageNumber == 0) {
                        pageNumber = 1;
                    }
                } catch(NumberFormatException e) {
                    sendMessage(sender, "core.invalid-page");
                    return;
                }
            } else {
                startsWith = args[0];
            }
        }

        int pageHeight;
        int pageWidth;
        if(sender instanceof ConsoleCommandSender) {
            pageHeight = ChatPaginator.UNBOUNDED_PAGE_HEIGHT;
            pageWidth = ChatPaginator.UNBOUNDED_PAGE_WIDTH;
        } else {
            pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 1;
            pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;
        }

        StringBuilder list = new StringBuilder();
        List<Permission> perms = new ArrayList<Permission>(Bukkit.getServer().getPluginManager().getPermissions());
        Collections.sort(perms, new Comparator<Permission>() {
            @Override
            public int compare(Permission perm, Permission perm2) {
                return perm.getName().compareTo(perm2.getName());
            }
        });

        for(Permission perm : perms) {
            if(perm.getName().contains("INTERNAL_PERMISSION") || !perm.getName().startsWith(startsWith)) {
                continue;
            }

            list.append(ChatColor.GOLD).append(perm.getName()).append(": ").append(ChatColor.WHITE).append(perm.getDescription()).append("\n");
        }

        ChatPaginator.ChatPage page = ChatPaginator.paginate(list.toString(), pageNumber, pageWidth, pageHeight);
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW);
        header.append("--------- ");
        header.append(ChatColor.WHITE);
        header.append("Permissions: ");
        header.append(" ");
        if(page.getTotalPages() > 1) {
            header.append("(");
            header.append(page.getPageNumber());
            header.append("/");
            header.append(page.getTotalPages());
            header.append(") ");
        }

        header.append(ChatColor.YELLOW);
        for(int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
            header.append("-");
        }

        sendMessage(sender, header.toString());
        for(String line : page.getLines()) {
            sendMessage(sender, line);
        }
    }

    @Command(aliases = { "wand" }, desc = "core.command.wand", permission = CorePermissions.WAND, console = false, commandblock = false)
    public void wand(CommandSender sender, String command, String args[]) {
        ItemStack item = new ItemStack(Material.WOOD_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Peacecraft Wand");
        item.setItemMeta(meta);
        ((Player) sender).getInventory().addItem(item);
    }

    @Command(aliases = { "fly" }, desc = "core.command.fly", usage = "[player]", permission = CorePermissions.FLY)
    public void fly(CommandSender sender, String command, String args[]) {
        if(args.length < 1 && !(sender instanceof Player)) {
            sendMessage(sender, "internal.cannot-use-command");
            return;
        }

        Player player = null;
        if(args.length > 0) {
            if(!sender.hasPermission(CorePermissions.FLY_OTHERS)) {
                sendMessage(sender, "internal.no-command-perm");
                return;
            }

            List<Player> matches = matchPlayer(args[0]);
            if(matches.size() == 0) {
                sendMessage(sender, "internal.player-not-found");
                return;
            } else if(matches.size() > 1) {
                sendMessage(sender, "internal.multiple-players");
                return;
            } else {
                player = matches.get(0);
            }
        } else {
            player = (Player) sender;
        }

        player.setAllowFlight(!player.getAllowFlight());
        if(player == sender) {
            sendMessage(sender, player.getAllowFlight() ? "core.can-now-fly" : "core.can-no-longer-fly");
        } else {
            sendMessage(player, player.getAllowFlight() ? "core.can-now-fly" : "core.can-no-longer-fly");
            sendMessage(sender, player.getAllowFlight() ? "core.other-can-now-fly" : "core.other-can-no-longer-fly", getDisplayName(player));
        }
    }

    @Command(aliases = { "invisible", "invis", "vanish" }, desc = "core.command.invisible", usage = "[player]", permission = CorePermissions.INVIS)
    public void invis(CommandSender sender, String command, String args[]) {
        if(args.length < 1 && !(sender instanceof Player)) {
            sendMessage(sender, "internal.cannot-use-command");
            return;
        }

        Player player = null;
        if(args.length > 0) {
            if(!sender.hasPermission(CorePermissions.INVIS_OTHERS)) {
                sendMessage(sender, "internal.no-command-perm");
                return;
            }

            List<Player> matches = matchPlayer(args[0]);
            if(matches.size() == 0) {
                sendMessage(sender, "internal.player-not-found");
                return;
            } else if(matches.size() > 1) {
                sendMessage(sender, "internal.multiple-players");
                return;
            } else {
                player = matches.get(0);
            }
        } else {
            player = (Player) sender;
        }

        this.module.setInvisible(player.getName(), !this.module.isInvisible(player.getName()));
        for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if(this.module.isInvisible(player.getName())) {
                pl.hidePlayer(player);
            } else {
                pl.showPlayer(player);
            }
        }

        if(player == sender) {
            sendMessage(sender, this.module.isInvisible(player.getName()) ? "core.is-now-invisible" : "core.is-no-longer-invisible");
        } else {
            sendMessage(player, this.module.isInvisible(player.getName()) ? "core.is-now-invisible" : "core.is-no-longer-invisible");
            sendMessage(sender, this.module.isInvisible(player.getName()) ? "core.other-is-now-invisible" : "core.other-is-no-longer-invisible", getDisplayName(player));
        }
    }

    @Command(aliases = { "rules" }, desc = "core.command.rules", permission = CorePermissions.RULES)
    public void rules(CommandSender sender, String command, String args[]) {
        sendMessage(sender, ChatColor.GOLD + "1." + ChatColor.WHITE + " No griefing, doing so will get you banned. This includes stealing animals and mobs.");
        sendMessage(sender, ChatColor.GOLD + "2." + ChatColor.WHITE + " No stealing, doing so can also get you banned.");
        sendMessage(sender, ChatColor.GOLD + "3." + ChatColor.WHITE + " Don't spam the chat with messages.");
        sendMessage(sender, ChatColor.GOLD + "4." + ChatColor.WHITE + " Respect the staff and their decisions.");
        sendMessage(sender, ChatColor.GOLD + "5." + ChatColor.WHITE + " Don't ask staff members for special favors (things like WorldEdit)");
        sendMessage(sender, ChatColor.GOLD + "6." + ChatColor.WHITE + " Don't use overly offensive language towards others (racial slurs, etc)");
        sendMessage(sender, ChatColor.GOLD + "7." + ChatColor.WHITE + " Do not advertise or link to other servers in chat.");
        sendMessage(sender, ChatColor.GOLD + "8." + ChatColor.WHITE + " No hacking or cheating. This includes x-ray mods and other things that give unfair advantages.");
        sendMessage(sender, ChatColor.GOLD + "9." + ChatColor.WHITE + " Do not intentionally kill players outside of allowed PVP.");
        sendMessage(sender, ChatColor.GOLD + "10." + ChatColor.WHITE + " Do not build any offensive structures.");
        sendMessage(sender, ChatColor.GOLD + "11." + ChatColor.WHITE + " Do not intentionally cause lag to the server.");
        sendMessage(sender, ChatColor.GOLD + "12." + ChatColor.WHITE + " Do not try to evade a ban, or you will be banned again and it will become permanent.");
        sendMessage(sender, ChatColor.GOLD + "13." + ChatColor.WHITE + " You are responsible for account security. If your account is compromised, you will be held responsible for what is done with it.");
        sendMessage(sender, ChatColor.GOLD + "If you have any questions about our rules or whether you can do something, please ask a staff member.");
    }

    @Command(aliases = { "back" }, desc = "core.command.back", permission = CorePermissions.BACK, console = false, commandblock = false)
    public void back(CommandSender sender, String command, String args[]) {
        Location loc = this.module.getBackLocation(sender.getName());
        if(loc == null) {
            sendMessage(sender, "core.no-back-location");
            return;
        }

        ((Player) sender).teleport(loc);
        sendMessage(sender, "core.sent-back");
    }

    @Command(aliases = { "tpr" }, desc = "core.command.tpr", usage = "<player>", min = 1, permission = CorePermissions.TPR_REQUEST, console = false, commandblock = false)
    public void tpr(CommandSender sender, String command, String args[]) {
        Player player = null;
        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        } else {
            player = matches.get(0);
        }

        this.module.setTpRequest(player.getName(), new TpRequest(TpRequestType.TPTO, sender.getName(), player.getName()));
        sendMessage(sender, "core.sent-tp-request", getDisplayName(player));
        sendMessage(player, "core.requested-tp", getDisplayName(sender));
        sendMessage(player, "core.how-to-accept");
    }

    @Command(aliases = { "tprhere" }, desc = "core.command.tprhere", usage = "<player>", min = 1, permission = CorePermissions.TPR_REQUEST, console = false, commandblock = false)
    public void tprhere(CommandSender sender, String command, String args[]) {
        Player player = null;
        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        } else {
            player = matches.get(0);
        }

        this.module.setTpRequest(player.getName(), new TpRequest(TpRequestType.TPHERE, sender.getName(), player.getName()));
        sendMessage(sender, "core.sent-tphere-request", getDisplayName(player));
        sendMessage(player, "core.requested-tphere", getDisplayName(sender));
        sendMessage(player, "core.how-to-accept");
    }

    @Command(aliases = { "tpa" }, desc = "core.command.tpa", permission = CorePermissions.TPR_RESPOND, console = false, commandblock = false)
    public void tpa(CommandSender sender, String command, String args[]) {
        TpRequest request = this.module.getTpRequest(sender.getName());
        if(request == null) {
            sendMessage(sender, "core.no-tp-requests");
            return;
        }

        this.module.setTpRequest(sender.getName(), null);
        Player player = Bukkit.getServer().getPlayer(request.getSender());
        if(player == null) {
            sendMessage(sender, "internal.player-not-found");
            return;
        }

        if(request.getType() == TpRequestType.TPTO) {
            player.teleport((Player) sender);
        } else if(request.getType() == TpRequestType.TPHERE) {
            ((Player) sender).teleport(player);
        }

        sendMessage(sender, "core.accepted-tp-request-of", getDisplayName(player));
        sendMessage(player, "core.accepted-your-tp-request", getDisplayName(sender));
    }

    @Command(aliases = { "tpd" }, desc = "core.command.tpd", permission = CorePermissions.TPR_RESPOND, console = false, commandblock = false)
    public void tpd(CommandSender sender, String command, String args[]) {
        TpRequest request = this.module.getTpRequest(sender.getName());
        if(request == null) {
            sendMessage(sender, "core.no-tp-requests");
            return;
        }

        this.module.setTpRequest(sender.getName(), null);
        Player player = Bukkit.getServer().getPlayer(request.getSender());
        if(player == null) {
            sendMessage(sender, "internal.player-not-found");
            return;
        }

        sendMessage(sender, "core.denied-tp-request-of", getDisplayName(player));
        sendMessage(player, "core.denied-your-tp-request", getDisplayName(sender));
    }

    @Command(aliases = { "home" }, desc = "core.command.home", permission = CorePermissions.HOME, console = false, commandblock = false)
    public void home(CommandSender sender, String command, String args[]) {
        Location loc = this.module.getHome(sender.getName());
        if(loc == null) {
            sendMessage(sender, "core.no-home");
            return;
        }

        ((Player) sender).teleport(loc);
        sendMessage(sender, "core.sent-home");
    }

    @Command(aliases = { "sethome" }, desc = "core.command.sethome", permission = CorePermissions.HOME, console = false, commandblock = false)
    public void sethome(CommandSender sender, String command, String args[]) {
        this.module.setHome(sender.getName(), ((Player) sender).getLocation());
        sendMessage(sender, "core.set-home");
    }

    @Command(aliases = { "warps" }, desc = "core.command.warps", permission = CorePermissions.WARP, console = false, commandblock = false)
    public void warps(CommandSender sender, String command, String args[]) {
        StringBuilder warps = new StringBuilder();
        for(String warp : this.module.getWarps()) {
            if(warps.length() != 0) {
                warps.append(", ");
            }

            warps.append(warp);
        }

        sendMessage(sender, "core.available-warps", warps.toString());
    }

    @Command(aliases = { "warp" }, desc = "core.command.warp", usage = "<name>", min = 1, permission = CorePermissions.WARP, console = false, commandblock = false)
    public void warp(CommandSender sender, String command, String args[]) {
        Location loc = this.module.getWarp(args[0].toLowerCase());
        if(loc == null) {
            sendMessage(sender, "core.no-warp", args[0]);
            return;
        }

        ((Player) sender).teleport(loc);
        sendMessage(sender, "core.sent-to-warp", args[0]);
    }

    @Command(aliases = { "setwarp" }, desc = "core.command.setwarp", usage = "<name>", min = 1, permission = CorePermissions.SET_WARP, console = false, commandblock = false)
    public void setwarp(CommandSender sender, String command, String args[]) {
        this.module.setWarp(args[0].toLowerCase(), ((Player) sender).getLocation());
        sendMessage(sender, "core.set-warp", args[0]);
    }

    @Command(aliases = { "invsee" }, desc = "core.command.invsee", usage = "<player>", min = 1, permission = CorePermissions.INVSEE, console = false, commandblock = false)
    public void invsee(CommandSender sender, String command, String args[]) {
        Player player = null;
        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        } else {
            player = matches.get(0);
        }

        ((Player) sender).openInventory(((Player) player).getInventory());
    }

    @Command(aliases = { "enderchest" }, desc = "core.command.enderchest", usage = "<player>", min = 1, permission = CorePermissions.ENDERCHEST, console = false, commandblock = false)
    public void enderchest(CommandSender sender, String command, String args[]) {
        Player player = null;
        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        } else {
            player = matches.get(0);
        }

        ((Player) sender).openInventory(player.getEnderChest());
    }

    @Command(aliases = { "lightning" }, desc = "core.command.lightning", permission = CorePermissions.LIGHTNING, console = false, commandblock = false)
    public void lightning(CommandSender sender, String command, String args[]) {
        ((Player) sender).getWorld().strikeLightning(((Player) sender).getLocation());
    }
}
