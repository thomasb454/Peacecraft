package com.peacecraftec.bukkit.internal.module.cmd.sender;

import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.module.lang.LanguageManager;
import org.bukkit.entity.Player;

public class BukkitPlayerSender extends BukkitCommandSender implements PlayerSender {

	public BukkitPlayerSender(Player handle, LanguageManager languages) {
		super(handle, languages);
	}
	
	@Override
	public String getLanguage() {
		return ((Player) this.handle).spigot().getLocale();
	}

	@Override
	public String getDisplayName() {
		return ((Player) this.handle).getDisplayName();
	}
	
}
