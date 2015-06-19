package com.peacecraftec.bukkit.core.tp;

public class TpRequest {
    private TpRequestType type;
    private String sender;
    private String other;

    public TpRequest(TpRequestType type, String sender, String other) {
        this.type = type;
        this.sender = sender;
        this.other = other;
    }

    public TpRequestType getType() {
        return this.type;
    }

    public String getSender() {
        return this.sender;
    }

    public String getOther() {
        return this.other;
    }
}
