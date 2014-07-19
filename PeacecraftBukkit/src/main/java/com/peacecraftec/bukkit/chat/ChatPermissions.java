package com.peacecraftec.bukkit.chat;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class ChatPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to use channel commands.")
	public static final String USE_CHANNELS = "peacecraft.chat.channels";
	
	@Perm(desc = "Signifies that the player is a mod chat-wise.")
	public static final String MOD = "peacecraft.chat.mod";
	
	@Perm(desc = "Allows players to use color codes in chat.")
	public static final String CHAT_COLOR = "peacecraft.chat.color";
	
	@Perm(desc = "Allows players to use the magic format code in chat.")
	public static final String CHAT_MAGIC = "peacecraft.chat.magic";
	
	@Perm(desc = "Allows players to use formatting codes in chat.")
	public static final String CHAT_FORMAT = "peacecraft.chat.format";
	
	@Perm(desc = "Allows players to use the nickname command.")
	public static final String NICKNAME = "peacecraft.chat.nickname";
	
	@Perm(desc = "Allows players to use the nickname command on other players.")
	public static final String NICKNAME_OTHER = "peacecraft.chat.nickname.other";

	@Perm(desc = "Allows players to use the whois command.")
	public static final String WHOIS = "peacecraft.chat.whois";

	@Perm(desc = "Allows players to use the me command.")
	public static final String ME = "peacecraft.chat.me";
	
	@Perm(desc = "Allows players to clear chat.")
	public static final String CLEAR_CHAT = "peacecraft.chat.clear";
	
	@Perm(desc = "Allows players to clear other players' chat.")
	public static final String CLEAR_OTHERS_CHAT = "peacecraft.chat.clear.others";
	
	@Perm(desc = "Allows players to set their webchat password.")
	public static final String SET_PASS = "peacecraft.chat.setpass";

	@Perm(desc = "Allows players to view the list of online staff members.")
	public static final String STAFF_LIST = "peacecraft.chat.stafflist";

	@Perm(desc = "Allows players to mute other players.")
	public static final String MUTE = "peacecraft.chat.mute";
	
}