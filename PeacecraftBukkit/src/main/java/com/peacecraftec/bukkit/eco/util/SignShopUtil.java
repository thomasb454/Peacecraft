package com.peacecraftec.bukkit.eco.util;

import java.util.regex.Pattern;

import org.bukkit.block.Sign;

public class SignShopUtil {

	public static final byte NAME_LINE = 0;
	public static final byte QUANTITY_LINE = 1;
	public static final byte PRICE_LINE = 2;
	public static final byte ITEM_LINE = 3;

	public static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^[\\w -.]*$"), Pattern.compile("^[1-9][0-9]*$"), Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w #:-]+$") };

	public static boolean isAdminShop(Sign sign) {
		return sign.getLine(NAME_LINE).replace(" ", "").equalsIgnoreCase("AdminShop");
	}

	public static boolean isValid(Sign sign) {
		return isValidPreparedSign(sign.getLines()) && (sign.getLine(PRICE_LINE).toUpperCase().contains("B") || sign.getLine(PRICE_LINE).toUpperCase().contains("S")) && !sign.getLine(PRICE_LINE).isEmpty();
	}

	public static boolean isValidPreparedSign(String lines[]) {
		for(int i = 0; i < 4; i++) {
			if(!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) {
				return false;
			}
		}

		return lines[PRICE_LINE].indexOf(':') == lines[PRICE_LINE].lastIndexOf(':');
	}

}
