package com.peacecraftec.bukkit.perms;

import com.peacecraftec.module.permission.Perm;

public class PermsPermissions {

    @Perm(desc = "Allows players to manage the permissions module.")
    public static final String MANAGE = "peacecraft.perms.manage";

    @Perm(desc = "Allows players to teleport to spawn.")
    public static final String SPAWN = "peacecraft.perms.spawn";

}