package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

public class RedisDatabase {
    private JedisPool pool;

    public RedisDatabase(String host) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(true);
        this.pool = new JedisPool(config, host);
    }

    public JedisPool getPool() {
        return this.pool;
    }

    public Set<String> getKeys(String start) {
        return this.keys(start + ".*");
    }

    public Set<String> keys(String query) {
        Jedis jedis = this.pool.getResource();
        try {
            return jedis.keys(query);
        } finally {
            this.pool.returnResource(jedis);
        }
    }

    public boolean contains(String name) {
        Jedis jedis = this.pool.getResource();
        try {
            return jedis.exists(name);
        } finally {
            this.pool.returnResource(jedis);
        }
    }

    public String getString(String name) {
        Jedis jedis = this.pool.getResource();
        try {
            return jedis.get(name);
        } finally {
            this.pool.returnResource(jedis);
        }
    }

    public byte getByte(String name) {
        try {
            return Byte.parseByte(this.getString(name));
        } catch(NumberFormatException e) {
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
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
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
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
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
            return 0;
        }
    }

    public long getLong(String name) {
        try {
            return Long.parseLong(this.getString(name));
        } catch(NumberFormatException e) {
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
            return 0;
        }
    }

    public float getFloat(String name) {
        try {
            return Float.parseFloat(this.getString(name));
        } catch(NumberFormatException e) {
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
            return 0;
        }
    }

    public double getDouble(String name) {
        try {
            return Double.parseDouble(this.getString(name));
        } catch(NumberFormatException e) {
            System.err.println("[PeacecraftCore] Tried to get number at \"" + name + "\" when it wasn't a number.");
            return 0;
        }
    }

    public void setValue(String name, Object obj) {
        Jedis jedis = this.pool.getResource();
        try {
            jedis.set(name, obj != null ? obj.toString() : null);
        } catch(Exception e) {
            System.err.println("[PeacecraftCore] Failed to put value at \"" + name + "\" in redis database.");
            e.printStackTrace();
        } finally {
            this.pool.returnResource(jedis);
        }
    }

    public void remove(String name) {
        Jedis jedis = this.pool.getResource();
        try {
            jedis.del(name);
        } finally {
            this.pool.returnResource(jedis);
        }
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
        this.pool.destroy();
    }

    public void publish(String channel, String message) {
        Jedis jedis = this.pool.getResource();
        try {
            jedis.publish(channel, message);
        } finally {
            this.pool.returnResource(jedis);
        }
    }
}
