package com.peacecraftec.web.chat.disabled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.chat.WebchatSystem;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;
import com.peacecraftec.web.chat.data.WebMessage;

public class DisabledWebchatSystem implements WebchatSystem {

	private Map<UUID, ChannelData> channelData = new HashMap<UUID, ChannelData>();
	private ModuleManager manager;
	
	public DisabledWebchatSystem(ModuleManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void send(WebMessage message) {
	}

	@Override
	public void broadcast(String message) {
	}

	@Override
	public void broadcast(String message, String channel) {
	}
	
	@Override
	public void sendMessage(String message, String player) {
	}

	@Override
	public List<WebMessage> getIncoming() {
		return new ArrayList<WebMessage>();
	}

	@Override
	public void send(ChannelAction action) {
	}

	@Override
	public List<ChannelAction> getChannelActions() {
		return new ArrayList<ChannelAction>();
	}

	@Override
	public ChannelData getChannelData(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return null;
		}
		
		if(!this.channelData.containsKey(uuid)) {
			this.channelData.put(uuid, new LocalChannelData(this.manager, uuid));
		}
		
		return this.channelData.get(uuid);
	}

	@Override
	public void addOnlinePlayer(String player) {
	}

	@Override
	public void removeOnlinePlayer(String player) {
	}

	@Override
	public String setPassword(String user, String pass) {
		return pass;
	}
	
	@Override
	public boolean isMod(String user) {
		return false;
	}

	@Override
	public void setMod(String user, boolean mod) {
	}

	@Override
	public void cleanup() {
	}

}
