package com.peacecraftec.web.chat.redis;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedisChannelData implements ChannelData {

    private ModuleManager manager;
    private RedisWebchatSystem system;
    private RedisDatabase db;
    private UUID uuid;

    public RedisChannelData(ModuleManager manager, RedisWebchatSystem system, RedisDatabase db, UUID uuid) {
        this.manager = manager;
        this.system = system;
        this.db = db;
        this.uuid = uuid;
    }

    public String getPlayer() {
        return this.manager.getUsername(this.uuid);
    }

    public List<String> getChannels() {
        if(!this.db.contains("channels." + this.uuid.toString())) {
            this.addChannel("global");
        }

        return new ArrayList<String>(this.db.getSet("channels." + this.uuid.toString()).all());
    }

    public boolean isInChannel(String channel) {
        return this.getChannels().contains(channel.toLowerCase());
    }

    public void addChannel(String channel) {
        this.db.getSet("channels." + this.uuid.toString()).add(channel.toLowerCase());
        this.system.send(new ChannelAction(this.getPlayer(), channel, ChannelAction.Action.JOIN));
    }

    public void removeChannel(String channel) {
        this.db.getSet("channels." + this.uuid.toString()).remove(channel.toLowerCase());
        this.system.send(new ChannelAction(this.getPlayer(), channel, ChannelAction.Action.LEAVE));
        if(this.getChannels().size() == 0) {
            this.addChannel("global");
        }

        if(this.getActiveChannel().equals(channel)) {
            this.setActiveChannel(this.getChannels().get(0));
        }
    }

    public String getActiveChannel() {
        if(!this.db.contains("channels.active." + this.uuid.toString())) {
            this.setActiveChannel("global");
        }

        return this.db.getString("channels.active." + this.uuid.toString());
    }

    public void setActiveChannel(String channel) {
        this.db.setValue("channels.active." + this.uuid.toString(), channel.toLowerCase());
        this.system.send(new ChannelAction(this.getPlayer(), channel, ChannelAction.Action.CHANGED_ACTIVE));
    }

}
