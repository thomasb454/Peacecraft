package com.peacecraftec.bukkit.protect.core.interact;

public class AccessRemoveAction implements InteractAction {
    private String players[];

    public AccessRemoveAction(String players[]) {
        this.players = players;
    }

    public String[] getPlayers() {
        return this.players;
    }
}
