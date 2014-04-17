package com.peacecraftec.bukkit.backup.command;

import com.peacecraftec.module.cmd.sender.CommandSender;

import com.peacecraftec.bukkit.backup.BackupPermissions;
import com.peacecraftec.bukkit.backup.PeacecraftBackup;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;

public class BackupCommands extends Executor {
	
	private PeacecraftBackup module;
	
	public BackupCommands(PeacecraftBackup module) {
		this.module = module;
	}
	
	@Command(aliases = {"backup"}, desc = "Manages backups.", usage = "<start/stop>", min = 1, permission = BackupPermissions.MANAGE)
	public void backup(CommandSender sender, String command, String args[]) {
		if(args[0].equalsIgnoreCase("start")) {
			if(this.module.isBackingUp()) {
				sender.sendMessage("backup.already-in-progress");
				return;
			}
			
			this.module.initiateBackup();
			sender.sendMessage("backup.initiated");
		} else if(args[0].equalsIgnoreCase("stop")) {
			if(!this.module.isBackingUp()) {
				sender.sendMessage("backup.not-running");
				return;
			}
			
			if(this.module.isBackupCancelled()) {
				sender.sendMessage("backup.already-cancelled");
				return;
			}
			
			this.module.cancel();
			sender.sendMessage("backup.cancelled");
		} else {
			sender.sendMessage("generic.invalid-sub", "start, stop");
		}
	}
	
}
