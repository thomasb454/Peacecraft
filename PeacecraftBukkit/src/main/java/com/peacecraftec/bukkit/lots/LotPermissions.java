package com.peacecraftec.bukkit.lots;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class LotPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to bypass restrictions such as the lot limit and managing other players' lots.")
	public static final String RESTRICTION_OVERRIDE = "peacecraft.lots.norestrict";
	
	@Perm(desc = "Allows players to build anywhere.")
	public static final String BUILD_ANYWHERE = "peacecraft.lots.buildanywhere";
	
	@Perm(desc = "Allows players to interact anywhere.")
	public static final String DO_ANYTHING = "peacecraft.lots.doanything";
	
	@Perm(desc = "Allows players to manage lots.")
	public static final String MANAGE_LOTS = "peacecraft.lots.managelots";
	
	@Perm(desc = "Allows players to use commands related to owning lots.")
	public static final String OWN_LOT = "peacecraft.lots.ownlot";
	
}