package com.peacecraftec.bukkit.internal.module.cmd;

import org.bukkit.command.CommandException;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;

public final class ExecutorCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

	private Plugin plugin;
	private ModuleManager manager;
	private Executor exec;

	public ExecutorCommand(Plugin plugin, ModuleManager manager, String name, Executor exec) {
		super(name);
		this.plugin = plugin;
		this.manager = manager;
		this.exec = exec;
	}

	@Override
	public boolean execute(org.bukkit.command.CommandSender send, String label, String[] args) {
		if(!this.testPermission(send)) return true;
		CommandSender sender = BukkitCommandSender.wrap(send, this.manager.getLanguageManager());
		try {
			return this.exec.execute(sender, label, args);
		} catch (Throwable e) {
			throw new CommandException("Unhandled exception executing Peacecraft command '" + label + "'", e);
		}
	}

	@Override
	public boolean testPermission(org.bukkit.command.CommandSender target) {
		if(this.testPermissionSilent(target)) {
			return true;
		}

		CommandSender sender = BukkitCommandSender.wrap(target, this.manager.getLanguageManager());
		sender.sendMessage("generic.no-command-perm");
		return false;
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

}
