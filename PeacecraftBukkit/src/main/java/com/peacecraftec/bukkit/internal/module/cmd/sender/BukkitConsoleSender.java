package com.peacecraftec.bukkit.internal.module.cmd.sender;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.peacecraftec.module.cmd.sender.ConsoleSender;
import com.peacecraftec.module.lang.LanguageManager;

public class BukkitConsoleSender extends BukkitCommandSender implements ConsoleSender {

	public BukkitConsoleSender(ConsoleCommandSender handle, LanguageManager languages) {
		super(handle, languages);
	}
	
	@Override
	public String getDisplayName() {
		return ChatColor.GRAY + "CONSOLE" + ChatColor.WHITE;
	}

}
