package com.peacecraftec.module;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ModuleLogger extends Logger {

    private String prefix;

    public ModuleLogger(Module module) {
        super(module.getClass().getCanonicalName(), null);
        this.prefix = "[" + module.getManager().getImplementationName() + "][" + module.getName() + "] ";
        this.setParent(module.getManager().getLogger());
        this.setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.prefix + logRecord.getMessage());
        super.log(logRecord);
    }

}
