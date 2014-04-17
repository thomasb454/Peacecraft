package com.peacecraftec.bukkit.portals.core;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

public class PortalManager {

	private PeacecraftPortals module;
	private Storage portals;
	
	public PortalManager(PeacecraftPortals module) {
		this.module = module;
		this.reload();
	}
	
	public void createPortal(String name, Location p1, Location p2) {
		this.portals.setValue("portals." + name + ".location", p1.getX() + "," + p1.getY() + "," + p1.getZ() + ":" + p2.getX() + "," + p2.getY() + "," + p2.getZ());
		this.portals.setValue("portals." + name + ".world", p1.getWorld().getName());
		this.portals.setValue("portals." + name + ".direction", BlockFace.NORTH.name());
		this.portals.save();
	}
	
	public void removePortal(String name) {
		this.portals.remove("portals." + name);
		this.portals.save();
	}
	
	public boolean isPortal(String portal) {
		return this.portals.contains("portals." + portal);
	}
	
	public String getDestPortal(String portal) {
		if(this.portals.contains("portals." + portal + ".destination")) {
			String dest = this.portals.getString("portals." + portal + ".destination");
			if(dest != null && dest.equals("")) {
				return null;
			}
			
			return dest;
		}
		
		return null;
	}
	
	public void setDestination(String portal, String dest) {
		this.portals.setValue("portals." + portal + ".destination", dest);
		this.portals.save();
	}
	
	public void setDirection(String portal, BlockFace dir) {
		this.portals.setValue("portals." + portal + ".direction", dir.name());
		this.portals.save();
	}
	
	public String getPortal(Location loc) {
		for(String portal : this.portals.getRelativeKeys("portals", false)) {
			Location points[] = this.getPoints(portal);
			if(points != null && points[0] != null && points[1] != null) {
				if(loc.getBlockX() >= points[0].getX() && loc.getBlockY() >= points[0].getY() && loc.getBlockZ() >= points[0].getZ() && loc.getBlockX() <= points[1].getX() && loc.getBlockY() <= points[1].getY() && loc.getBlockZ() <= points[1].getZ()) {
					return portal;
				}
			}
		}
		
		return null;
	}
	
	public Location getPortalPoint(String portal) {
		if(!this.portals.contains("portals." + portal)) {
			return null;
		}
		
		Location points[] = this.getPoints(portal);
		double x = Math.floor(points[1].getX() - (points[1].getX() - points[0].getX()) / 2.0) + 0.5;
		double y = Math.floor(points[1].getY() - (points[1].getY() - points[0].getY()) / 2.0);
		double z = Math.floor(points[1].getZ() - (points[1].getZ() - points[0].getZ()) / 2.0) + 0.5;
		Location ret = new Location(points[0].getWorld(), x, y, z);
		if(this.portals.contains("portals." + portal + ".direction")) {
			String dirString = this.portals.getString("portals." + portal + ".direction");
			BlockFace face = null;
			try {
				face = BlockFace.valueOf(dirString.toUpperCase());
				if(face == null) {
					this.module.getLogger().warning("Invalid location found");
					return null;
				}
			} catch(IllegalArgumentException e) {
				this.module.getLogger().warning("Invalid location found");
				return null;
			}
			
			float yaw = 0;
			float pitch = 0;
			switch(face) {
				case UP:
					pitch = -90;
					break;
				case DOWN:
					pitch = 90;
					break;
				case NORTH:
					yaw = 180;
					break;
				case SOUTH:
					yaw = 0;
					break;
				case EAST:
					yaw = 270;
					break;
				case WEST:
					yaw = 90;
					break;
				case NORTH_EAST:
					yaw = 225;
					break;
				case NORTH_WEST:
					yaw = 135;
					break;
				case SOUTH_EAST:
					yaw = 315;
					break;
				case SOUTH_WEST:
					yaw = 45;
					break;
				default:
					break;
			}
			
			ret.add(new Vector(face.getModX(), face.getModY(), face.getModZ()));
			ret.setPitch(pitch);
			ret.setYaw(yaw);
		}
		
		return ret;
	}
	
	public Location[] getPoints(String portal) {
		String loc1String = this.portals.getString("portals." + portal + ".location").split(":")[0];
		String loc2String = this.portals.getString("portals." + portal + ".location").split(":")[1];
		String worldString = this.portals.getString("portals." + portal + ".world");
		return new Location[] { this.build(worldString, loc1String), this.build(worldString, loc2String) };
	}
	
	private Location build(String worldString, String locString) {
		World world = Bukkit.getServer().getWorld(worldString);
		if(world == null) {
			this.module.getLogger().warning("World \"" + worldString + "\" not found in portal location!");
			return null;
		}
		
		String split[] = locString.split(",");
		double x = 0;
		double y = 0;
		double z = 0;
		try {
			x = Double.parseDouble(split[0]);
			y = Double.parseDouble(split[1]);
			z = Double.parseDouble(split[2]);
		} catch(Exception e) {
			this.module.getLogger().warning("Invalid location found");
			return null;
		}
		
		return new Location(world, x, y, z);
	}
	
	public void reload() {
		this.portals = new YamlStorage(new File(this.module.getDirectory(), "portals.yml").getPath());
		this.portals.load();
	}
	
}
