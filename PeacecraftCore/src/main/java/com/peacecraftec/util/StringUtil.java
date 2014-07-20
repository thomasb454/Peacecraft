package com.peacecraftec.util;

public class StringUtil {
	public static String capitalizeAll(String str) {
		StringBuilder result = new StringBuilder();
		for(String word : str.split(" ")) {
			result.append(capitalizeFirst(word)).append(" ");
		}
		
		return result.toString().trim();
	}
	
	public static String capitalizeFirst(String str) {
		if(str.length() < 1) return str;
		String first = str.substring(0, 1).toUpperCase();
		return first + str.substring(1);
	}
}
