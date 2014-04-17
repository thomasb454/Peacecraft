package com.peacecraftec.bukkit.internal.module.cmd.sender;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;

import com.peacecraftec.module.cmd.sender.CommandBlockSender;
import com.peacecraftec.module.lang.LanguageManager;

public class BukkitCommandBlockSender extends BukkitCommandSender implements CommandBlockSender {

	public BukkitCommandBlockSender(BlockCommandSender handle, LanguageManager languages) {
		super(handle, languages);
	}
	
	@Override
	public String getDisplayName() {
		return ChatColor.GRAY + "@" + ChatColor.WHITE;
	}

}
