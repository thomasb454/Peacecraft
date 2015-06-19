package com.peacecraftec.web.chat.data;

public class ChannelAction {

    private String player;
    private String channel;
    private Action action;

    public ChannelAction(String player, String channel, Action action) {
        if(channel.equalsIgnoreCase("g")) {
            channel = "global";
        }

        this.player = player;
        this.channel = channel;
        this.action = action;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getChannel() {
        return this.channel;
    }

    public Action getAction() {
        return this.action;
    }

    public enum Action {
        JOIN,
        LEAVE,
        CHANGED_ACTIVE;
    }

}
