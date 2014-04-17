package com.peacecraftec.bukkit.perms.vault;

import com.peacecraftec.bukkit.perms.core.PermissionGroup;
import com.peacecraftec.bukkit.perms.core.PermissionManager;
import com.peacecraftec.bukkit.perms.core.PermissionPlayer;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class PeaceChatVault extends Chat {

	private PermissionManager manager;
	
	public PeaceChatVault(Permission perms, PermissionManager manager) {
		super(perms);
		this.manager = manager;
	}

	@Override
	public String getName() {
		return "PeacecraftPerms";
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean def) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return false;
		}
		
		return g.getInfoBoolean(node, def);
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double def) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return 0;
		}
		
		return g.getInfoDouble(node, def);
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int def) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return 0;
		}
		
		return g.getInfoInt(node, def);
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String def) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return null;
		}
		
		return g.getInfoString(node, def);
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return null;
		}
		
		return g.getPrefix();
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return null;
		}
		
		return g.getSuffix();
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean def) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return false;
		}
		
		return p.getInfoBoolean(node, def);
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double def) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return 0;
		}
		
		return p.getInfoDouble(node, def);
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int def) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return 0;
		}
		
		return p.getInfoInt(node, def);
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String def) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return null;
		}
		
		return p.getInfoString(node, def);
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return null;
		}
		
		return p.getPrefix();
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return null;
		}
		
		return p.getSuffix();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setInfoBoolean(node, value);
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setInfoDouble(node, value);
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setInfoInteger(node, value);
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setInfoString(node, value);
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setPrefix(prefix);
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		PermissionGroup g = this.manager.getWorld(world).getGroup(group);
		if(g == null) {
			return;
		}
		
		g.setSuffix(suffix);
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setInfo(node, value);
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setInfo(node, value);
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setInfo(node, value);
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setInfo(node, value);
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setPrefix(prefix);
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
		if(p == null) {
			return;
		}
		
		p.setSuffix(suffix);
	}

}
