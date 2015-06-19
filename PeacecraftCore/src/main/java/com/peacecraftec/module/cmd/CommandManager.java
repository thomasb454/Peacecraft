package com.peacecraftec.module.cmd;

import com.peacecraftec.module.Module;

public interface CommandManager {
    public void register(Module module, Object executor);

    public void unregister(Module module, Object executor);

    public void unregisterAll(Module module);
}
