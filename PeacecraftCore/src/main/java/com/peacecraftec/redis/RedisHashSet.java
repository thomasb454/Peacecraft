package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;

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
		return this.db.contains(this.name);
	}
	
	public Set<String> all() {
		Jedis jedis = this.db.getPool().getResource();
		try {
			return jedis.hkeys(this.name);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public boolean contains(String field) {
		return this.get(field) != null;
	}
	
	public String get(String field) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			return jedis.hget(this.name, field);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public void put(String field, String value) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			jedis.hset(this.name, field, value);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}

	public void remove(String field) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			jedis.hdel(this.name, field);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
}
