package com.peacecraftec.bukkit.stats;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class StatsPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to view stats.")
	public static final String STATS = "peacecraft.stats.view";
	
}