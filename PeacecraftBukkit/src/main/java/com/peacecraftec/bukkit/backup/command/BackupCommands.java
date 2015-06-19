package com.peacecraftec.bukkit.backup.command;

import com.peacecraftec.bukkit.backup.BackupPermissions;
import com.peacecraftec.bukkit.backup.PeacecraftBackup;
import com.peacecraftec.module.cmd.Command;
import org.bukkit.command.CommandSender;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class BackupCommands {

    private PeacecraftBackup module;

    public BackupCommands(PeacecraftBackup module) {
        this.module = module;
    }

    @Command(aliases = { "backup" }, desc = "backup.command.backup", usage = "<start/stop>", min = 1, permission = BackupPermissions.MANAGE)
    public void backup(CommandSender sender, String command, String args[]) {
        if(args[0].equalsIgnoreCase("start")) {
            if(this.module.isBackingUp()) {
                sendMessage(sender, "backup.already-in-progress");
                return;
            }

            this.module.initiateBackup();
            sendMessage(sender, "backup.initiated");
        } else if(args[0].equalsIgnoreCase("stop")) {
            if(!this.module.isBackingUp()) {
                sendMessage(sender, "backup.not-running");
                return;
            }

            if(this.module.isBackupCancelled()) {
                sendMessage(sender, "backup.already-cancelled");
                return;
            }

            this.module.cancel();
            sendMessage(sender, "backup.cancelled");
        } else {
            sendMessage(sender, "internal.invalid-sub", "start, stop");
        }
    }

}
