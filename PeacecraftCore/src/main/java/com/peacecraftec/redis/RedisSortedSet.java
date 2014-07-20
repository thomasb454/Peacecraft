package com.peacecraftec.redis;

import java.util.Set;

public class RedisSortedSet {

	private String name;
	private RedisDatabase db;
	
	protected RedisSortedSet(String name, RedisDatabase db) {
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
		return this.db.getRedis().zrangeByScore(this.name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}
	
	public boolean contains(String member) {
		return this.db.getRedis().zscore(this.name, member) != null;
	}
	
	public double get(String member) {
		Double d = this.db.getRedis().zscore(this.name, member);
		if(d == null) {
			return 0;
		}
		
		return d;
	}
	
	public void put(String member, double value) {
		this.db.getRedis().zadd(this.name, value, member);
	}
	
	public void remove(String member) {
		this.db.getRedis().zrem(this.name, member);
	}
	
	public void increment(String member) {
		this.increment(member, 1);
	}
	
	public void increment(String member, double amount) {
		this.db.getRedis().zincrby(this.name, amount, member);
	}
	
}
