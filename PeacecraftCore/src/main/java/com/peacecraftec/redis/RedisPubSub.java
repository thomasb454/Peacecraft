package com.peacecraftec.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

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
