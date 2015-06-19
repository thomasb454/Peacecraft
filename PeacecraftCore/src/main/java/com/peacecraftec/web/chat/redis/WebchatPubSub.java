package com.peacecraftec.web.chat.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.redis.RedisPubSub;
import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.WebMessage;

import java.util.UUID;

public class WebchatPubSub extends RedisPubSub {

    private ModuleManager manager;
    private RedisWebchatSystem chat;

    public WebchatPubSub(ModuleManager manager, RedisWebchatSystem chat, RedisDatabase database, String... channels) {
        super(database, channels);
        this.manager = manager;
        this.chat = chat;
    }

    @Override
    public void recieve(String channel, String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        String type = json.get("type").getAsString();
        String player = this.manager.getUsername(UUID.fromString(json.get("uuid").getAsString()));
        boolean ch = json.has("channel");
        String to = ch ? json.get("channel").getAsString() : json.get("to").getAsString();
        if(type.equals("channel")) {
            ChannelAction.Action action = ChannelAction.Action.valueOf(json.get("action").getAsString().toUpperCase());
            this.chat.addChannelAction(new ChannelAction(player, to, action));
        } else if(type.equals("chat")) {
            String msg = json.get("message").getAsString();
            this.chat.addIncoming(new WebMessage(player, to, msg, ch));
        }
    }

    @Override
    public void onSubscribe(String channel, long count) {
    }

    @Override
    public void onUnsubscribe(String channel, long count) {
    }

}
