package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;

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
        return this.db.contains(this.name);
    }

    public Set<String> all() {
        Jedis jedis = this.db.getPool().getResource();
        try {
            return jedis.smembers(this.name);
        } finally {
            this.db.getPool().returnResource(jedis);
        }
    }

    public boolean contains(String value) {
        Jedis jedis = this.db.getPool().getResource();
        try {
            return jedis.sismember(this.name, value);
        } finally {
            this.db.getPool().returnResource(jedis);
        }
    }

    public void add(String value) {
        Jedis jedis = this.db.getPool().getResource();
        try {
            jedis.sadd(this.name, value);
        } finally {
            this.db.getPool().returnResource(jedis);
        }
    }

    public void remove(String value) {
        Jedis jedis = this.db.getPool().getResource();
        try {
            jedis.srem(this.name, value);
        } finally {
            this.db.getPool().returnResource(jedis);
        }
    }

}
