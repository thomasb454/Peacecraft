package com.peacecraftec.module.cmd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.peacecraftec.module.cmd.sender.CommandBlockSender;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.ConsoleSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;

public abstract class Executor {

	public Executor() {
	}
	
	public boolean execute(CommandSender sender, String command, String args[]) {
		for(Method method : this.getClass().getDeclaredMethods()) {
			if(method.getAnnotation(Command.class) != null) {
				Command cmd = method.getAnnotation(Command.class);
				for(String alias : cmd.aliases()) {
					if(alias.equalsIgnoreCase(command)) {
						if((!cmd.player() && sender instanceof PlayerSender) || (!cmd.console() && sender instanceof ConsoleSender) || (!cmd.commandblock() && sender instanceof CommandBlockSender)) {
							sender.sendMessage("generic.cannot-use-command");
							return true;
						}
						
						if(args.length < cmd.min() || (cmd.max() > 0 && args.length > cmd.max())) {
							sender.sendMessage("generic.usage", ((sender instanceof PlayerSender) ? "/" : "") + alias + " " + cmd.usage());
							return true;
						}
						
						try {
							method.invoke(this, sender, command, args);
						} catch (Exception e) {
							sender.sendMessage("generic.command-error");
							System.err.println("Exception while executing command " + alias + ": " + e);
							e.printStackTrace();
						}
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public List<Command> getCommands() {
		List<Command> commands = new ArrayList<Command>();
		for(Method method : this.getClass().getMethods()) {
			if(method.getAnnotation(Command.class) != null) {
				commands.add(method.getAnnotation(Command.class));
			}
		}
		
		return commands;
	}
	
}
