package com.peacecraftec.bukkit.worlds;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class WorldPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to manage the economy module.")
	public static final String MANAGE = "peacecraft.worlds.manage";

	@Perm(desc = "Allows players to override per-world gamemode settings.")
	public static final String GM_OVERRIDE = "peacecraft.worlds.gmoverride";
	
}