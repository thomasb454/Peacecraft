package com.peacecraftec.bukkit.chat;

import org.bukkit.ChatColor;

public class ChatUtil {

	public static String formatChannelName(String channel) {
		if(channel.equals("global")) {
			return "[g]";
		}
		
		if(channel.equals("mod")) {
			return ChatColor.AQUA + "[MOD]" + ChatColor.RESET;
		}
		
		return "[" + channel + "]";
	}

	public static String translateColor(String text) {
		char[] b = text.toCharArray();
		for(int i = 0; i < b.length - 1; i++) {
			if(b[i] == '&' && "0123456789AaBbCcDdEeFf".indexOf(b[i + 1]) > -1) {
				b[i] = ChatColor.COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}
	
	public static String translateFormat(String text) {
		char[] b = text.toCharArray();
		for(int i = 0; i < b.length - 1; i++) {
			if(b[i] == '&' && "LlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = ChatColor.COLOR_CHAR;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}
	
}
