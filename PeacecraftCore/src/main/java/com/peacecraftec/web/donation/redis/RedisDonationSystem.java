package com.peacecraftec.web.donation.redis;

import java.util.UUID;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.redis.RedisSortedSet;
import com.peacecraftec.web.donation.DonationCallback;
import com.peacecraftec.web.donation.DonationSystem;

public class RedisDonationSystem implements DonationSystem {

	private static final String CHANNEL = "PeacecraftDonations";

	private ModuleManager manager;
	private RedisDatabase db;
	private RedisSortedSet donors;
	private DonationPubSub pubsub;
	
	public RedisDonationSystem(final ModuleManager manager, final DonationCallback callback) {
		this(manager, callback, new RedisDatabase("localhost"));
	}
	
	public RedisDonationSystem(final ModuleManager manager, final DonationCallback callback, final RedisDatabase db) {
		this.manager = manager;
		this.db = db;
		this.donors = this.db.getSortedSet("donations");
		new Thread() {
			public void run() {
				pubsub = new DonationPubSub(callback, "localhost", CHANNEL);
				pubsub.subscribe();
			}
		}.start();
	}
	
	public boolean isDonor(String player) {
		this.convert(player); // CONVERSION CODE
		UUID uuid = this.manager.getUUID(player);
		if(uuid == null) {
			return false;
		}
		
		return this.donors.contains(uuid.toString()) ? this.donors.get(uuid.toString()) > System.currentTimeMillis() : false;
	}
	
	public void cleanup() {
		this.pubsub.unsubscribe();
		this.db.cleanup();
	}
	
	// CONVERSION CODE
	private void convert(String name) {
		String player = name.toLowerCase();
		UUID uuid = this.manager.getUUID(player);
		if(uuid != null) {
			if(this.donors.contains(player)) {
				this.donors.set(uuid.toString(), this.donors.get(player));
				this.donors.remove(player);
			}
		} else {
			this.manager.getLogger().severe("[Donation] Player " + name + " does not have a UUID to convert data to!");
		}
	}
	// END CONVERSION CODE

}
