package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;

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
		return this.db.contains(this.name);
	}
	
	public Set<String> all() {
		Jedis jedis = this.db.getPool().getResource();
		try {
			return jedis.zrangeByScore(this.name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public boolean contains(String member) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			return jedis.zscore(this.name, member) != null;
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public double get(String member) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			Double d = jedis.zscore(this.name, member);
			if(d == null) {
				return 0;
			}

			return d;
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public void put(String member, double value) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			jedis.zadd(this.name, value, member);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public void remove(String member) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			jedis.zrem(this.name, member);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
	public void increment(String member) {
		this.increment(member, 1);
	}
	
	public void increment(String member, double amount) {
		Jedis jedis = this.db.getPool().getResource();
		try {
			jedis.zincrby(this.name, amount, member);
		} finally {
			this.db.getPool().returnResource(jedis);
		}
	}
	
}
