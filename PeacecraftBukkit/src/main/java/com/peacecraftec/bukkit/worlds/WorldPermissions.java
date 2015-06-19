package com.peacecraftec.bukkit.worlds;

import com.peacecraftec.module.permission.Perm;

public class WorldPermissions {

    @Perm(desc = "Allows players to manage the worlds module.")
    public static final String MANAGE = "peacecraft.worlds.manage";

    @Perm(desc = "Allows players to override per-world gamemode settings.")
    public static final String GM_OVERRIDE = "peacecraft.worlds.gmoverride";

}