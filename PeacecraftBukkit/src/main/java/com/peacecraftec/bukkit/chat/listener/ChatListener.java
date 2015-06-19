package com.peacecraftec.bukkit.chat.listener;

import com.peacecraftec.bukkit.chat.ChatPermissions;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.chat.util.ChatUtil;
import com.peacecraftec.web.chat.data.ChannelData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.getDisplayName;
import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class ChatListener implements Listener {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm");

    private PeacecraftChat module;

    public ChatListener(PeacecraftChat module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Make sure player has channels.
        this.module.getChannelData(event.getPlayer().getName()).getChannels();
        this.module.getChannelData(event.getPlayer().getName()).getActiveChannel();
        this.module.loadDisplayName(event.getPlayer());
        this.module.addOnlinePlayer(event.getPlayer().getName());
        event.setJoinMessage(event.getJoinMessage().replaceFirst(Pattern.quote(event.getPlayer().getName()), ChatColor.RESET + Matcher.quoteReplacement(event.getPlayer().getDisplayName()) + ChatColor.RESET + ChatColor.YELLOW));
        this.module.broadcastWeb(event.getJoinMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.module.removeOnlinePlayer(event.getPlayer().getName());
        event.setQuitMessage(event.getQuitMessage().replaceFirst(Pattern.quote(event.getPlayer().getName()), ChatColor.RESET + Matcher.quoteReplacement(event.getPlayer().getDisplayName()) + ChatColor.RESET + ChatColor.YELLOW));
        this.module.broadcastWeb(event.getQuitMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        this.module.removeOnlinePlayer(event.getPlayer().getName());
        event.setLeaveMessage(event.getLeaveMessage().replaceFirst(Pattern.quote(event.getPlayer().getName()), ChatColor.RESET + Matcher.quoteReplacement(event.getPlayer().getDisplayName()) + ChatColor.RESET + ChatColor.YELLOW));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        StringBuilder msg = new StringBuilder();
        for(String pt : event.getDeathMessage().split(" ")) {
            if(msg.length() > 0) {
                msg.append(" ");
            } else {
                msg.append(ChatColor.YELLOW);
            }

            Player p = Bukkit.getServer().getPlayerExact(pt);
            if(p != null) {
                msg.append(ChatColor.RESET + getDisplayName(p) + ChatColor.RESET + ChatColor.YELLOW);
            } else {
                msg.append(pt);
            }
        }

        event.setDeathMessage(msg.toString());
        this.module.broadcastWeb(event.getDeathMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(this.module.isMuted(player.getName())) {
            sendMessage(player, "chat.muted");
            event.setCancelled(true);
            return;
        }

        if(player.hasPermission(ChatPermissions.CHAT_COLOR)) {
            event.setMessage(ChatUtil.translateColor(event.getMessage()));
        }

        if(player.hasPermission(ChatPermissions.CHAT_MAGIC)) {
            event.setMessage(event.getMessage().replaceAll("&[kK]", ChatColor.COLOR_CHAR + "k"));
        }

        if(player.hasPermission(ChatPermissions.CHAT_FORMAT)) {
            event.setMessage(ChatUtil.translateFormat(event.getMessage()));
        }

        if(ChatColor.stripColor(event.getMessage()).trim().equals("")) {
            event.setCancelled(true);
            return;
        }

        ChannelData data = this.module.getChannelData(player.getName());

        event.setFormat(ChatColor.GRAY + "[" + FORMAT.format(new Date()) + "] " + ChatColor.RESET + "%1$s" + ChatColor.RESET + " " + ChatUtil.formatChannelName(data.getActiveChannel()) + ": %2$s");
        this.module.sendToChannel(event.getPlayer().getName(), data.getActiveChannel(), String.format(event.getFormat(), getDisplayName(player), event.getMessage()));
        event.setCancelled(true);
    }
}
