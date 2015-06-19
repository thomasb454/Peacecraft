package com.peacecraftec.module.lang;

import com.peacecraftec.module.ModuleManager;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private ModuleManager manager;
    private Map<String, Language> cache = new HashMap<String, Language>();

    public LanguageManager(ModuleManager manager) {
        this.manager = manager;
    }

    public Language get(String name) {
        if(this.cache.containsKey(name)) {
            return this.cache.get(name);
        }

        Language lang = new Language(this.manager, name);
        if(lang.isEmpty()) {
            if(!name.equalsIgnoreCase("en_US")) {
                lang = this.getDefault();
            } else {
                return new EmptyLanguage(this.manager);
            }
        }

        this.cache.put(name, lang);
        return lang;
    }

    public Language getDefault() {
        return this.get("en_US");
    }
}
