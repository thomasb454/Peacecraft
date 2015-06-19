package com.peacecraftec.bukkit.internal.module.cmd;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class BukkitCommandManager implements CommandManager {
    private Plugin plugin;
    private ModuleManager manager;
    private Map<Module, List<Object>> executors = new HashMap<Module, List<Object>>();

    public BukkitCommandManager(Plugin plugin, ModuleManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public void register(Module module, Object exec) {
        CommandMap cmdmap = getCommandMap();
        Map<String, org.bukkit.command.Command> map = getCommands(cmdmap);
        if(map != null) {
            try {
                for(Command command : getCommands(exec)) {
                    org.bukkit.command.Command cmd = new ExecutorCommand(this.plugin, command.aliases()[0], exec);
                    cmd.setAliases(Arrays.asList(command.aliases().length > 1 ? Arrays.copyOfRange(command.aliases(), 1, command.aliases().length) : new String[0]));
                    cmd.setDescription(this.manager.getLanguageManager().getDefault().translate(command.desc()));
                    cmd.setUsage("/<command> " + command.usage());
                    cmd.setPermission(command.permission());
                    cmd.setPermissionMessage(this.manager.getLanguageManager().getDefault().translate("internal.no-command-perm"));
                    for(String alias : command.aliases()) {
                        map.put(alias, cmd);
                    }

                    map.put("peacecraft:" + command.aliases()[0], cmd);
                    cmd.register(cmdmap);
                    this.plugin.getServer().getHelpMap().addTopic(new GenericCommandHelpTopic(cmd));
                }

                if(!this.executors.containsKey(module)) {
                    this.executors.put(module, new ArrayList<Object>());
                }

                this.executors.get(module).add(exec);
            } catch(Exception e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to load commands!", e);
            }
        }
    }

    @Override
    public void unregister(Module module, Object executor) {
        CommandMap cmdmap = getCommandMap();
        Map<String, org.bukkit.command.Command> map = getCommands(cmdmap);
        if(map != null) {
            try {
                for(Command command : getCommands(executor)) {
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
                    this.executors.get(module).remove(executor);
                }
            } catch(Exception e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to unregister commands for executor \"" + executor.getClass().getSimpleName() + "\"!", e);
            }
        }
    }

    @Override
    public void unregisterAll(Module module) {
        if(this.executors.containsKey(module)) {
            for(Object executor : new ArrayList<Object>(this.executors.get(module))) {
                this.unregister(module, executor);
            }

            this.executors.remove(module);
        }
    }

    private static CommandMap getCommandMap() {
        try {
            Field f = SimplePluginManager.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            return (CommandMap) f.get(Bukkit.getServer().getPluginManager());
        } catch(Exception e) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to get command map.", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, org.bukkit.command.Command> getCommands(CommandMap map) {
        try {
            Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            return (Map<String, org.bukkit.command.Command>) f.get(map);
        } catch(Exception e) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to get commands.", e);
            return null;
        }
    }

    private static List<Command> getCommands(Object executor) {
        List<Command> commands = new ArrayList<Command>();
        for(Method method : executor.getClass().getMethods()) {
            if(method.getAnnotation(Command.class) != null) {
                commands.add(method.getAnnotation(Command.class));
            }
        }

        return commands;
    }
}
