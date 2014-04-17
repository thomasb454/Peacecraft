package com.peacecraftec.web.donation.disabled;

import com.peacecraftec.web.donation.DonationSystem;

public class DisabledDonationSystem implements DonationSystem {

	@Override
	public boolean isDonor(String player) {
		return false;
	}

	@Override
	public void cleanup() {
	}

}
