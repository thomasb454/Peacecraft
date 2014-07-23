package com.peacecraftec.bukkit.protect.core.interact;

public class AccessAddAction implements InteractAction {
	private String players[];

	public AccessAddAction(String players[]) {
		this.players = players;
	}

	public String[] getPlayers() {
		return this.players;
	}
}
