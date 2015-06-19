package com.peacecraftec.web.chat.data;

public class WebMessage {

    private String player;
    private String to;
    private String message;
    private boolean channel;

    public WebMessage(String player, String to, String message, boolean channel) {
        if(to != null && to.equalsIgnoreCase("g")) {
            to = "global";
        }

        this.player = player;
        this.to = to != null ? to.toLowerCase() : to;
        this.message = message;
        this.channel = channel;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getTo() {
        return this.to;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isToChannel() {
        return this.channel;
    }

}
