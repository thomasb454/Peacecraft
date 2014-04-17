package com.peacecraftec.bukkit.internal.module.cmd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.CommandManager;
import com.peacecraftec.module.cmd.Executor;

public class BukkitCommandManager implements CommandManager {

	private Plugin plugin;
	private ModuleManager manager;
	private Map<Module, List<Executor>> executors = new HashMap<Module, List<Executor>>();
	
	public BukkitCommandManager(Plugin plugin, ModuleManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}
	
	@Override
	public void register(Module module, Executor exec) {
		CommandMap cmdmap = getCommandMap();
		Map<String, org.bukkit.command.Command> map = getCommands(cmdmap);
		if(map != null) {
			try {
				for(Command command : exec.getCommands()) {
					org.bukkit.command.Command cmd = new ExecutorCommand(this.plugin, this.manager, command.aliases()[0], exec);
					cmd.setAliases(Arrays.asList(command.aliases().length > 1 ? Arrays.copyOfRange(command.aliases(), 1, command.aliases().length) : new String[0]));
					cmd.setDescription(command.desc());
					cmd.setUsage("/<command> " + command.usage());
					cmd.setPermission(command.permission());
					cmd.setPermissionMessage(this.manager.getLanguageManager().getDefault().translate("generic.no-command-perm"));
					for(String alias : command.aliases()) {
						map.put(alias, cmd);
					}

					map.put("peacecraft:" + command.aliases()[0], cmd);
					cmd.register(cmdmap);
					this.plugin.getServer().getHelpMap().addTopic(new GenericCommandHelpTopic(cmd));
				}
				
				if(!this.executors.containsKey(module)) {
					this.executors.put(module, new ArrayList<Executor>());
				}
				
				this.executors.get(module).add(exec);
			} catch (Exception e) {
				System.err.println("Failed to load commands!");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void unregister(Module module, Executor exec) {
		CommandMap cmdmap = getCommandMap();
		Map<String, org.bukkit.command.Command> map = getCommands(cmdmap);
		if(map != null) {
			try {
				for(Command command : exec.getCommands()) {
					org.bukkit.command.Command cmd = map.get(command.aliases()[0]);
					if(cmd != null) {
						for(String alias : command.aliases()) {
							map.remove(alias);
						}
						
						map.remove("peacecraft:" + command.aliases()[0]);
						cmd.unregister(cmdmap);
					}
				}
				
				if(this.executors.containsKey(module)) {
					this.executors.get(module).remove(exec);
				}
			} catch (Exception e) {
				System.err.println("Failed to unregister commands for executor \"" + exec.getClass().getSimpleName() + "\"!");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void unregisterAll(Module module) {
		if(this.executors.containsKey(module)) {
			for(Executor exec : new ArrayList<Executor>(this.executors.get(module))) {
				this.unregister(module, exec);
			}
			
			this.executors.remove(module);
		}
	}
	
	private static CommandMap getCommandMap() {
		try {
			Field f = SimplePluginManager.class.getDeclaredField("commandMap");
			f.setAccessible(true);
			return (CommandMap) f.get(Bukkit.getServer().getPluginManager());
		} catch (Exception e) {
			System.err.println("Failed to get Bukkit command map. No commands for you!");
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<String, org.bukkit.command.Command> getCommands(CommandMap map) {
		try {
			Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
			f.setAccessible(true);
			return (Map<String, org.bukkit.command.Command>) f.get(map);
		} catch (Exception e) {
			System.err.println("Failed to get command map. No commands for you!");
			e.printStackTrace();
			return null;
		}
	}

}
