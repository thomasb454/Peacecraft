package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisDatabase {
	private Jedis redis;
	private boolean create;
	
	public RedisDatabase(String host) {
		this.redis = new Jedis(host);
		this.redis.connect();
	}
	
	public RedisDatabase(String host, int port) {
		this.redis = new Jedis(host, port);
		this.redis.connect();
	}
	
	public Jedis getRedis() {
		return this.redis;
	}
	
	public Set<String> getKeys(String start) {
		return this.redis.keys(start + ".*");
	}
	
	public Set<String> keys(String query) {
		return this.redis.keys(query);
	}
	
	public boolean contains(String name) {
		return this.redis.exists(name);
	}
	
	public String getString(String name) {
		String result = this.redis.get(name);
		if(result == null && this.create) {
			result = "";
			this.redis.set(name, result);
		}
		
		return result;
	}

	public byte getByte(String name) {
		try {
			return Byte.parseByte(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(this.getString(name));
	}

	public short getShort(String name) {
		try {
			return Short.parseShort(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public char getChar(String name) {
		return this.getString(name).charAt(0);
	}

	public int getInt(String name) {
		try {
			return Integer.parseInt(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public long getLong(String name) {
		try {
			return Long.parseLong(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public float getFloat(String name) {
		try {
			return Float.parseFloat(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public double getDouble(String name) {
		try {
			return Double.parseDouble(this.getString(name));
		} catch(NumberFormatException e) {
			System.err.println("Tried to get number at \"" + name + "\" when it wasn't a number.");
			return 0;
		}
	}

	public void setValue(String name, Object obj) {
		try {
			this.redis.set(name, obj != null ? obj.toString() : null);
		} catch(Exception e) {
			System.err.println("Failed to set value at \"" + name + "\" in redis database.");
			e.printStackTrace();
		}
	}
	
	public void remove(String name) {
		this.redis.del(name);
	}
	
	public RedisSet getSet(String name) {
		return new RedisSet(name, this);
	}
	
	public RedisHashSet getHashSet(String name) {
		return new RedisHashSet(name, this);
	}
	
	public RedisSortedSet getSortedSet(String name) {
		return new RedisSortedSet(name, this);
	}

	public void cleanup() {
		this.redis.close();
	}
	
	public void publish(String channel, String message) {
		this.redis.publish(channel, message);
	}
}
