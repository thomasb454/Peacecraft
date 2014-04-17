package com.peacecraftec.bukkit.chat;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.peacecraftec.bukkit.chat.command.ChatCommands;
import com.peacecraftec.bukkit.chat.listener.ChatListener;
import com.peacecraftec.bukkit.donation.PeacecraftDonation;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.bukkit.internal.hook.VaultAPI;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;
import com.peacecraftec.web.chat.WebchatFactory;
import com.peacecraftec.web.chat.WebchatSystem;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;
import com.peacecraftec.web.chat.data.WebMessage;

public class PeacecraftChat extends Module {

	private Storage nicknames;
	private WebchatSystem webchat;
	
	public PeacecraftChat(ModuleManager manager) {
		super("Chat", manager);
	}
	
	@Override
	public void onEnable() {
		this.webchat = WebchatFactory.create(this.getManager());
		this.nicknames = new YamlStorage(new File(this.getDirectory(), "nicknames.yml").getPath());
		this.nicknames.load();
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
		this.nicknames.save();
		this.nicknames = null;
		this.webchat.cleanup();
		this.webchat = null;
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			player.setDisplayName(player.getName());
			player.setPlayerListName(player.getName());
		}
	}
	
	@Override
	public void reload() {
		this.nicknames.save();
		this.webchat.cleanup();
		this.webchat = null;
		this.webchat = WebchatFactory.create(this.getManager());
		this.nicknames = new YamlStorage(new File(this.getDirectory(), "nicknames.yml").getPath());
		this.nicknames.load();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.loadDisplayName(player);
			this.addOnlinePlayer(player.getName());
		}
	}
	
	public boolean hasName(String player) {
		this.convert(player); // CONVERSION CODE
		return this.nicknames.contains(this.getManager().getUUID(player).toString());
	}
	
	public String getName(String player) {
		UUID uuid = this.getManager().getUUID(player);
		return uuid != null && this.hasName(player) ? this.nicknames.getString(uuid.toString()) : this.getManager().getCasedUsername(player);
	}
	
	public void setName(Player player, String name) {
		this.convert(player.getName()); // CONVERSION CODE
		UUID uuid = this.getManager().getUUID(player.getName());
		if(uuid != null) {
			this.nicknames.setValue(uuid.toString(), name);
			this.nicknames.save();
			this.loadDisplayName(player);
		}
	}
	
	public void clearName(String player) {
		this.convert(player); // CONVERSION CODE
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			this.nicknames.remove(uuid.toString());
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
		} else if(VaultAPI.getChat() != null) {
			name = VaultAPI.getChat().getPlayerPrefix(world, player) + name + VaultAPI.getChat().getPlayerSuffix(world, player); 
		}
		
		if(this.getManager().getModule("Donation") != null && ((PeacecraftDonation) this.getManager().getModule("Donation")).getStorage().isDonor(player)) {
			name = ((PeacecraftDonation) this.getManager().getModule("Donation")).getPrefix() + ChatColor.RESET + name;
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
		} else if(VaultAPI.getChat() != null) {
			String pre = ChatColor.getLastColors(VaultAPI.getChat().getPlayerPrefix(world, player));
			String suf = ChatColor.getLastColors(VaultAPI.getChat().getPlayerSuffix(world, player));
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
	
	// CONVERSION CODE
	private void convert(String name) {
		String player = name;
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			if(this.nicknames.contains(player)) {
				this.nicknames.setValue(uuid.toString(), this.nicknames.getString(player));
				this.nicknames.remove(player);
				this.nicknames.save();
			}
		} else {
			this.getLogger().severe("Player " + name + " does not have a UUID to convert data to!");
		}
	}
	// END CONVERSION CODE
	
}
