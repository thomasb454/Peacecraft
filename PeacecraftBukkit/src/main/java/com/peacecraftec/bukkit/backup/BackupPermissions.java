package com.peacecraftec.bukkit.backup;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class BackupPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to manage the backup module.")
	public static final String MANAGE = "peacecraft.backup.manage";
	
}