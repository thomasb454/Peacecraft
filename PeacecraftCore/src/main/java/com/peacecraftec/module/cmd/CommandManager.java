package com.peacecraftec.module.cmd;

import com.peacecraftec.module.Module;

public interface CommandManager {

	public void register(Module module, Executor exec);

	public void unregister(Module module, Executor exec);
	
	public void unregisterAll(Module module);

}
