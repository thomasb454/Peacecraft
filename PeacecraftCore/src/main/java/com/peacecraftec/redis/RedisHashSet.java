package com.peacecraftec.redis;

import java.util.List;

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
	
	public RedisDatabase getDatabase() {
		return this.db;
	}
	
	public boolean exists() {
		return this.db.getRedis().exists(this.name);
	}
	
	public List<String> getMembers() {
		return this.db.getRedis().hkeys(this.name);
	}
	
	public boolean contains(String field) {
		return this.db.getRedis().hget(this.name, field) != null;
	}
	
	public String get(String field) {
		return this.db.getRedis().hget(this.name, field);
	}
	
	public void set(String field, String value) {
		this.db.getRedis().hset(this.name, field, value);
	}
	
}
