package com.peacecraftec.bukkit.internal.hook.selection;

import org.bukkit.Location;

public class Selection {

	private Location point1;
	private Location point2;
	
	public Selection(Location point1, Location point2) {
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public Location getFirstPoint() {
		return this.point1;
	}
	
	public Location getSecondPoint() {
		return this.point2;
	}
	
}
