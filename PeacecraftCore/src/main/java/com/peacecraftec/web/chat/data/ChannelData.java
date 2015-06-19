package com.peacecraftec.web.chat.data;

import java.util.List;

public interface ChannelData {

    public String getPlayer();

    public List<String> getChannels();

    public boolean isInChannel(String channel);

    public void addChannel(String channel);

    public void removeChannel(String channel);

    public String getActiveChannel();

    public void setActiveChannel(String channel);

}
