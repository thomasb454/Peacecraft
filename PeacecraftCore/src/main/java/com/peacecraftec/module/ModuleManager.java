package com.peacecraftec.module;

import com.peacecraftec.module.cmd.CommandManager;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import com.peacecraftec.module.event.EventManager;
import com.peacecraftec.module.lang.LanguageManager;
import com.peacecraftec.module.permission.PermissionManager;
import com.peacecraftec.module.scheduler.Scheduler;
import com.peacecraftec.redis.RedisDatabase;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public abstract class ModuleManager {

	private File dataFolder;
	private Storage config;
	private RedisDatabase db;
	private LanguageManager languages = new LanguageManager(this);
	private Map<String, Class<? extends Module>> moduleTypes = new LinkedHashMap<String, Class<? extends Module>>();
	private Map<String, Module> modules = new LinkedHashMap<String, Module>();
	
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

	public void register(String name, Class<? extends Module> module) {
		this.moduleTypes.put(name, module);
	}
	
	public void loadAll() {
		for(String name : this.moduleTypes.keySet()) {
			this.load(name);
		}
	}

	public void load(String name) {
		if(this.isEnabled(name)) {
			return;
		}

		Class<? extends Module> type = this.getModuleType(name);
		if(this.getCoreConfig().getBoolean("modules." + name.toLowerCase(), true)) {
			Module module = null;
			try {
				module = type.getDeclaredConstructor(String.class, ModuleManager.class).newInstance(name, this);
				this.modules.put(name, module);
				module.onEnable();
				this.getLogger().info("Module \"" + module.getName() + "\" successfully loaded.");
				this.getEventManager().callModuleEnableEvent(module);
			} catch(Throwable t) {
				this.getLogger().severe("Failed to load module \"" + name + "\"!");
				t.printStackTrace();
				if(module != null) {
					try {
						this.unload(name, false);
					} catch(Throwable t1) {
					}
				}
			}
		}
	}

	public void unloadAll() {
		for(String name : this.moduleTypes.keySet()) {
			this.unload(name);
		}
	}
	
	public void unload(String name) {
		this.unload(name, true);
	}

	public void unload(String name, boolean onDisable) {
		if(!this.isEnabled(name)) {
			return;
		}

		try {
			Module module = this.getModule(name);
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
			this.getLogger().severe("Failed to unload module \"" + name + "\"!");
			t.printStackTrace();
		}
		
		this.modules.remove(name);
	}

	public List<String> getModuleTypes() {
		return new ArrayList<String>(this.moduleTypes.keySet());
	}

	public Class<? extends Module> getModuleType(String name) {
		for(String type : this.moduleTypes.keySet()) {
			if(name.equalsIgnoreCase(type)) {
				return this.moduleTypes.get(type);
			}
		}

		return null;
	}

	public boolean isEnabled(String name) {
		return this.getModule(name) != null;
	}
	
	public List<Module> getModules() {
		return new ArrayList<Module>(this.modules.values());
	}

	public Module getModule(String name) {
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

	public RedisDatabase getDatabase() {
		return this.db;
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
