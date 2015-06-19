package com.peacecraftec.web.chat;

import com.peacecraftec.web.chat.data.ChannelAction;
import com.peacecraftec.web.chat.data.ChannelData;
import com.peacecraftec.web.chat.data.WebMessage;

import java.util.List;

public interface WebchatSystem {

    public void send(WebMessage message);

    public void broadcast(String message);

    public void broadcast(String message, String channel);

    public void sendMessage(String message, String player);

    public List<WebMessage> getIncoming();

    public void send(ChannelAction action);

    public List<ChannelAction> getChannelActions();

    public ChannelData getChannelData(String player);

    public void addOnlinePlayer(String player);

    public void removeOnlinePlayer(String player);

    public String setPassword(String user, String pass);

    public boolean isMod(String user);

    public void setMod(String user, boolean mod);

    public void cleanup();

}
