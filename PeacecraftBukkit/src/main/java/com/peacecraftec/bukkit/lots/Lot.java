package com.peacecraftec.bukkit.lots;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class Lot {
	
	private PeacecraftLots module;
	private Town town;
	private int id;
	private int x1;
	private int y1;
	private int z1;
	private int x2;
	private int y2;
	private int z2;
	private int size;
	private double price;
	private boolean forsale;
	private UUID owner;
	private List<UUID> builders;
	
	// CONVERSION CODE
	private String unconvertedOwner;
	private List<String> unconvertedBuilders;
	// END CONVERSION CODE

	public Lot(PeacecraftLots module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, String owner, String builders) {
		this(module, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner, new ArrayList<UUID>());
		this.buildBuilderList(builders);
		this.buildUnconvertedList(builders);
	}
	
	public Lot(PeacecraftLots module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, UUID owner, List<UUID> builders) {
		this(module, town, id, x1, y1, z1, x2, y2, z2, size, price, forsale, owner != null ? owner.toString() : null, builders);
	}
	
	private Lot(PeacecraftLots module, Town town, int id, int x1, int y1, int z1, int x2, int y2, int z2, int size, double price, boolean forsale, String owner, List<UUID> builders) {
		this.module = module;
		this.town = town;
		this.id = id;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.size = size;
		this.price = price;
		this.forsale = forsale;
		// CONVERSION CODE
		try {
			this.owner = owner != null ? UUID.fromString(owner) : null;
		} catch(IllegalArgumentException e) {
			this.owner = null;
			this.unconvertedOwner = owner;
		}
		// END CONVERSION CODE
		
		this.builders = builders;
	}
	
	// CONVERSION CODE
	public String getUnconvertedOwner() {
		return this.unconvertedOwner;
	}
	
	public List<String> getUnconvertedBuilders() {
		return this.unconvertedBuilders != null ? new ArrayList<String>(this.unconvertedBuilders) : null;
	}
	
	public void setConvertedOwner(UUID owner) {
		this.owner = owner;
		this.unconvertedOwner = null;
	}
	
	public void addConvertedBuilder(String player, UUID uuid) {
		if(!this.builders.contains(uuid)) {
			this.builders.add(uuid);
		}
		
		if(this.unconvertedBuilders != null) {
			this.unconvertedBuilders.remove(player);
			if(this.unconvertedBuilders.size() == 0) {
				this.unconvertedBuilders = null;
			}
		}
	}
	// END CONVERSION CODE
	
	public Town getTown() {
		return this.town;
	}
	
	public void setTownInstance(Town town) {
		this.town = town;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public double getPrice() {
		if(this.price <= 0) {
			return this.town.getLotPrice(this.size / (this.y2 - this.y1 + 1));
		}
		
		return this.price;
	}
	
	public double getRawPrice() {
		return this.price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public double getRent() {
		return this.town.getLotRent(this.getSize());
	}
	
	public boolean isForSale() {
		return this.forsale;
	}
	
	public void setForSale(boolean forsale) {
		this.forsale = forsale;
	}
	
	public String getOwner() {
		// CONVERSION CODE
		if(this.unconvertedOwner != null) {
			return this.unconvertedOwner;
		}
		// END CONVERSION CODE
		
		if(this.owner == null) {
			return "";
		}
		
		return this.module.getManager().getUsername(this.owner);
	}
	
	public UUID getOwnerUUID() {
		return this.owner;
	}
	
	public void setOwner(String owner) {
		if(owner == null) {
			owner = "";
		}
		
		this.owner = this.module.getManager().getUUID(owner);
	}
	
	public String getBuildersString() {
		return this.buildBuilderString();
	}
	
	public List<String> getBuilders() {
		List<String> ret = new ArrayList<String>();
		for(UUID uuid : this.builders) {
			ret.add(this.module.getManager().getUsername(uuid));
		}
		
		// CONVERSION CODE
		if(this.unconvertedBuilders != null) {
			ret.addAll(this.unconvertedBuilders);
		}
		// END CONVERSION CODE
		
		return ret;
	}
	
	public void addBuilder(String builder) {
		UUID uuid = this.module.getManager().getUUID(builder);
		if(uuid != null) {
			this.builders.add(uuid);
		}
	}
	
	public void removeBuilder(String builder) {
		UUID uuid = this.module.getManager().getUUID(builder);
		if(uuid != null) {
			this.builders.remove(uuid);
		}
	}
	
	public World getWorld() {
		return this.town.getWorld();
	}
	
	public int getX1() {
		return this.x1;
	}
	
	public int getY1() {
		return this.y1;
	}
	
	public int getZ1() {
		return this.z1;
	}
	
	public int getX2() {
		return this.x2;
	}
	
	public int getY2() {
		return this.y2;
	}
	
	public int getZ2() {
		return this.z2;
	}
	
	private void buildBuilderList(String builders) {
		String parts[] = builders.split(":");
		this.builders = new ArrayList<UUID>();
		for(String uuid : parts) {
			// CONVERSION CODE - try/catch
			try {
				this.builders.add(UUID.fromString(uuid));
			} catch(IllegalArgumentException e) {
			}
		}
	}
	
	// CONVERSION CODE
	private void buildUnconvertedList(String builders) {
		String parts[] = builders.split(":");
		this.unconvertedBuilders = new ArrayList<String>();
		for(String player : parts) {
			try {
				UUID.fromString(player);
			} catch(IllegalArgumentException e) {
				this.unconvertedBuilders.add(player);
			}
		}
		
		if(this.unconvertedBuilders.size() == 0) {
			this.unconvertedBuilders = null;
		}
	}
	// END CONVERSION CODE
	
	private String buildBuilderString() {
		StringBuilder build = new StringBuilder();
		for(UUID builder : this.builders) {
			if(build.length() > 0) {
				build.append(":");
			}
			
			build.append(builder.toString());
		}
		
		// CONVERSION CODE
		if(this.unconvertedBuilders != null) {
			for(String builder : this.unconvertedBuilders) {
				if(build.length() > 0) {
					build.append(":");
				}
				
				build.append(builder);
			}
		}
		// END CONVERSION CODE
		
		return build.toString();
	}

	public boolean canBuild(Player player) {
		return player.getUniqueId().equals(this.owner) || this.getBuilders().contains(player.getName()) || this.getBuilders().contains("#everyone");
	}
	
	public boolean canInteract(Player player) {
		return this.canBuild(player) || this.getBuilders().contains("#everyoneinteract");
	}

	public void setBounds(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
}
