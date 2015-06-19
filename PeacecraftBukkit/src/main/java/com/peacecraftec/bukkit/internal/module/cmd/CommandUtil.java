package com.peacecraftec.bukkit.internal.module.cmd;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandUtil {
    private static ModuleManager manager;

    public static void init(ModuleManager manager) {
        CommandUtil.manager = manager;
    }

    public static void cleanup() {
        CommandUtil.manager = null;
    }

    public static List<Player> matchPlayer(String name) {
        List<Player> players = Bukkit.getServer().matchPlayer(name);
        boolean had = players.size() > 0;
        if(CommandUtil.manager.isEnabled("Chat")) {
            PeacecraftChat chat = (PeacecraftChat) CommandUtil.manager.getModule("Chat");
            for(Player player : Bukkit.getServer().getOnlinePlayers()) {
                if(!players.contains(player)) {
                    if(chat.hasName(player.getName())) {
                        String nick = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', chat.getName(player.getName())));
                        if(!had && nick.equalsIgnoreCase(name)) {
                            players.clear();
                            players.add(player);
                            break;
                        }

                        if(nick.toLowerCase().contains(name.toLowerCase())) {
                            players.add(player);
                        }
                    }
                }
            }
        }

        return players;
    }

    public static String getDisplayName(CommandSender sender) {
        if(sender instanceof ConsoleCommandSender) {
            return ChatColor.GRAY + "CONSOLE" + ChatColor.WHITE;
        } else if(sender instanceof BlockCommandSender) {
            return ChatColor.GRAY + "@" + ChatColor.WHITE;
        } else if(sender instanceof Player) {
            return ((Player) sender).getDisplayName();
        } else {
            return sender.getName();
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        Language language = sender instanceof Player ? CommandUtil.manager.getLanguageManager().get(((Player) sender).spigot().getLocale()) : CommandUtil.manager.getLanguageManager().getDefault();
        sender.sendMessage(language.translate(message));
    }

    public static void sendMessage(CommandSender sender, String message, Object... args) {
        Language language = sender instanceof Player ? CommandUtil.manager.getLanguageManager().get(((Player) sender).spigot().getLocale()) : CommandUtil.manager.getLanguageManager().getDefault();
        sender.sendMessage(language.translate(message, args));
    }

    public static void broadcastMessage(String key) {
        String msg = CommandUtil.manager.getLanguageManager().getDefault().translate(key);
        Bukkit.getServer().broadcastMessage(msg);
        if(CommandUtil.manager.isEnabled("Chat")) {
            PeacecraftChat chat = (PeacecraftChat) CommandUtil.manager.getModule("Chat");
            chat.broadcastWeb(msg);
        }
    }

    public static void broadcastMessage(String key, Object... args) {
        String msg = CommandUtil.manager.getLanguageManager().getDefault().translate(key, args);
        Bukkit.getServer().broadcastMessage(msg);
        if(CommandUtil.manager.isEnabled("Chat")) {
            PeacecraftChat chat = (PeacecraftChat) CommandUtil.manager.getModule("Chat");
            chat.broadcastWeb(msg);
        }
    }
}
