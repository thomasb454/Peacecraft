package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;

public abstract class RedisPubSub {
	private Jedis conn;
	private String channels[];
	private PeacePubSub internal;
	
	public RedisPubSub(String host, String... channels) {
		this.conn = new Jedis(host);
		this.conn.connect();
		this.channels = channels;
	}
	
	public void subscribe() {
		this.internal = new PeacePubSub(this);
		this.conn.subscribe(this.internal, this.channels);
	}
	
	public void unsubscribe() {
		if(this.internal != null) {
			this.internal.unsubscribe();
			this.internal = null;
		}

		this.conn.close();
	}
	
	public abstract void recieve(String channel, String message);
	
	public abstract void onSubscribe(String channel, long count);
	
	public abstract void onUnsubscribe(String channel, long count);
}
