package com.peacecraftec.bukkit.internal.module.cmd.sender;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.module.lang.LanguageManager;

public class BukkitPlayerSender extends BukkitCommandSender implements PlayerSender {

	public BukkitPlayerSender(Player handle, LanguageManager languages) {
		super(handle, languages);
	}
	
	@Override
	public String getLanguage() {
		try {
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer");
			Class<?> entityPlayer = Class.forName("net.minecraft.server.v1_7_R1.EntityPlayer");
			Method getHandle = craftPlayer.getDeclaredMethod("getHandle");
			Object ent = getHandle.invoke(this.handle);
			Field locale = entityPlayer.getDeclaredField("locale");
			locale.setAccessible(true);
			return (String) locale.get(ent);
		} catch(Exception e) {
			e.printStackTrace();
			return "en_US";
		}
	}

	@Override
	public String getDisplayName() {
		return ((Player) this.handle).getDisplayName();
	}
	
}
