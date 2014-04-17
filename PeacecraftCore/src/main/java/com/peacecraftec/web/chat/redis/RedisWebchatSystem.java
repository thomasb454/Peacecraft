package com.peacecraftec.web.chat.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.JsonObject;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.redis.RedisHashSet;
import com.peacecraftec.redis.RedisSet;
import com.peacecraftec.web.chat.WebchatSystem;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;
import com.peacecraftec.web.chat.data.WebMessage;

public class RedisWebchatSystem implements WebchatSystem {

	private static final String CHANNEL_OUT = "PeacecraftWebChatOut";
	private static final String CHANNEL_IN = "PeacecraftWebChatIn";
	
	private ModuleManager manager;
	private RedisDatabase db;
	private RedisHashSet passwords;
	private RedisSet mods;
	private RedisSet onlinePlayers;
	private WebchatPubSub pubsub;
	private List<WebMessage> incoming = new CopyOnWriteArrayList<WebMessage>();
	private List<ChannelAction> channelActions = new CopyOnWriteArrayList<ChannelAction>();
	
	public RedisWebchatSystem(ModuleManager manager) {
		this.manager = manager;
		this.db = new RedisDatabase("localhost");
		this.passwords = this.db.getHashSet("webchat.passwords");
		this.mods = this.db.getSet("webchat.mods");
		this.onlinePlayers = this.db.getSet("webchat.online");
		for(String uuid : this.onlinePlayers.all()) {
			this.removeOnlinePlayer(uuid);
		}
		
		new Thread() {
			public void run() {
				pubsub = new WebchatPubSub(RedisWebchatSystem.this.manager, RedisWebchatSystem.this, "localhost", CHANNEL_IN);
				pubsub.subscribe();
			}
		}.start();
	}
	
	@Override
	public void send(WebMessage message) {
		try {
			JsonObject json = new JsonObject();
			json.addProperty("type", "chat");
			UUID uuid = this.manager.getUUID(message.getPlayer());
			json.addProperty("player", message.getPlayer());
			json.addProperty("uuid", uuid != null ? uuid.toString() : null);
			json.addProperty(message.isToChannel() ? "channel" : "to", message.getTo());
			json.addProperty("message", message.getMessage());
			this.db.publish(CHANNEL_OUT, json.toString());
		} catch(Exception e) {
			System.err.println("Failed to publish chat message to webchat!");
			e.printStackTrace();
		}
	}
	
	@Override
	public void broadcast(String message) {
		this.send(new WebMessage("server", null, message, true));
	}
	
	@Override
	public void broadcast(String message, String channel) {
		this.send(new WebMessage("server", channel, message, true));
	}
	
	@Override
	public void sendMessage(String message, String player) {
		this.send(new WebMessage("server", player, message, false));
	}

	@Override
	public List<WebMessage> getIncoming() {
		List<WebMessage> ret = new ArrayList<WebMessage>(this.incoming);
		this.incoming.removeAll(ret);
		return ret;
	}
	
	@Override
	public void send(ChannelAction action) {
		try {
			JsonObject json = new JsonObject();
			json.addProperty("type", "channel");
			UUID uuid = this.manager.getUUID(action.getPlayer());
			json.addProperty("player", action.getPlayer());
			json.addProperty("uuid", uuid != null ? uuid.toString() : null);
			json.addProperty("channel", action.getChannel());
			json.addProperty("action", action.getAction().name().toLowerCase());
			this.db.publish(CHANNEL_OUT, json.toString());
		} catch(Exception e) {
			System.err.println("Failed to publish channel action to webchat!");
			e.printStackTrace();
		}
	}

	@Override
	public List<ChannelAction> getChannelActions() {
		List<ChannelAction> ret = new ArrayList<ChannelAction>(this.channelActions);
		this.channelActions.removeAll(ret);
		return ret;
	}
	
	@Override
	public ChannelData getChannelData(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return null;
		}
		
		return new RedisChannelData(this.manager, this, this.db, uuid);
	}
	
	public void addIncoming(WebMessage message) {
		this.incoming.add(message);
	}
	
	public void addChannelAction(ChannelAction action) {
		this.channelActions.add(action);
	}

	@Override
	public String setPassword(String user, String pass) {
		UUID uuid = this.manager.getUUID(user);
		if(uuid != null) {
			this.passwords.set(uuid.toString(), BCrypt.hashpw(pass, BCrypt.gensalt(10)));
		}
		
		return pass;
	}
	
	@Override
	public boolean isMod(String user) {
		UUID uuid = this.manager.getUUID(user);
		if(uuid == null) {
			return false;
		}
		
		return this.mods.contains(uuid.toString());
	}
	
	@Override
	public void setMod(String user, boolean mod) {
		UUID uuid = this.manager.getUUID(user);
		if(uuid != null) {
			if(mod && !this.mods.contains(uuid.toString())) {
				this.mods.add(uuid.toString());
			} else if(!mod && this.mods.contains(uuid.toString())) {
				this.mods.remove(uuid.toString());
			}
		}
	}

	@Override
	public void cleanup() {
		for(String uuid : this.onlinePlayers.all()) {
			this.onlinePlayers.remove(uuid);
		}
		
		this.pubsub.unsubscribe();
		this.db.cleanup();
	}

	@Override
	public void addOnlinePlayer(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid != null) {
			this.onlinePlayers.add(uuid.toString());
		}
	}

	@Override
	public void removeOnlinePlayer(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid != null) {
			this.onlinePlayers.remove(uuid.toString());
		}
	}

}
