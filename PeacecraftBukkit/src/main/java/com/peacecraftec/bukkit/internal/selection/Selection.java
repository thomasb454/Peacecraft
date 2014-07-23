package com.peacecraftec.bukkit.internal.selection;

import org.bukkit.Location;

public class Selection {
	private Location point1;
	private Location point2;
	
	public Selection(Location point1, Location point2) {
		if(point1 != null && point2 != null) {
			int x1 = point1.getBlockX();
			int y1 = point1.getBlockY();
			int z1 = point1.getBlockZ();
			int x2 = point2.getBlockX();
			int y2 = point2.getBlockY();
			int z2 = point2.getBlockZ();
			if(x2 < x1) {
				int old = x1;
				x1 = x2;
				x2 = old;
			}

			if(y2 < y1) {
				int old = y1;
				y1 = y2;
				y2 = old;
			}

			if(z2 < z1) {
				int old = z1;
				z1 = z2;
				z2 = old;
			}

			this.point1 = new Location(point1.getWorld(), x1, y1, z1);
			this.point2 = new Location(point2.getWorld(), x2, y2, z2);
		} else {
			this.point1 = point1;
			this.point2 = point2;
		}
	}
	
	public Location getFirstPoint() {
		return this.point1;
	}
	
	public Location getSecondPoint() {
		return this.point2;
	}

	public boolean isComplete() {
		return this.point1 != null && this.point2 != null;
	}
}
