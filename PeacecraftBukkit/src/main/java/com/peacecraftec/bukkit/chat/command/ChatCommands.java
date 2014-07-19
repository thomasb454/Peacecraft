package com.peacecraftec.bukkit.chat.command;

import com.peacecraftec.bukkit.chat.ChatPermissions;
import com.peacecraftec.bukkit.chat.ChatUtil;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.web.chat.data.ChannelData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatCommands extends Executor {
	
	private PeacecraftChat module;
	
	public ChatCommands(PeacecraftChat module) {
		this.module = module;
	}
	
	@Command(aliases = {"channel", "ch"}, desc = "Manages your active chat channels.", usage = "<action> <channel>", min = 2, permission = ChatPermissions.USE_CHANNELS, console = false, commandblock = false)
	public void channel(CommandSender sender, String command, String args[]) {
		PlayerSender player = (PlayerSender) sender;
		if(args[1].equalsIgnoreCase("g")) {
			args[1] = "global";
		}
		
		String channel = args[1].toLowerCase().replaceAll("[^a-z0-9_-]", "");
		ChannelData data = this.module.getChannelData(player.getName());
		if(args[0].equalsIgnoreCase("join")) {
			if(channel.equalsIgnoreCase("mod")) {
				if(!player.hasPermission(ChatPermissions.MOD)) {
					player.sendMessage("chat.no-perm-modchannel");
					return;
				}
				
				data.setActiveChannel(channel);
				player.sendMessage("chat.no-need-join-modchannel");
				return;
			}
			
			if(data.isInChannel(channel)) {
				player.sendMessage("chat.already-in-channel");
				return;
			}
			
			data.addChannel(channel);
			data.setActiveChannel(channel);
			this.module.sendToChannel(null, channel, "chat.player-joined-channel", player.getDisplayName(), channel);
		} else if(args[0].equalsIgnoreCase("leave")) {
			if(channel.equalsIgnoreCase("mod")) {
				if(!player.hasPermission(ChatPermissions.MOD)) {
					player.sendMessage("chat.no-perm-modchannel");
					return;
				}
				
				player.sendMessage("chat.modchannel-cant-leave");
				return;
			}
			
			if(!data.isInChannel(channel)) {
				player.sendMessage("chat.not-in-channel");
				return;
			}
			
			this.module.sendToChannel(null, channel, "chat.player-left-channel", player.getDisplayName(), channel);
			data.removeChannel(channel);
		} else if(args[0].equalsIgnoreCase("active")) {
			if(channel.equalsIgnoreCase("mod") && !player.hasPermission(ChatPermissions.MOD)) {
				player.sendMessage("chat.no-perm-modchannel");
				return;
			}
			
			if(!data.isInChannel(channel)) {
				data.addChannel(channel);
				this.module.sendToChannel(null, channel, "chat.player-joined-channel", player.getDisplayName(), channel);
			}
			
			data.setActiveChannel(channel);
			player.sendMessage("chat.set-active", channel);
		}
	}
	
	@Command(aliases = {"mod", "m"}, desc = "Quick-sends a message in the mod channel.", usage = "<message>", min = 1, permission = ChatPermissions.MOD)
	public void mod(CommandSender sender, String command, String args[]) {
		this.module.sendToMod(sender instanceof PlayerSender ? sender.getName() : null, sender.getDisplayName() + ChatColor.AQUA + " [MOD]" + ChatColor.WHITE + ": " + join(Arrays.copyOfRange(args, 0, args.length), " "));
	}
	
	@Command(aliases = {"me", "emote"}, desc = "Sends an emote message.", usage = "<message>", min = 1, permission = ChatPermissions.ME)
	public void me(CommandSender sender, String command, String args[]) {
		if(sender instanceof PlayerSender && this.module.isMuted(sender.getName())) {
			sender.sendMessage("chat.muted");
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

		ChannelData data = sender instanceof PlayerSender ? this.module.getChannelData(sender.getName()) : null;
		String format = ChatColor.DARK_PURPLE + " * " + ChatColor.RESET + "%1$s" + ChatColor.RESET + (data != null ? " " + ChatUtil.formatChannelName(data.getActiveChannel()) : "") + ChatColor.DARK_PURPLE + " %2$s";
		if(sender instanceof PlayerSender) {
			this.module.sendToChannel(sender.getName(), data.getActiveChannel(), String.format(format, sender.getDisplayName(), message));
		} else {
			this.module.getManager().broadcastMessage(String.format(format, sender.getDisplayName(), message));
		}
	}
	
	@Command(aliases = {"nickname", "nick"}, desc = "Sets the nickname of a player.", usage = "[player] <name/off>", min = 1, permission = ChatPermissions.NICKNAME)
	public void nickname(CommandSender sender, String command, String args[]) {
		PlayerSender player = null;
		String name = "";
		if(args.length > 1) {
			if(!sender.hasPermission(ChatPermissions.NICKNAME_OTHER)) {
				sender.sendMessage("generic.no-command-perm");
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
				player = players.get(0);
			}
			
			name = args[1];
		} else {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			player = (PlayerSender) sender;
			name = args[0];
		}
		
		if(name.equalsIgnoreCase("off")) {
			this.module.clearName(player.getName());
			this.module.loadDisplayName((Player) BukkitCommandSender.unwrap(player));
			sender.sendMessage("chat.nick-removed", player.getName());
			player.sendMessage("chat.your-nick-removed");
		} else {
			this.module.setName((Player) BukkitCommandSender.unwrap(player), name.replaceAll(" ", "_"));
			String fmt = ChatColor.translateAlternateColorCodes('&', name);
			sender.sendMessage("chat.nick-changed", player.getName(), fmt);
			player.sendMessage("chat.your-nick-changed", fmt);
		}
	}
	
	@Command(aliases = {"whois", "realname"}, desc = "Gets the real name of a player.", usage = "<name>", min = 1, permission = ChatPermissions.WHOIS)
	public void whois(CommandSender sender, String command, String args[]) {
		List<String> results = new ArrayList<String>();
		for(PlayerSender player : this.module.getManager().getPlayerSenders()) {
			if(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', this.module.getName(player.getName()))).toLowerCase().contains(args[0].toLowerCase())) {
				results.add(player.getName());
			}
		}
		
		if(results.size() == 0) {
			sender.sendMessage("chat.name-not-found");
		} else if(results.size() > 1) {
			sender.sendMessage("generic.multiple-players");
		} else {
			sender.sendMessage("chat.is-really", ChatColor.translateAlternateColorCodes('&', this.module.getName(results.get(0))), results.get(0));
		}
	}
	
	@Command(aliases = {"staff", "mods"}, desc = "Lists all staff members currently online.", usage = "", permission = ChatPermissions.STAFF_LIST)
	public void staff(CommandSender sender, String command, String args[]) {
		StringBuilder build = new StringBuilder(ChatColor.RESET.toString());
		int count = 0;
		for(PlayerSender player : this.module.getManager().getPlayerSenders()) {
			if(player.hasPermission(ChatPermissions.MOD)) {
				if(count != 0) {
					build.append(ChatColor.RESET + ", " + ChatColor.RESET);
				}
				
				build.append(player.getDisplayName());
				count++;
			}
		}
		
		if(build.length() == 0) {
			sender.sendMessage("chat.no-staff-online");
		} else {
			sender.sendMessage(count == 1 ? "chat.staff-count-singular" : "chat.staff-count-plural", count);
			sender.sendMessage(build.toString());
		}
	}
	
	@Command(aliases = {"clearchat"}, desc = "Clears chat of messages.", usage = "[players/all]", permission = ChatPermissions.CLEAR_CHAT)
	public void clearchat(CommandSender sender, String command, String args[]) {
		PlayerSender players[] = null;
		if(args.length > 0) {
			if(!sender.hasPermission(ChatPermissions.CLEAR_OTHERS_CHAT)) {
				sender.sendMessage("generic.no-command-perm");
				return;
			}
			
			if(args[0].equals("all")) {
				players = this.module.getManager().getPlayerSenders();
			} else {
				players = new PlayerSender[args.length];
				for(int index = 0; index < args.length; index++) {
					List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[index]);
					if(matches.size() == 0) {
						sender.sendMessage("generic.player-not-found");
						continue;
					} else if(matches.size() > 1) {
						sender.sendMessage("generic.multiple-players");
						continue;
					} else {
						players[index] = matches.get(0);
					}
				}
			}
		} else {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			players = new PlayerSender[] { (PlayerSender) sender };
		}
		
		for(PlayerSender player : players) {
			if(player != null) {
				for(int i = 0; i < 100; i++) {
					player.sendMessage(" ");
				}
				
				player.sendMessage(ChatColor.GREEN + "Chat cleared.");
			}
		}
	}
	
	@Command(aliases = {"chatpass"}, desc = "Sets your webchat password.", usage = "<password>", min = 1, permission = ChatPermissions.SET_PASS, console = false, commandblock = false)
	public void chatpass(CommandSender sender, String command, String args[]) {
		String res = this.module.setPassword(sender.getName(), args[0]);
		sender.sendMessage("chat.webchat-set", res);
	}

	@Command(aliases = {"mute"}, desc = "Mutes a player.", usage = "<player>", min = 1, permission = ChatPermissions.MUTE)
	public void mute(CommandSender sender, String command, String args[]) {
		if(sender instanceof PlayerSender && this.module.isMuted(sender.getName())) {
			sender.sendMessage("chat.muted");
			return;
		}

		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		}

		PlayerSender player = matches.get(0);
		this.module.setMuted(player.getName(), true);
		sender.sendMessage("chat.player-muted");
	}

	@Command(aliases = {"unmute"}, desc = "Unmutes a player.", usage = "<player>", min = 1, permission = ChatPermissions.MUTE)
	public void unmute(CommandSender sender, String command, String args[]) {
		if(sender instanceof PlayerSender && this.module.isMuted(sender.getName())) {
			sender.sendMessage("chat.muted");
			return;
		}

		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		}

		PlayerSender player = matches.get(0);
		this.module.setMuted(player.getName(), false);
		sender.sendMessage("chat.player-unmuted");
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
