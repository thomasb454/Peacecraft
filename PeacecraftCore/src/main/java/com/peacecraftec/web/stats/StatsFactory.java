package com.peacecraftec.web.stats;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.stats.disabled.DisabledStatSystem;
import com.peacecraftec.web.stats.redis.RedisStatSystem;

public class StatsFactory {

    public static StatSystem create(ModuleManager manager, String server) {
        try {
            return new RedisStatSystem(manager, server);
        } catch(Throwable t) {
            System.err.println("[PeacecraftCore] Failed to create redis stat system, stats will not be available.");
            t.printStackTrace();
            return new DisabledStatSystem();
        }
    }

}
