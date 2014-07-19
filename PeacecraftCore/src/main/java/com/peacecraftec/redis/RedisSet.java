package com.peacecraftec.redis;

import java.util.Set;

public class RedisSet {

	private String name;
	private RedisDatabase db;
	
	protected RedisSet(String name, RedisDatabase db) {
		this.name = name;
		this.db = db;
	}
	
	public String getName() {
		return this.name;
	}

	public boolean exists() {
		return this.db.getRedis().exists(this.name);
	}
	
	public Set<String> all() {
		return this.db.getRedis().smembers(this.name);
	}
	
	public boolean contains(String value) {
		return this.db.getRedis().sismember(this.name, value);
	}
	
	public void add(String value) {
		this.db.getRedis().sadd(this.name, value);
	}
	
	public void remove(String value) {
		this.db.getRedis().srem(this.name, value);
	}
	
}
