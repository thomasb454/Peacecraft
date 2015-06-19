package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public abstract class RedisPubSub {
    private RedisDatabase database;
    private String channels[];
    private PeacePubSub internal;

    public RedisPubSub(RedisDatabase database, String... channels) {
        this.database = database;
        this.channels = channels;
    }

    public void subscribe() {
        this.internal = new PeacePubSub(this);
        Jedis jedis = this.database.getPool().getResource();
        try {
            jedis.subscribe(this.internal, this.channels);
        } finally {
            this.database.getPool().returnResource(jedis);
        }
    }

    public void unsubscribe() {
        if(this.internal != null) {
            this.internal.unsubscribe();
            this.internal = null;
        }
    }

    public abstract void recieve(String channel, String message);

    public abstract void onSubscribe(String channel, long count);

    public abstract void onUnsubscribe(String channel, long count);

    private static class PeacePubSub extends JedisPubSub {
        private RedisPubSub parent;

        protected PeacePubSub(RedisPubSub parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(String channel, String message) {
            this.parent.recieve(channel, message);
        }

        @Override
        public void onSubscribe(String channel, int count) {
            this.parent.onSubscribe(channel, count);
        }

        @Override
        public void onUnsubscribe(String channel, int count) {
            this.parent.onUnsubscribe(channel, count);
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
        }

        @Override
        public void onPUnsubscribe(String pattern, int count) {
        }

        @Override
        public void onPSubscribe(String pattern, int count) {
        }
    }
}
