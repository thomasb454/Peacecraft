package com.peacecraftec.redis;

import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;

public class PeacePubSub extends RedisPubSubAdapter<String, String> {

	private RedisPubSub parent;
	
	protected PeacePubSub(RedisPubSub parent) {
		this.parent = parent;
	}

	@Override
	public void message(String channel, String message) {
		this.parent.recieve(channel, message);
	}
	
	@Override
	public void subscribed(String channel, long count) {
		this.parent.onSubscribe(channel, count);
	}

	@Override
	public void unsubscribed(String channel, long count) {
		this.parent.onUnsubscribe(channel, count);
	}

}
