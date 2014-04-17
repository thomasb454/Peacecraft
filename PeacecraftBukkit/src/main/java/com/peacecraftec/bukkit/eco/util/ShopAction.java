package com.peacecraftec.bukkit.eco.util;

public enum ShopAction {

	BUY,
	SELL;

	public double getPrice(String line) {
		String[] split = line.replace(" ", "").toLowerCase().split(":");
		String character = this == BUY ? "b" : "s";
		for(String part : split) {
			if(!part.contains(character)) {
				continue;
			}

			part = part.replace(character, "");
			if(part.equals("free")) {
				return 0;
			}

			if(isDouble(part)) {
				double price = Double.valueOf(part);
				if(price <= 0) {
					return -1;
				} else {
					return price;
				}
			}
		}

		return -1;
	}

	private static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
