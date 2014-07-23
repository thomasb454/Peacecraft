package com.peacecraftec.bukkit.chat;

import com.peacecraftec.bukkit.chat.command.ChatCommands;
import com.peacecraftec.bukkit.chat.listener.ChatListener;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.redis.RedisHashSet;
import com.peacecraftec.redis.RedisSet;
import com.peacecraftec.web.chat.WebchatFactory;
import com.peacecraftec.web.chat.WebchatSystem;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;
import com.peacecraftec.web.chat.data.WebMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PeacecraftChat extends Module {

	private RedisHashSet nicknames;
	private RedisSet muted;
	private WebchatSystem webchat;
	
	public PeacecraftChat(String name, ModuleManager manager) {
		super(name, manager);
	}
	
	@Override
	public void onEnable() {
		this.webchat = WebchatFactory.create(this.getManager());
		this.nicknames = this.getManager().getDatabase().getHashSet("nicknames");
		this.muted = this.getManager().getDatabase().getSet("muted");
		this.getManager().getPermissionManager().register(this, ChatPermissions.class);
		this.getManager().getCommandManager().register(this, new ChatCommands(this));
		this.getManager().getEventManager().register(this, new ChatListener(this));
		this.getManager().getScheduler().runTaskTimer(this, new WebchatTask(this), 5, 5);
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.loadDisplayName(player);
			this.addOnlinePlayer(player.getName());
		}
	}
	
	@Override
	public void onDisable() {
		this.nicknames = null;
		this.muted = null;
		this.webchat.cleanup();
		this.webchat = null;
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.setDisplayName(player.getName());
			player.setPlayerListName(player.getName());
		}
	}
	
	@Override
	public void reload() {
		this.webchat.cleanup();
		this.webchat = null;
		this.webchat = WebchatFactory.create(this.getManager());
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.loadDisplayName(player);
			this.addOnlinePlayer(player.getName());
		}
	}

	public boolean hasName(String player) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			return this.nicknames.contains(uuid.toString());
		}

		return false;
	}
	
	public String getName(String player) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null && this.hasName(player)) {
			return this.nicknames.get(uuid.toString());
		}

		return this.getManager().getCasedUsername(player);
	}
	
	public void setName(Player player, String name) {
		UUID uuid = this.getManager().getUUID(player.getName());
		if(uuid != null) {
			this.nicknames.put(uuid.toString(), name);
			this.loadDisplayName(player);
		}
	}
	
	public void clearName(String player) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			this.nicknames.remove(uuid.toString());
		}
	}

	public boolean isMuted(String player) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			return this.muted.contains(uuid.toString());
		}

		return false;
	}

	public void setMuted(String player, boolean muted) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			if(muted) {
				this.muted.add(uuid.toString());
			} else {
				this.muted.remove(uuid.toString());
			}
		}
	}
	
	public String getDisplayName(Player player) {
		return this.getDisplayName(player.getName(), player.getWorld().getName());
	}
	
	public String getDisplayName(String player, String world) {
		String name = ChatColor.translateAlternateColorCodes('&', this.getName(player));
		if(this.getManager().getModule("Permissions") != null) {
			PeacecraftPerms perms = (PeacecraftPerms) this.getManager().getModule("Permissions");
			name = perms.getPermsManager().getWorld(world).getPlayer(player).getPrefix() + name + perms.getPermsManager().getWorld(world).getPlayer(player).getSuffix();
		}
		
		return ChatColor.RESET + name + ChatColor.RESET;
	}
	
	public String getListName(Player player) {
		return this.getListName(player.getName(), player.getWorld().getName());
	}
	
	public String getListName(String player, String world) {
		String name = ChatColor.translateAlternateColorCodes('&', this.getName(player));
		if(this.getManager().getModule("Permissions") != null) {
			PeacecraftPerms perms = (PeacecraftPerms) this.getManager().getModule("Permissions");
			String pre = ChatColor.getLastColors(perms.getPermsManager().getWorld(world).getPlayer(player).getPrefix());
			String suf = ChatColor.getLastColors(perms.getPermsManager().getWorld(world).getPlayer(player).getSuffix());
			name = pre + name + suf; 
		}
		
		name = name.substring(0, Math.min(name.length(), 15));
		if(name.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
			name = name.substring(0, name.length() - 1);
		}
		
		return name;
	}
	
	public void loadDisplayName(Player player) {
		try {
			player.setDisplayName(this.getDisplayName(player));
			player.setPlayerListName(this.getListName(player));
		} catch(Exception e) {
			this.getLogger().severe("Failed to load display name for player \"" + player.getName() + "\".");
			e.printStackTrace();
		}
	}
	
	public ChannelData getChannelData(String player) {
		return this.webchat.getChannelData(player);
	}
	
	public void sendToChannel(String p, String channel, String message, Object... args) {
		if(channel.equalsIgnoreCase("mod")) {
			this.sendToMod(p, message);
			return;
		}
		
		if(p == null) {
			message = this.getManager().getLanguageManager().getDefault().translate(message, args);
		}
		
		this.getLogger().info("[" + channel + "] " + message);
		if(p == null) {
			this.broadcastWeb(message, channel);
		} else {
			this.sendWebchat(p, channel, message);
		}
		
		for(PlayerSender player : this.getManager().getPlayerSenders()) {
			if(this.getChannelData(player.getName()).isInChannel(channel)) {
				player.sendMessage(message, args);
			}
		}
	}
	
	public void sendToMod(String p, String message, Object... args) {
		if(p == null) {
			message = this.getManager().getLanguageManager().getDefault().translate(message, args);
		}
		
		this.getLogger().info("[MOD] " + message);
		if(p == null) {
			this.broadcastWeb(message, "mod");
		} else {
			this.sendWebchat(p, "mod", message);
		}
		
		for(PlayerSender player : this.getManager().getPlayerSenders()) {
			if(player.hasPermission(ChatPermissions.MOD)) {
				player.sendMessage(message, args);
			}
		}
	}
	
	public void sendWebchat(final String player, final String channel, final String message) {
		this.getManager().getScheduler().callSync(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				webchat.send(new WebMessage(player, channel.toLowerCase(), message, true));
				return null;
			}
		});
	}
	
	public void broadcastWeb(final String message) {
		this.getManager().getScheduler().callSync(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				webchat.broadcast(message);
				return null;
			}
		});
	}
	
	public void broadcastWeb(final String message, final String channel) {
		this.getManager().getScheduler().callSync(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				webchat.broadcast(message, channel.toLowerCase());
				return null;
			}
		});
	}
	
	public void sendWebchatMessage(final String player, final String message) {
		this.getManager().getScheduler().callSync(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				webchat.sendMessage(message, player);
				return null;
			}
		});
	}
	
	public List<WebMessage> getIncoming() {
		return this.webchat.getIncoming();
	}
	
	public List<ChannelAction> getChannelActions() {
		return this.webchat.getChannelActions();
	}
	
	public void addOnlinePlayer(String player) {
		this.webchat.addOnlinePlayer(player);
	}
	
	public void removeOnlinePlayer(String player) {
		this.webchat.removeOnlinePlayer(player);
	}

	public String setPassword(String user, String pass) {
		return this.webchat.setPassword(user, pass);
	}
	
	public void setMod(String user, boolean mod) {
		this.webchat.setMod(user, mod);
	}

}
