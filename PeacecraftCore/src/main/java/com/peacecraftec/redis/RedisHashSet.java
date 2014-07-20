package com.peacecraftec.redis;

import java.util.Set;

public class RedisHashSet {

	private String name;
	private RedisDatabase db;
	
	protected RedisHashSet(String name, RedisDatabase db) {
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
		return this.db.getRedis().hkeys(this.name);
	}
	
	public boolean contains(String field) {
		return this.db.getRedis().hget(this.name, field) != null;
	}
	
	public String get(String field) {
		return this.db.getRedis().hget(this.name, field);
	}
	
	public void put(String field, String value) {
		this.db.getRedis().hset(this.name, field, value);
	}
	
}
