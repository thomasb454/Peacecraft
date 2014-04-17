package com.peacecraftec.bukkit.lots.command;

import java.util.Comparator;

import com.peacecraftec.bukkit.lots.Lot;

public class LotSorter implements Comparator<Lot> {

	@Override
	public int compare(Lot l1, Lot l2) {
		if(l1.getId() > l2.getId()) {
			return 1;
		} else if(l1.getId() < l2.getId()) {
			return -1;
		} else {
			return 0;
		}
	}

}
