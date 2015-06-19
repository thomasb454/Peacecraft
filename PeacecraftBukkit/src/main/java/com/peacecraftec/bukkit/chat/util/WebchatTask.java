package com.peacecraftec.bukkit.chat.util;

import com.peacecraftec.bukkit.chat.ChatPermissions;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.WebMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class WebchatTask implements Runnable {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm");

    private PeacecraftChat module;

    public WebchatTask(PeacecraftChat module) {
        this.module = module;
    }

    @Override
    public void run() {
        for(WebMessage chat : this.module.getIncoming()) {
            if(Bukkit.getOfflinePlayer(this.module.getManager().getUUID(chat.getPlayer())).isBanned() || this.module.isMuted(chat.getPlayer())) {
                continue;
            }

            UUID uuid = this.module.getManager().getUUID(chat.getPlayer());
            if(uuid == null) {
                continue;
            }

            String message = chat.getMessage().trim().replace("§", "");
            if(VaultAPI.getPermissions().has(this.module.getManager().getDefaultWorld(), chat.getPlayer(), ChatPermissions.CHAT_COLOR)) {
                message = ChatUtil.translateColor(message);
            }

            if(VaultAPI.getPermissions().has(this.module.getManager().getDefaultWorld(), chat.getPlayer(), ChatPermissions.CHAT_MAGIC)) {
                message = message.replaceAll("&[kK]", ChatColor.COLOR_CHAR + "k");
            }

            if(VaultAPI.getPermissions().has(this.module.getManager().getDefaultWorld(), chat.getPlayer(), ChatPermissions.CHAT_FORMAT)) {
                message = ChatUtil.translateFormat(message);
            }

            if(ChatColor.stripColor(chat.getMessage()).trim().equals("")) {
                return;
            }

            String displayName = ChatColor.DARK_GRAY + "[WEB] " + ChatColor.RESET + this.module.getDisplayName(chat.getPlayer(), this.module.getManager().getDefaultWorld());
            String format = ChatColor.GRAY + "[" + FORMAT.format(new Date()) + "] " + ChatColor.RESET + "%1$s" + ChatColor.RESET + " " + ChatUtil.formatChannelName(chat.getTo()) + ": %2$s";
            if(message.toLowerCase().startsWith("/me ") && !message.toLowerCase().endsWith("/me ")) {
                format = ChatColor.DARK_PURPLE + " * " + ChatColor.RESET + "%1$s" + ChatColor.RESET + " " + ChatUtil.formatChannelName(chat.getTo()) + ChatColor.DARK_PURPLE + " %2$s";
                message = message.replaceFirst("/me ", "");
            }

            this.module.sendToChannel(chat.getPlayer(), chat.getTo(), String.format(format, displayName, message));
        }

        for(ChannelAction action : this.module.getChannelActions()) {
            String displayName = ChatColor.DARK_GRAY + "[WEB] " + ChatColor.RESET + this.module.getDisplayName(action.getPlayer(), this.module.getManager().getDefaultWorld());
            if(action.getAction() == ChannelAction.Action.JOIN) {
                this.module.getChannelData(action.getPlayer()).addChannel(action.getChannel());
                this.module.getChannelData(action.getPlayer()).setActiveChannel(action.getChannel());
                this.module.sendToChannel(null, action.getChannel(), "chat.player-joined-channel", displayName, action.getChannel().toLowerCase());
            } else if(action.getAction() == ChannelAction.Action.LEAVE) {
                this.module.sendToChannel(null, action.getChannel(), "chat.player-left-channel", displayName, action.getChannel().toLowerCase());
                this.module.getChannelData(action.getPlayer()).removeChannel(action.getChannel());
            } else if(action.getAction() == ChannelAction.Action.CHANGED_ACTIVE) {
                this.module.getChannelData(action.getPlayer()).setActiveChannel(action.getChannel());
            }
        }
    }
}