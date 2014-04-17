package com.peacecraftec.bukkit.eco;

import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;

public class EcoPermissions implements PermissionContainer {

	@Perm(desc = "Allows players to manage the economy module.")
	public static final String MANAGE = "peacecraft.eco.manage";

	@Perm(desc = "Allows players to check their balance.")
	public static final String BALANCE = "peacecraft.eco.balance";
	
	@Perm(desc = "Allows players to check another's balance.")
	public static final String BALANCE_OTHERS = "peacecraft.eco.balance.other";
	
	@Perm(desc = "Allows players to pay other players money.")
	public static final String PAY = "peacecraft.eco.pay";
	
	@Perm(desc = "Allows players to sell items.")
	public static final String SELL = "peacecraft.eco.sell";

	@Perm(desc = "Allows players to use chest shops.")
	public static final String USE_CHEST_SHOP = "peacecraft.eco.chestshop.use";
	
	@Perm(desc = "Allows players to make chest shops.")
	public static final String MAKE_CHEST_SHOP = "peacecraft.eco.chestshop.make";
	
	@Perm(desc = "Allows players to make admin shops.")
	public static final String MAKE_ADMIN_SHOP = "peacecraft.eco.adminshop";

	@Perm(desc = "Allows players to check the info of an item.")
	public static final String ITEM_INFO = "peacecraft.eco.iteminfo";
	
}