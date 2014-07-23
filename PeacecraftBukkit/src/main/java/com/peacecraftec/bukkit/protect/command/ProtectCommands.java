package com.peacecraftec.bukkit.protect.command;

import com.peacecraftec.bukkit.protect.PeacecraftProtect;
import com.peacecraftec.bukkit.protect.ProtectPermissions;
import com.peacecraftec.bukkit.protect.core.Access;
import com.peacecraftec.bukkit.protect.core.interact.*;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;

import java.util.Arrays;

public class ProtectCommands extends Executor {
	private PeacecraftProtect module;

	public ProtectCommands(PeacecraftProtect module) {
		this.module = module;
	}

	@Command(aliases = {"protect"}, desc = "Protects a block.", usage = "<private/public>", min = 1, permission = ProtectPermissions.USE, console = false, commandblock = false)
	public void protect(CommandSender sender, String command, String args[]) {
		Access access = null;
		try {
			access = Access.valueOf(args[0].toUpperCase());
			if(access == null) {
				sender.sendMessage("protect.invalid-access", args[0]);
				return;
			}
		} catch(Exception e) {
			sender.sendMessage("protect.invalid-access", args[0]);
			return;
		}

		this.module.setAction(sender.getName(), new ProtectAction(access));
		sender.sendMessage("protect.click-to-protect");
	}

	@Command(aliases = {"unprotect"}, desc = "Unprotects a block.", permission = ProtectPermissions.USE, console = false, commandblock = false)
	public void unprotect(CommandSender sender, String command, String args[]) {
		this.module.setAction(sender.getName(), new UnprotectAction());
		sender.sendMessage("protect.click-to-unprotect");
	}

	@Command(aliases = {"access"}, desc = "Manages protection access.", usage = "<add/remove/list> [players]", min = 1, permission = ProtectPermissions.USE, console = false, commandblock = false)
	public void access(CommandSender sender, String command, String args[]) {
		if(args[0].equalsIgnoreCase("add")) {
			if(args.length < 2) {
				sender.sendMessage("generic.usage", "/access add <players>");
				return;
			}

			this.module.setAction(sender.getName(), new AccessAddAction(Arrays.copyOfRange(args, 1, args.length)));
			sender.sendMessage("protect.click-to-add");
		} else if(args[0].equalsIgnoreCase("remove")) {
			if(args.length < 2) {
				sender.sendMessage("generic.usage", "/access remove <players>");
				return;
			}

			this.module.setAction(sender.getName(), new AccessRemoveAction(Arrays.copyOfRange(args, 1, args.length)));
			sender.sendMessage("protect.click-to-remove");
		} else if(args[0].equalsIgnoreCase("list")) {
			this.module.setAction(sender.getName(), new AccessListAction());
			sender.sendMessage("protect.click-to-list");
		}
	}
}
