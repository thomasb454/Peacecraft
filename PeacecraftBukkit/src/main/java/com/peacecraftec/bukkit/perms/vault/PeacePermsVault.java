package com.peacecraftec.bukkit.perms.vault;

import com.peacecraftec.bukkit.perms.core.PermissionGroup;
import com.peacecraftec.bukkit.perms.core.PermissionManager;
import com.peacecraftec.bukkit.perms.core.PermissionPlayer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class PeacePermsVault extends Permission {

    private PermissionManager manager;

    public PeacePermsVault(PermissionManager manager) {
        this.manager = manager;
    }

    @Override
    public String[] getGroups() {
        List<String> ret = new ArrayList<String>();
        for(World world : Bukkit.getServer().getWorlds()) {
            ret.addAll(this.manager.getWorld(world.getName()).getGroups());
        }

        return ret.toArray(new String[ret.size()]);
    }

    @Override
    public String getName() {
        return "PeacecraftPerms";
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return new String[0];
        }

        List<String> groups = p.getGroupNames();
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return null;
        }

        return p.getGroupNames().get(0);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        PermissionGroup g = this.manager.getWorld(world).getGroup(group);
        if(g == null) {
            return false;
        }

        return g.addPermission(permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        PermissionGroup g = this.manager.getWorld(world).getGroup(group);
        if(g == null) {
            return false;
        }

        Boolean b = g.getAllPermissions().get(permission);
        if(b != null) {
            return b;
        } else {
            return false;
        }
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        PermissionGroup g = this.manager.getWorld(world).getGroup(group);
        if(g == null) {
            return false;
        }

        return g.removePermission(permission);
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        return p.addPermission(permission);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        return p.addGroup(group);
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        Boolean b = p.getAllPermissions().get(permission);
        if(b != null) {
            return b;
        } else {
            return false;
        }
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        return p.getGroupNames().contains(group);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        return p.removePermission(permission);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        PermissionPlayer p = this.manager.getWorld(world).getPlayer(player);
        if(p == null) {
            return false;
        }

        return p.removeGroup(group);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

}
