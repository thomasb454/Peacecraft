package com.peacecraftec.module.event;

import com.peacecraftec.module.Module;

public interface EventManager {

	public void callModuleEnableEvent(Module module);
	
	public void register(Module module, Object listener);
	
	public void unregister(Module module, Object listener);
	
	public void unregisterAll(Module module);
	
}
