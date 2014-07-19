package com.peacecraftec.bukkit;

import com.peacecraftec.bukkit.backup.PeacecraftBackup;
import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.bukkit.internal.module.BukkitModuleManager;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import com.peacecraftec.bukkit.restrictions.PeacecraftRestrictions;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
import com.peacecraftec.bukkit.worlds.PeacecraftWorlds;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.storage.yaml.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: use translation system for command descriptions
public class Peacecraft extends JavaPlugin {

	private ModuleManager modules;
	
	@Override
	public void onEnable() {
		YamlStorage.setParameters(YamlConstructor.class, YamlRepresenter.class);
		this.modules = new BukkitModuleManager(this);
		this.modules.load(new PeacecraftCore(this.modules));
		this.modules.load(new PeacecraftWorlds(this.modules));
		this.modules.load(new PeacecraftPerms(this.modules));
		this.modules.load(new PeacecraftChat(this.modules));
		this.modules.load(new PeacecraftBackup(this.modules));
		this.modules.load(new PeacecraftRestrictions(this.modules));
		this.modules.load(new PeacecraftPortals(this.modules));
		this.modules.load(new PeacecraftStats(this.modules));
	}
	
	@Override
	public void onDisable() {
		this.modules.unload();
		this.modules.cleanup();
		this.modules = null;
	}
	
	public ModuleManager getModules() {
		return this.modules;
	}
	
}
