package com.peacecraftec.module;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.peacecraftec.module.cmd.CommandManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.module.event.EventManager;
import com.peacecraftec.module.lang.LanguageManager;
import com.peacecraftec.module.permission.PermissionManager;
import com.peacecraftec.module.scheduler.Scheduler;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

public abstract class ModuleManager {

	private File dataFolder;
	private Storage config;
	private RedisDatabase db;
	private LanguageManager languages = new LanguageManager(this);
	private Map<String, Module> modules = new HashMap<String, Module>();
	
	public ModuleManager(File dataFolder) {
		this.dataFolder = dataFolder;
		if(!this.dataFolder.exists()) {
			this.dataFolder.mkdirs();
		}
		
		this.config = new YamlStorage(new File(this.getDataFolder(), "config.yml"));
		this.config.load();
		this.db = new RedisDatabase("localhost");
	}
	
	public void cleanup() {
		this.db.cleanup();
		this.db = null;
	}

	public void unload() {
		for(Module module : new HashMap<String, Module>(this.modules).values()) {
			this.unload(module);
		}
	}
	
	public void load(Module module) {
		if(this.getCoreConfig().getBoolean("modules." + module.getName().toLowerCase(), true)) {
			try {
				this.modules.put(module.getName(), module);
				module.onEnable();
				this.getLogger().info("Module \"" + module.getName() + "\" successfully loaded.");
				this.getEventManager().callModuleEnableEvent(module);
			} catch(Throwable t) {
				this.getLogger().severe("Failed to load module \"" + module.getName() + "\"!");
				t.printStackTrace();
				this.unload(module, false);
			}
		}
	}
	
	public void unload(String name) {
		this.unload(this.modules.get(name));
	}
	
	public void unload(Module module) {
		this.unload(module, true);
	}
	
	public void unload(Module module, boolean onDisable) {
		if(module == null) {
			return;
		}
		
		try {
			if(onDisable) {
				try {
					module.onDisable();
				} catch(Throwable t) {
					this.getLogger().severe("Failed to call \"onDisable\" of module \"" + module.getName() + "\"!");
					t.printStackTrace();
				}
			}
			
			this.getCommandManager().unregisterAll(module);
			this.getPermissionManager().unregisterAll(module);
			this.getEventManager().unregisterAll(module);
			this.getScheduler().cancelAllTasks(module);
			this.getLogger().info("Module \"" + module.getName() + "\" successfully unloaded.");
		} catch(Throwable t) {
			this.getLogger().severe("Failed to unload module \"" + module.getName() + "\"!");
			t.printStackTrace();
		}
		
		this.modules.remove(module.getName());
	}
	
	public boolean isEnabled(String name) {
		return this.getModule(name) != null;
	}
	
	public List<Module> getModules() {
		return new ArrayList<Module>(this.modules.values());
	}
	
	public Module getModule(String name) {
		return this.modules.get(name);
	}
	
	public Module looseGetModule(String name) {
		for(String module : this.modules.keySet()) {
			if(name.equalsIgnoreCase(module)) {
				return this.modules.get(module);
			}
		}
		
		return null;
	}
	
	public String listString() {
		StringBuilder ret = new StringBuilder();
		for(String name : this.modules.keySet()) {
			if(ret.length() > 0) {
				ret.append(", ");
			}
			
			ret.append(name);
		}
		
		return ret.toString();
	}
	
	public File getDataFolder() {
		return this.dataFolder;
	}
	
	public Storage getCoreConfig() {
		return this.config;
	}
	
	public LanguageManager getLanguageManager() {
		return this.languages;
	}
	
	public String getCasedUsername(String username) {
		if(!this.db.contains("nametocase." + username.toLowerCase())) {
			return username;
		}
		
		return this.db.getString("nametocase." + username.toLowerCase());
	}
	
	public String getUsername(UUID uuid) {
		return this.db.getString("uuidtoname." + uuid.toString());
	}
	
	public UUID getUUID(String username) {
		if(!this.db.contains("nametouuid." + username.toLowerCase())) {
			return null;
		}
		
		return UUID.fromString(this.db.getString("nametouuid." + username.toLowerCase()));
	}
	
	public void setUserPair(UUID uuid, String username) {
		this.db.setValue("nametocase." + username.toLowerCase(), username);
		this.db.setValue("uuidtoname." + uuid.toString(), username);
		this.db.setValue("nametouuid." + username.toLowerCase(), uuid.toString());
	}
	
	public abstract String getImplementationName();
	
	public abstract Logger getLogger();
	
	public abstract InputStream getResource(String path);
	
	public abstract String getDefaultWorld();
	
	public abstract CommandManager getCommandManager();
	
	public abstract PermissionManager getPermissionManager();
	
	public abstract EventManager getEventManager();
	
	public abstract Scheduler getScheduler();
	
	public abstract void broadcastMessage(String key);
	
	public abstract void broadcastMessage(String key, Object... args);

	public abstract PlayerSender[] getPlayerSenders();
	
	public abstract List<PlayerSender> matchPlayerSender(String name);
	
	public abstract PlayerSender getPlayerSender(String name);

}
