package com.peacecraftec.web.donation;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.donation.disabled.DisabledDonationSystem;
import com.peacecraftec.web.donation.redis.RedisDonationSystem;

public class DonationFactory {

	public static DonationSystem create(ModuleManager manager, DonationCallback callback) {
		try {
			return new RedisDonationSystem(manager, callback);
		} catch(Throwable t) {
			System.err.println("Failed to create redis donation system, donations will not be available.");
			t.printStackTrace();
			return new DisabledDonationSystem();
		}
	}
	
}
