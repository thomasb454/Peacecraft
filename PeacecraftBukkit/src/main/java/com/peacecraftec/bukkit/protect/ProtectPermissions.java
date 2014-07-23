package com.peacecraftec.bukkit.protect;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class ProtectPermissions implements PermissionContainer {
	@Perm(desc = "Allows players to protect blocks.")
	public static final String USE = "peacecraft.protect.use";

	@Perm(desc = "Allows players to access all protections.")
	public static final String ACCESS_ALL = "peacecraft.protect.accessall";

	@Perm(desc = "Allows players to modify all protections.")
	public static final String MODIFY_ALL = "peacecraft.protect.modifyall";
}