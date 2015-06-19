package com.peacecraftec.bukkit.chat.command;

import com.peacecraftec.bukkit.chat.ChatPermissions;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.chat.util.ChatUtil;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.web.chat.data.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.*;

public class ChatCommands {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm");

    private PeacecraftChat module;

    public ChatCommands(PeacecraftChat module) {
        this.module = module;
    }

    @Command(aliases = { "channel", "ch" }, desc = "chat.command.channel", usage = "<action> <channel>", min = 2, permission = ChatPermissions.USE_CHANNELS, console = false, commandblock = false)
    public void channel(CommandSender sender, String command, String args[]) {
        Player player = (Player) sender;
        if(args[1].equalsIgnoreCase("g")) {
            args[1] = "global";
        }

        String channel = args[1].toLowerCase().replaceAll("[^a-z0-9_-]", "");
        ChannelData data = this.module.getChannelData(player.getName());
        if(args[0].equalsIgnoreCase("join")) {
            if(channel.equalsIgnoreCase("mod")) {
                if(!player.hasPermission(ChatPermissions.MOD)) {
                    sendMessage(player, "chat.no-perm-modchannel");
                    return;
                }

                data.setActiveChannel(channel);
                sendMessage(player, "chat.no-need-join-modchannel");
                return;
            }

            if(data.isInChannel(channel)) {
                sendMessage(player, "chat.already-in-channel");
                return;
            }

            data.addChannel(channel);
            data.setActiveChannel(channel);
            this.module.sendToChannel(null, channel, "chat.player-joined-channel", getDisplayName(player), channel);
        } else if(args[0].equalsIgnoreCase("leave")) {
            if(channel.equalsIgnoreCase("mod")) {
                if(!player.hasPermission(ChatPermissions.MOD)) {
                    sendMessage(player, "chat.no-perm-modchannel");
                    return;
                }

                sendMessage(player, "chat.modchannel-cant-leave");
                return;
            }

            if(!data.isInChannel(channel)) {
                sendMessage(player, "chat.not-in-channel");
                return;
            }

            this.module.sendToChannel(null, channel, "chat.player-left-channel", getDisplayName(player), channel);
            data.removeChannel(channel);
        } else if(args[0].equalsIgnoreCase("active")) {
            if(channel.equalsIgnoreCase("mod") && !player.hasPermission(ChatPermissions.MOD)) {
                sendMessage(player, "chat.no-perm-modchannel");
                return;
            }

            if(!data.isInChannel(channel)) {
                data.addChannel(channel);
                this.module.sendToChannel(null, channel, "chat.player-joined-channel", getDisplayName(player), channel);
            }

            data.setActiveChannel(channel);
            sendMessage(player, "chat.set-active", channel);
        }
    }

    @Command(aliases = { "mod", "m" }, desc = "chat.command.mod", usage = "<message>", min = 1, permission = ChatPermissions.MOD)
    public void mod(CommandSender sender, String command, String args[]) {
        this.module.sendToMod(sender instanceof Player ? sender.getName() : null, getDisplayName(sender) + ChatColor.AQUA + " [MOD]" + ChatColor.WHITE + ": " + join(Arrays.copyOfRange(args, 0, args.length), " "));
    }

    @Command(aliases = { "me", "emote" }, desc = "chat.command.me", usage = "<message>", min = 1, permission = ChatPermissions.ME)
    public void me(CommandSender sender, String command, String args[]) {
        if(sender instanceof Player && this.module.isMuted(sender.getName())) {
            sendMessage(sender, "chat.muted");
            return;
        }

        String message = join(Arrays.copyOfRange(args, 0, args.length), " ");
        if(sender.hasPermission(ChatPermissions.CHAT_COLOR)) {
            message = ChatUtil.translateColor(message);
        }

        if(sender.hasPermission(ChatPermissions.CHAT_MAGIC)) {
            message = message.replaceAll("&[kK]", ChatColor.COLOR_CHAR + "k");
        }

        if(sender.hasPermission(ChatPermissions.CHAT_FORMAT)) {
            message = ChatUtil.translateFormat(message);
        }

        if(ChatColor.stripColor(message).trim().equals("")) {
            return;
        }

        ChannelData data = sender instanceof Player ? this.module.getChannelData(sender.getName()) : null;
        String format = ChatColor.DARK_PURPLE + " * " + ChatColor.RESET + "%1$s" + ChatColor.RESET + (data != null ? " " + ChatUtil.formatChannelName(data.getActiveChannel()) : "") + ChatColor.DARK_PURPLE + " %2$s";
        if(sender instanceof Player) {
            this.module.sendToChannel(sender.getName(), data.getActiveChannel(), String.format(format, getDisplayName(sender), message));
        } else {
            broadcastMessage(String.format(format, getDisplayName(sender), message));
        }
    }

