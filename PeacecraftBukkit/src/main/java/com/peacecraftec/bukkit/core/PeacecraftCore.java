package com.peacecraftec.bukkit.core;

import com.peacecraftec.bukkit.core.command.CoreCommands;
import com.peacecraftec.bukkit.core.listener.CoreListener;
import com.peacecraftec.bukkit.core.tp.TpRequest;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class PeacecraftCore extends Module {
	private List<String> invisible = new ArrayList<String>();
	private Map<String, Location> back = new HashMap<String, Location>();
	private Map<String, TpRequest> requests = new HashMap<String, TpRequest>();

	public PeacecraftCore(String name, ModuleManager manager) {
		super(name, manager);
	}
	
	@Override
	public void onEnable() {
		this.getManager().getCommandManager().register(this, new CoreCommands(this));
		this.getManager().getPermissionManager().register(this, CorePermissions.class);
		this.getManager().getEventManager().register(this, new CoreListener(this));
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.getManager().setUserPair(player.getUniqueId(), player.getName());
		}
	}

	@Override
	public void onDisable() {
	}
	
	@Override
	public void reload() {
	}

	public boolean isInvisible(String player) {
		return this.invisible.contains(player);
	}

	public void setInvisible(String player, boolean invisible) {
		if(invisible) {
			this.invisible.add(player);
		} else {
			this.invisible.remove(player);
		}
	}

	public Location getBackLocation(String player) {
		return this.back.get(player);
	}

	public void setBackLocation(String player, Location loc) {
		if(loc == null) {
			this.back.remove(player);
		} else {
			this.back.put(player, loc);
		}
	}

	public TpRequest getTpRequest(String player) {
		return this.requests.get(player);
	}

	public void setTpRequest(String player, TpRequest request) {
		if(request == null) {
			this.requests.remove(player);
		} else {
			for(String p : this.requests.keySet()) {
				TpRequest r = this.requests.get(p);
				if(r.getSender().equals(request.getSender())) {
					this.requests.remove(p);
				}
			}

			this.requests.put(player, request);
		}
	}

	public Location getHome(String player) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid == null) {
			return null;
		}

		if(!this.getManager().getDatabase().contains("homes." + uuid.toString() + ".world")) {
			return null;
		}

		String w = this.getManager().getDatabase().getString("homes." + uuid.toString() + ".world");
		World world = Bukkit.getServer().getWorld(w);
		if(world == null) {
			if(this.getManager().isEnabled("Worlds")) {
				((PeacecraftWorlds) this.getManager().getModule("Worlds")).getWorldManager().loadWorld(w);
			}

			world = Bukkit.getServer().getWorld(w);
			if(world == null) {
				return null;
			}
		}

		float yaw = 0;
		float pitch = 0;
		if(this.getManager().getDatabase().contains("homes." + uuid.toString() + ".yaw")) {
			yaw = this.getManager().getDatabase().getFloat("homes." + uuid.toString() + ".yaw");
		}

		if(this.getManager().getDatabase().contains("homes." + uuid.toString() + ".pitch")) {
			pitch = this.getManager().getDatabase().getFloat("homes." + uuid.toString() + ".pitch");
		}

		return new Location(world, this.getManager().getDatabase().getDouble("homes." + uuid.toString() + ".x"), this.getManager().getDatabase().getDouble("homes." + uuid.toString() + ".y"), this.getManager().getDatabase().getDouble("homes." + uuid.toString() + ".z"), yaw, pitch);
	}

	public void setHome(String player, Location loc) {
		UUID uuid = this.getManager().getUUID(player);
		if(uuid != null) {
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".world", loc.getWorld().getName());
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".x", loc.getX());
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".y", loc.getY());
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".z", loc.getZ());
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".yaw", loc.getYaw());
			this.getManager().getDatabase().setValue("homes." + uuid.toString() + ".pitch", loc.getPitch());
		}
	}

	public Set<String> getWarps() {
		Set<String> keys = this.getManager().getDatabase().getKeys("warps");
		Set<String> ret = new HashSet<String>();
		for(String key : keys) {
			if(key.endsWith(".world")) {
				ret.add(key.substring("warps.".length(), key.length() - ".world".length()));
			}
		}

		return ret;
	}

	public Location getWarp(String name) {
		if(!this.getManager().getDatabase().contains("warps." + name + ".world")) {
			return null;
		}

		String w = this.getManager().getDatabase().getString("warps." + name + ".world");
		World world = Bukkit.getServer().getWorld(w);
		if(world == null) {
			if(this.getManager().isEnabled("Worlds")) {
				((PeacecraftWorlds) this.getManager().getModule("Worlds")).getWorldManager().loadWorld(w);
			}

			world = Bukkit.getServer().getWorld(w);
			if(world == null) {
				return null;
			}
		}

		float yaw = 0;
		float pitch = 0;
		if(this.getManager().getDatabase().contains("warps." + name + ".yaw")) {
			yaw = this.getManager().getDatabase().getFloat("warps." + name + ".yaw");
		}

		if(this.getManager().getDatabase().contains("warps." + name + ".pitch")) {
			pitch = this.getManager().getDatabase().getFloat("warps." + name + ".pitch");
		}

		return new Location(world, this.getManager().getDatabase().getDouble("warps." + name + ".x"), this.getManager().getDatabase().getDouble("warps." + name + ".y"), this.getManager().getDatabase().getDouble("warps." + name + ".z"), yaw, pitch);
	}

	public void setWarp(String name, Location loc) {
		this.getManager().getDatabase().setValue("warps." + name + ".world", loc.getWorld().getName());
		this.getManager().getDatabase().setValue("warps." + name + ".x", loc.getX());
		this.getManager().getDatabase().setValue("warps." + name + ".y", loc.getY());
		this.getManager().getDatabase().setValue("warps." + name + ".z", loc.getZ());
		this.getManager().getDatabase().setValue("warps." + name + ".yaw", loc.getYaw());
		this.getManager().getDatabase().setValue("warps." + name + ".pitch", loc.getPitch());
	}
}
