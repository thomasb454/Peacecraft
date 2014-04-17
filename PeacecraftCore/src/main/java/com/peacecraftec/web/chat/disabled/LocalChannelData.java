package com.peacecraftec.web.chat.disabled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.chat.data.ChannelData;

public class LocalChannelData implements ChannelData {
	
	private ModuleManager manager;
	private UUID uuid;
	private List<String> channels = Arrays.asList("global");
	private String activeChannel = "global";
	
	public LocalChannelData(ModuleManager manager, UUID uuid) {
		this.manager = manager;
		this.uuid = uuid;
	}
	
	@Override
	public String getPlayer() {
		return this.manager.getUsername(this.uuid);
	}

	@Override
	public List<String> getChannels() {
		return new ArrayList<String>(this.channels);
	}

	@Override
	public boolean isInChannel(String channel) {
		return this.channels.contains(channel.toLowerCase());
	}

	@Override
	public void addChannel(String channel) {
		this.channels.add(channel.toLowerCase());
	}

	@Override
	public void removeChannel(String channel) {
		this.channels.remove(channel.toLowerCase());
	}

	@Override
	public String getActiveChannel() {
		return this.activeChannel;
	}

	@Override
	public void setActiveChannel(String channel) {
		this.activeChannel = channel.toLowerCase();
	}

}
