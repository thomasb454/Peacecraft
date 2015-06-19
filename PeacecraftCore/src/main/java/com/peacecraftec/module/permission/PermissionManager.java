package com.peacecraftec.module.permission;

import com.peacecraftec.module.Module;

public interface PermissionManager {

    public void register(Module module, Class<?> clazz);

    public void unregister(Module module, Class<?> clazz);

    public void unregisterAll(Module module);

}
