package com.peacecraftec.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

public abstract class RedisPubSub {

	static {
		RedisLib.preloadClasses();
	}
	
	private RedisPubSubConnection<String, String> conn;
	private String channels[];
	
	public RedisPubSub(String host, String... channels) {
		RedisClient client = new RedisClient("localhost");
		this.conn = client.connectPubSub();
		this.conn.addListener(new PeacePubSub(this));
		this.channels = channels;
	}
	
	public void subscribe() {
		this.conn.subscribe(this.channels);
	}
	
	public void unsubscribe() {
		this.conn.unsubscribe(this.channels);
		this.conn.close();
	}
	
	public abstract void recieve(String channel, String message);
	
	public abstract void onSubscribe(String channel, long count);
	
	public abstract void onUnsubscribe(String channel, long count);
	
}
