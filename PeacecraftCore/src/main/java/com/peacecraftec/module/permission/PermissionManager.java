package com.peacecraftec.module.permission;

import com.peacecraftec.module.Module;

public interface PermissionManager {
	
	public void register(Module module, Class<? extends PermissionContainer> clazz);

	public void unregister(Module module, Class<? extends PermissionContainer> clazz);
	
	public void unregisterAll(Module module);
	
}
