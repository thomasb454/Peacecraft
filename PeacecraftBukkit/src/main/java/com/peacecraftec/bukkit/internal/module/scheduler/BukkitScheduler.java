package com.peacecraftec.bukkit.internal.module.scheduler;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class BukkitScheduler implements Scheduler {

    private Plugin plugin;
    private Map<Module, List<Integer>> tasks = new HashMap<Module, List<Integer>>();

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int runTask(Module module, Runnable task) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTask(this.plugin, task).getTaskId());
    }

    @Override
    public int runTaskAsynchronously(Module module, Runnable task) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, task).getTaskId());
    }

    @Override
    public int runTaskLater(Module module, Runnable task, long delay) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTaskLater(this.plugin, task, delay).getTaskId());
    }

    @Override
    public int runTaskLaterAsynchronously(Module module, Runnable task, long delay) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, task, delay).getTaskId());
    }

    @Override
    public int runTaskTimer(Module module, Runnable task, long delay, long period) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTaskTimer(this.plugin, task, delay, period).getTaskId());
    }

    @Override
    public int runTaskTimerAsynchronously(Module module, Runnable task, long delay, long period) throws IllegalArgumentException {
        return this.addTask(module, Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, task, delay, period).getTaskId());
    }

    @Override
    public void cancelTask(Module module, int task) {
        Bukkit.getServer().getScheduler().cancelTask(task);
        if(this.tasks.containsKey(module)) {
            this.tasks.get(module).remove((Integer) task);
        }
    }

    private int addTask(Module module, int task) {
        if(!this.tasks.containsKey(module)) {
            this.tasks.put(module, new ArrayList<Integer>());
        }

        this.tasks.get(module).add(task);
        return task;
    }

    @Override
    public void cancelAllTasks(Module module) {
        if(this.tasks.containsKey(module)) {
            for(int task : new ArrayList<Integer>(this.tasks.get(module))) {
                this.cancelTask(module, task);
            }

            this.tasks.remove(module);
        }
    }

    @Override
    public <T> void callSync(Callable<T> task) {
        Bukkit.getServer().getScheduler().callSyncMethod(this.plugin, task);
    }

}