    @Command(aliases = { "nickname", "nick" }, desc = "chat.command.nickname", usage = "[player] <name/off>", min = 1, permission = ChatPermissions.NICKNAME)
    public void nickname(CommandSender sender, String command, String args[]) {
        Player player = null;
        String name = "";
        if(args.length > 1) {
            if(!sender.hasPermission(ChatPermissions.NICKNAME_OTHER)) {
                sendMessage(sender, "internal.no-command-perm");
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

            name = args[1];
        } else {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            player = (Player) sender;
            name = args[0];
        }

        if(name.equalsIgnoreCase("off")) {
            this.module.clearName(player.getName());
            this.module.loadDisplayName(player);
            sendMessage(sender, "chat.nick-removed", player.getName());
            sendMessage(player, "chat.your-nick-removed");
        } else {
            this.module.setName(player, name.replaceAll(" ", "_"));
            String fmt = ChatColor.translateAlternateColorCodes('&', name);
            sendMessage(sender, "chat.nick-changed", player.getName(), fmt);
            sendMessage(player, "chat.your-nick-changed", fmt);
        }
    }

    @Command(aliases = { "whois", "realname" }, desc = "chat.command.whois", usage = "<name>", min = 1, permission = ChatPermissions.WHOIS)
    public void whois(CommandSender sender, String command, String args[]) {
        List<String> results = new ArrayList<String>();
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            if(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.module.getName(player.getName()))).toLowerCase().contains(args[0].toLowerCase())) {
                results.add(player.getName());
            }
        }

        if(results.size() == 0) {
            sendMessage(sender, "chat.name-not-found");
        } else if(results.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
        } else {
            sendMessage(sender, "chat.is-really", ChatColor.translateAlternateColorCodes('&', this.module.getName(results.get(0))), results.get(0));
        }
    }

    @Command(aliases = { "staff", "mods" }, desc = "chat.command.staff", usage = "", permission = ChatPermissions.STAFF_LIST)
    public void staff(CommandSender sender, String command, String args[]) {
        StringBuilder build = new StringBuilder(ChatColor.RESET.toString());
        int count = 0;
        for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            if(player.hasPermission(ChatPermissions.MOD)) {
                if(count != 0) {
                    build.append(ChatColor.RESET).append(", ").append(ChatColor.RESET);
                }

                build.append(getDisplayName(player));
                count++;
            }
        }

        if(build.length() == 0) {
            sendMessage(sender, "chat.no-staff-online");
        } else {
            sendMessage(sender, count == 1 ? "chat.staff-count-singular" : "chat.staff-count-plural", count);
            sendMessage(sender, build.toString());
        }
    }

    @Command(aliases = { "clearchat" }, desc = "chat.command.clearchat", usage = "[players/all]", permission = ChatPermissions.CLEAR_CHAT)
    public void clearchat(CommandSender sender, String command, String args[]) {
        List<Player> players = new ArrayList<Player>();
        if(args.length > 0) {
            if(!sender.hasPermission(ChatPermissions.CLEAR_OTHERS_CHAT)) {
                sendMessage(sender, "internal.no-command-perm");
                return;
            }

            if(args[0].equals("all")) {
                players.addAll(Bukkit.getServer().getOnlinePlayers());
            } else {
                for(String arg : args) {
                    List<Player> matches = matchPlayer(arg);
                    if(matches.size() == 0) {
                        sendMessage(sender, "internal.player-not-found");
                    } else if(matches.size() > 1) {
                        sendMessage(sender, "internal.multiple-players");
                    } else {
                        players.add(matches.get(0));
                    }
                }
            }
        } else {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            players.add((Player) sender);
        }

        for(Player player : players) {
            if(player != null) {
                for(int i = 0; i < 100; i++) {
                    sendMessage(player, " ");
                }

                sendMessage(player, ChatColor.GREEN + "Chat cleared.");
            }
        }
    }

    @Command(aliases = { "chatpass" }, desc = "chat.command.chatpass", usage = "<password>", min = 1, permission = ChatPermissions.SET_PASS, console = false, commandblock = false)
    public void chatpass(CommandSender sender, String command, String args[]) {
        sendMessage(sender, "chat.webchat-put", this.module.setPassword(sender.getName(), args[0]));
    }

    @Command(aliases = { "mute" }, desc = "chat.command.mute", usage = "<player>", min = 1, permission = ChatPermissions.MUTE)
    public void mute(CommandSender sender, String command, String args[]) {
        if(sender instanceof Player && this.module.isMuted(sender.getName())) {
            sendMessage(sender, "chat.muted");
            return;
        }

        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        }

        Player player = matches.get(0);
        this.module.setMuted(player.getName(), true);
        sendMessage(sender, "chat.player-muted");
    }

    @Command(aliases = { "unmute" }, desc = "chat.command.unmute", usage = "<player>", min = 1, permission = ChatPermissions.MUTE)
    public void unmute(CommandSender sender, String command, String args[]) {
        if(sender instanceof Player && this.module.isMuted(sender.getName())) {
            sendMessage(sender, "chat.muted");
            return;
        }

        List<Player> matches = matchPlayer(args[0]);
        if(matches.size() == 0) {
            sendMessage(sender, "internal.player-not-found");
            return;
        } else if(matches.size() > 1) {
            sendMessage(sender, "internal.multiple-players");
            return;
        }

        Player player = matches.get(0);
        this.module.setMuted(player.getName(), false);
        sendMessage(sender, "chat.player-unmuted");
    }

    @Command(aliases = { "servertime" }, desc = "chat.command.servertime", permission = ChatPermissions.SERVER_TIME)
    public void servertime(CommandSender sender, String command, String args[]) {
        sendMessage(sender, "chat.current-time", FORMAT.format(new Date()));
    }

    private static String join(String split[], String sep) {
        StringBuilder ret = new StringBuilder();
        for(String str : split) {
            if(ret.length() > 0) ret.append(sep);
            ret.append(str);
        }

        return ret.toString();
    }
}
