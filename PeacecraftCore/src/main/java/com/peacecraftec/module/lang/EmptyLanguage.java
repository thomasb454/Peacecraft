package com.peacecraftec.module.lang;

import com.peacecraftec.module.ModuleManager;

public class EmptyLanguage extends Language {

    public EmptyLanguage(ModuleManager manager) {
        super(manager, "");
    }

    public boolean isEmpty() {
        return true;
    }

    public String translate(String key) {
        return key;
    }

    public String translate(String key, Object... replace) {
        return String.format(key, replace);
    }

}
