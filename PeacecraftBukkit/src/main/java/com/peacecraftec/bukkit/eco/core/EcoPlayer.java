package com.peacecraftec.bukkit.eco.core;

import java.util.UUID;

import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.storage.Storage;

public class EcoPlayer {

	private String key;
	private EcoWorld world;
	private Storage data;
	private PeacecraftEco module;
	
	public EcoPlayer(String key, EcoWorld world, Storage data, PeacecraftEco module) {
		this.key = key;
		this.world = world;
		this.data = data;
	}
	
	public String getName() {
		try {
			UUID uuid = UUID.fromString(this.key);
			return this.module.getManager().getUsername(uuid);
		} catch(IllegalArgumentException e) {
			return this.key;
		}
	}
	
	public EcoWorld getWorld() {
		return this.world;
	}
	
	public double getBalance() {
		return (double) Math.round(this.data.getDouble("players." + this.key + ".balance") * 100) / 100;
	}
	
	public void setBalance(double balance) {
		balance = (double) Math.round(balance * 100) / 100;
		this.data.setValue("players." + this.key + ".balance", balance);
		this.data.save();
	}
	
	public void addBalance(double balance) {
		this.setBalance(this.getBalance() + balance);
	}
	
	public void removeBalance(double balance) {
		this.setBalance(this.getBalance() - balance);
	}
	
}
