package com.peacecraftec.bukkit.eco.vault;

import java.util.ArrayList;
import java.util.List;

import com.peacecraftec.bukkit.eco.core.EcoManager;
import com.peacecraftec.module.ModuleManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class PeaceEcoVault implements Economy {

	private EcoManager manager;
	private ModuleManager modules;

	public PeaceEcoVault(EcoManager manager, ModuleManager modules) {
		this.manager = manager;
		this.modules = modules;
	}
	
	@Override
	public String getName() {
		return "PeacecraftEco";
	}
	
	@Override
	public String currencyNamePlural() {
		return "";
	}

	@Override
	public String currencyNameSingular() {
		return "";
	}

	@Override
	public String format(double amount) {
		return this.manager.format(amount);
	}

	@Override
	public int fractionalDigits() {
		return 2;
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public EconomyResponse isBankMember(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}
	
	@Override
	public EconomyResponse bankBalance(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "PeacecraftEco does not support bank accounts!");
	}
	
	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}
	
	@Override
	public boolean hasAccount(String player) {
		return this.hasAccount(player, this.modules.getDefaultWorld());
	}

	@Override
	public boolean hasAccount(String player, String world) {
		return this.manager.getWorld(world).getPlayer(player) != null;
	}

	@Override
	public boolean createPlayerAccount(String player) {
		return this.createPlayerAccount(player, this.modules.getDefaultWorld());
	}

	@Override
	public boolean createPlayerAccount(String player, String world) {
		if(this.hasAccount(player, world)) {
			return false;
		}

		this.manager.getWorld(world).addIfMissing(player);
		return true;
	}

	@Override
	public EconomyResponse depositPlayer(String player, double amount) {
		return this.depositPlayer(player, this.modules.getDefaultWorld(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(String player, String world, double amount) {
		if(amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		}
		
		if(!this.hasAccount(player, world)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player does not have an account in this world.");
		}
		
		this.manager.getWorld(world).getPlayer(player).addBalance(amount);
		return new EconomyResponse(amount, this.getBalance(player, world), ResponseType.SUCCESS, null);
	}
	
	@Override
	public EconomyResponse withdrawPlayer(String player, double amount) {
		return this.withdrawPlayer(player, this.modules.getDefaultWorld(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String player, String world, double amount) {
		if(amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}
		
		if(!this.hasAccount(player, world)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player does not have an account in this world.");
		}
		
		this.manager.getWorld(world).getPlayer(player).removeBalance(amount);
		return new EconomyResponse(amount, this.getBalance(player, world), ResponseType.SUCCESS, null);
	}

	@Override
	public double getBalance(String player) {
		return this.getBalance(player, this.modules.getDefaultWorld());
	}

	@Override
	public double getBalance(String player, String world) {
		return this.manager.getWorld(world).getPlayer(player).getBalance();
	}

	@Override
	public boolean has(String player, double amount) {
		return this.has(player, this.modules.getDefaultWorld(), amount);
	}

	@Override
	public boolean has(String player, String world, double amount) {
		return this.getBalance(player, world) > amount;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
