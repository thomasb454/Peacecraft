package com.peacecraftec.bukkit.core;

import com.peacecraftec.module.permission.Perm;

public class CorePermissions {

    @Perm(desc = "Allows players to reload modules.")
    public static final String MANAGE_MODULES = "peacecraft.modules";

    @Perm(desc = "Allows players to view /help.")
    public static final String HELP = "peacecraft.help";

    @Perm(desc = "Allows players to view /permissions.")
    public static final String PERMISSIONS = "peacecraft.permissions";

    @Perm(desc = "Allows players to view /rules.")
    public static final String RULES = "peacecraft.rules";

    @Perm(desc = "Allows players to use the selection wand.")
    public static final String WAND = "peacecraft.wand";

    @Perm(desc = "Allows players to toggle flight.")
    public static final String FLY = "peacecraft.fly";

    @Perm(desc = "Allows players to toggle flight on other players.")
    public static final String FLY_OTHERS = "peacecraft.fly.others";

    @Perm(desc = "Allows players to toggle invisibility.")
    public static final String INVIS = "peacecraft.invis";

    @Perm(desc = "Allows players to toggle invisibility on others.")
    public static final String INVIS_OTHERS = "peacecraft.invis.others";

    @Perm(desc = "Allows players to teleport back after teleporting somewhere.")
    public static final String BACK = "peacecraft.back";

    @Perm(desc = "Allows players to make teleport requests.")
    public static final String TPR_REQUEST = "peacecraft.tpr.request";

    @Perm(desc = "Allows players to respond to teleport requests.")
    public static final String TPR_RESPOND = "peacecraft.tpr.respond";

    @Perm(desc = "Allows players to set a home to teleport to.")
    public static final String HOME = "peacecraft.home";

    @Perm(desc = "Allows players to set a home to teleport to.")
    public static final String WARP = "peacecraft.warp";

    @Perm(desc = "Allows players to set a home to teleport to.")
    public static final String SET_WARP = "peacecraft.warp.set";

    @Perm(desc = "Allows players to open the inventories of other players.")
    public static final String INVSEE = "peacecraft.invsee";

    @Perm(desc = "Allows players to open the enderchests of other players.")
    public static final String ENDERCHEST = "peacecraft.enderchest";

    @Perm(desc = "Allows players to create lightning.")
    public static final String LIGHTNING = "peacecraft.lightning";

}
