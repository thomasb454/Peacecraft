package com.peacecraftec.bukkit.core;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class CorePermissions implements PermissionContainer {

	@Perm(desc = "Allows players to reload modules.")
	public static final String RELOAD = "peacecraft.reload";
	
	@Perm(desc = "Allows players to view /help.")
	public static final String HELP = "peacecraft.help";
	
	@Perm(desc = "Allows players to view /permissions.")
	public static final String PERMISSIONS = "peacecraft.permissions";
	
	@Perm(desc = "Allows players to view /plugins.")
	public static final String PLUGINS = "peacecraft.plugins";
	
}
