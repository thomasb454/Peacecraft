package com.peacecraftec.bukkit.eco.command;

import java.util.Comparator;

import com.peacecraftec.bukkit.eco.core.EcoPlayer;

public class BalanceComparator implements Comparator<EcoPlayer> {

	@Override
	public int compare(EcoPlayer p1, EcoPlayer p2) {
		if(p2.getBalance() > p1.getBalance()) {
			return 1;
		} else if(p2.getBalance() < p1.getBalance()) {
			return -1;
		} else {
			return 0;
		}
	}

}
