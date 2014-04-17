package com.peacecraftec.web.donation.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.peacecraftec.redis.RedisPubSub;
import com.peacecraftec.web.donation.DonationCallback;

public class DonationPubSub extends RedisPubSub {
	
	private DonationCallback callback;
	
	public DonationPubSub(DonationCallback callback, String host, String... channels) {
		super(host, channels);
		this.callback = callback;
	}
	
	@Override
	public void recieve(String channel, String message) {
		JsonObject json = new Gson().fromJson(message, JsonObject.class);
		this.callback.onDonation(json.get("player").getAsString(), json.get("dollars").getAsInt());
	}

	@Override
	public void onSubscribe(String channel, long count) {
	}

	@Override
	public void onUnsubscribe(String channel, long count) {
	}

}
