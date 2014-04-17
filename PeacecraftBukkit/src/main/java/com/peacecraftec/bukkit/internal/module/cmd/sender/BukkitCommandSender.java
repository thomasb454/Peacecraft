package com.peacecraftec.bukkit.internal.module.cmd.sender;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.lang.LanguageManager;

public class BukkitCommandSender implements CommandSender {

	protected org.bukkit.command.CommandSender handle;
	private LanguageManager languages;
	
	public BukkitCommandSender(org.bukkit.command.CommandSender handle, LanguageManager languages) {
		this.handle = handle;
		this.languages = languages;
	}
	
	@Override
	public String getName() {
		return this.handle.getName();
	}
	
	@Override
	public String getDisplayName() {
		return this.getName();
	}

	@Override
	public String getLanguage() {
		return "en_US";
	}

	@Override
	public void sendMessage(String key) {
		this.handle.sendMessage(this.languages.get(this).translate(key));
	}

	@Override
	public void sendMessage(String key, Object... args) {
		this.handle.sendMessage(this.languages.get(this).translate(key, args));
	}

	@Override
	public boolean hasPermission(String permission) {
		return this.handle.hasPermission(permission);
	}
	
	public static CommandSender wrap(org.bukkit.command.CommandSender handle, LanguageManager languages) {
		if(handle instanceof ConsoleCommandSender) {
			return new BukkitConsoleSender((ConsoleCommandSender) handle, languages);
		} else if(handle instanceof BlockCommandSender) {
			return new BukkitCommandBlockSender((BlockCommandSender) handle, languages);
		} else if(handle instanceof Player) {
			return new BukkitPlayerSender((Player) handle, languages);
		} else {
			return new BukkitCommandSender(handle, languages);
		}
	}
	
	public static org.bukkit.command.CommandSender unwrap(CommandSender wrapped) {
		if(wrapped == null) {
			return null;
		}
		
		return ((BukkitCommandSender) wrapped).handle;
	}

}
