package com.peacecraftec.bukkit.portals;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class PortalPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to manage the portals module.")
	public static final String MANAGE = "peacecraft.portals.manage";
	
}