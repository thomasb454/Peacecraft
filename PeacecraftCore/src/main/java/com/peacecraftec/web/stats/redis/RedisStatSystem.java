package com.peacecraftec.web.stats.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.redis.RedisSet;
import com.peacecraftec.redis.RedisSortedSet;
import com.peacecraftec.web.stats.StatSystem;

public class RedisStatSystem implements StatSystem {

	private ModuleManager manager;
	private String server;
	private RedisDatabase db;
	private RedisSet available;
	private RedisSet players;
	
	public RedisStatSystem(ModuleManager manager, String server) {
		this(manager, server, new RedisDatabase("localhost"));
	}
	
	public RedisStatSystem(ModuleManager manager, String server, RedisDatabase db) {
		this.manager = manager;
		this.server = server;
		this.db = db;
		this.available = this.db.getSet(this.server + ".stats.available");
		this.players = this.db.getSet(this.server + ".stats.players");
	}
	
	@Override
	public Map<String, Map<String, Double>> getAll() {
		Map<String, Map<String, Double>> result = new HashMap<String, Map<String, Double>>();
		for(String stat : this.available.all()) {
			RedisSortedSet stats = this.db.getSortedSet(this.server + "." + stat);
			Map<String, Double> values = new HashMap<String, Double>();
			for(String uuid : stats.getMembers()) {
				values.put(this.manager.getUsername(UUID.fromString(uuid)), stats.get(uuid));
			}
			
			result.put(stat, values);
		}
		
		return result;
	}
	
	@Override
	public Map<String, Double> getAll(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return new HashMap<String, Double>();
		}
		
		Map<String, Double> result = new HashMap<String, Double>();
		for(String stat : this.available.all()) {
			RedisSortedSet stats = this.db.getSortedSet(this.server + "." + stat);
			if(stats.contains(uuid.toString())) {
				result.put(stat, stats.get(uuid.toString()));
			}
		}
		
		return result;
	}
	
	@Override
	public void addPlayer(String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid != null) {
			this.players.add(uuid.toString());
		}
	}
	
	@Override
	public boolean contains(String stat, String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return false;
		}
		
		return this.db.getSortedSet(this.server + "." + stat).contains(uuid.toString());
	}

	@Override
	public byte getByte(String stat, String player) {
		return (byte) this.getDouble(stat, player);
	}

	@Override
	public boolean getBoolean(String stat, String player) {
		return this.getByte(stat, player) == 1;
	}

	@Override
	public char getChar(String stat, String player) {
		return (char) this.getDouble(stat, player);
	}

	@Override
	public short getShort(String stat, String player) {
		return (short) this.getDouble(stat, player);
	}

	@Override
	public int getInt(String stat, String player) {
		return (int) this.getDouble(stat, player);
	}

	@Override
	public long getLong(String stat, String player) {
		return (long) this.getDouble(stat, player);
	}

	@Override
	public float getFloat(String stat, String player) {
		return (float) this.getDouble(stat, player);
	}

	@Override
	public double getDouble(String stat, String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return 0;
		}
		
		return this.db.getSortedSet(this.server + "." + stat).get(uuid.toString());
	}

	@Override
	public void setStat(String stat, String player, byte value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, boolean value) {
		this.setStat(stat, player, (double) (value ? 1 : 0));
	}

	@Override
	public void setStat(String stat, String player, char value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, short value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, int value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, long value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, float value) {
		this.setStat(stat, player, (double) value);
	}

	@Override
	public void setStat(String stat, String player, double value) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return;
		}
		
		this.available.add(stat);
		this.db.getSortedSet(this.server + "." + stat).set(uuid.toString(), value);
	}

	@Override
	public void increment(String stat, String player) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return;
		}
		
		this.available.add(stat);
		this.db.getSortedSet(this.server + "." + stat).increment(uuid.toString());
	}
	
	@Override
	public void increment(String stat, String player, double amount) {
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return;
		}
		
		this.available.add(stat);
		this.db.getSortedSet(this.server + "." + stat).increment(uuid.toString(), amount);
	}
	
	@Override
	public void cleanup() {
		this.db.cleanup();
	}

}
