package com.peacecraftec.bukkit.portals;

import com.peacecraftec.bukkit.portals.command.PortalCommands;
import com.peacecraftec.bukkit.portals.core.PortalManager;
import com.peacecraftec.bukkit.portals.listener.PortalListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

public class PeacecraftPortals extends Module {

	private PortalManager manager;
	
	public PeacecraftPortals(String name, ModuleManager manager) {
		super(name, manager);
	}

	@Override
	public void onEnable() {
		this.manager = new PortalManager(this);
		this.getManager().getPermissionManager().register(this, PortalPermissions.class);
		this.getManager().getCommandManager().register(this, new PortalCommands(this));
		this.getManager().getEventManager().register(this, new PortalListener(this));
	}

	@Override
	public void onDisable() {
		this.manager = null;
	}
	
	@Override
	public void reload() {
		this.getPortalManager().reload();
	}

	public PortalManager getPortalManager() {
		return this.manager;
	}

}
