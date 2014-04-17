package com.peacecraftec.web.chat;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.chat.disabled.DisabledWebchatSystem;
import com.peacecraftec.web.chat.redis.RedisWebchatSystem;

public class WebchatFactory {

	public static WebchatSystem create(ModuleManager manager) {
		try {
			return new RedisWebchatSystem(manager);
		} catch(Throwable t) {
			System.err.println("Failed to create redis webchat system, webchat will not be available.");
			t.printStackTrace();
			return new DisabledWebchatSystem(manager);
		}
	}
	
}
