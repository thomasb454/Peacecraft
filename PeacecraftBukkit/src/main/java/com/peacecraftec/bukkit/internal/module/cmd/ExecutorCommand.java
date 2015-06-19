package com.peacecraftec.bukkit.internal.module.cmd;

import com.peacecraftec.module.cmd.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandException;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public final class ExecutorCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {

    private Plugin plugin;
    private Object executor;

    public ExecutorCommand(Plugin plugin, String name, Object executor) {
        super(name);
        this.plugin = plugin;
        this.executor = executor;
    }

    @Override
    public boolean execute(org.bukkit.command.CommandSender sender, String label, String[] args) {
        if(!this.testPermission(sender)) {
            return true;
        }

        try {
            for(Method method : this.executor.getClass().getDeclaredMethods()) {
                if(method.getAnnotation(Command.class) != null) {
                    Command cmd = method.getAnnotation(Command.class);
                    for(String alias : cmd.aliases()) {
                        if(alias.equalsIgnoreCase(label)) {
                            if((!cmd.player() && sender instanceof Player) || (!cmd.console() && sender instanceof ConsoleCommandSender) || (!cmd.commandblock() && sender instanceof BlockCommandSender)) {
                                sendMessage(sender, "internal.cannot-use-command");
                                return true;
                            }

                            if(args.length < cmd.min() || (cmd.max() > 0 && args.length > cmd.max())) {
                                sendMessage(sender, "internal.usage", ((sender instanceof Player) ? "/" : "") + alias + " " + cmd.usage());
                                return true;
                            }

                            try {
                                method.invoke(this.executor, sender, label, args);
                            } catch(Exception e) {
                                sendMessage(sender, "internal.command-error");
                                Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Exception while executing command \"" + alias + "\".", e);
                            }

                            return true;
                        }
                    }
                }
            }

            return false;
        } catch(Throwable e) {
            throw new CommandException("Unhandled exception executing Peacecraft command '" + label + "'", e);
        }
    }

    @Override
    public boolean testPermission(org.bukkit.command.CommandSender target) {
        if(this.testPermissionSilent(target)) {
            return true;
        }

        sendMessage(target, "internal.no-command-perm");
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

}
