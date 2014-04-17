package com.peacecraftec.module.scheduler;

import java.util.concurrent.Callable;

import com.peacecraftec.module.Module;

public interface Scheduler {
	
    public int runTask(Module module, Runnable task);

    public int runTaskAsynchronously(Module module, Runnable task);

    public int runTaskLater(Module module, Runnable task, long delay);

    public int runTaskLaterAsynchronously(Module module, Runnable task, long delay);

    public int runTaskTimer(Module module, Runnable task, long delay, long period);

    public int runTaskTimerAsynchronously(Module module, Runnable task, long delay, long period);
	
    public void cancelTask(Module module, int task);
    
    public void cancelAllTasks(Module module);
	
    public <T> void callSync(Callable<T> task);
	
}
