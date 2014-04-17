package com.peacecraftec.bukkit.restrictions;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class RestrictionsPermissions implements PermissionContainer {
	
	@Perm(desc = "Allows players to break/place bedrock.")
	public static final String BEDROCK = "peacecraft.restrictions.bedrock";
	
}