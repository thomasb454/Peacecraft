package com.peacecraftec.redis;

import redis.clients.jedis.JedisPubSub;

public class PeacePubSub extends JedisPubSub {
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
