package com.peacecraftec.bukkit.perms.core;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import com.peacecraftec.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;

public class PermissionPlayer {

    private UUID uuid;
    private PermissionWorld world;
    private Storage data;
    private PeacecraftPerms module;

    public PermissionPlayer(UUID uuid, PermissionWorld world, Storage data, PeacecraftPerms module) {
        this.uuid = uuid;
        this.world = world;
        this.data = data;
        this.module = module;
    }

    public String getName() {
        return this.module.getManager().getUsername(this.uuid);
    }

    public PermissionWorld getWorld() {
        return this.world;
    }

    public String getPrefix() {
        if(this.data.contains("players." + this.uuid.toString() + ".info.prefix")) {
            return ChatColor.translateAlternateColorCodes('&', this.data.getString("players." + this.uuid.toString() + ".info.prefix"));
        }

        StringBuilder prefix = new StringBuilder();
        for(PermissionGroup group : this.getGroups()) {
            prefix.append(group.getPrefix());
        }

        return prefix.toString();
    }

    public String getSuffix() {
        if(this.data.contains("players." + this.uuid.toString() + ".info.suffix")) {
            return ChatColor.translateAlternateColorCodes('&', this.data.getString("players." + this.uuid.toString() + ".info.suffix"));
        }

        StringBuilder prefix = new StringBuilder();
        for(PermissionGroup group : this.getGroups()) {
            prefix.append(group.getSuffix());
        }

        return prefix.toString();
    }

    public void setPrefix(String prefix) {
        this.data.setValue("players." + this.uuid.toString() + ".info.prefix", prefix);
        this.data.save();
        if(this.module.getManager().isEnabled("Chat")) {
            Player player = Bukkit.getServer().getPlayerExact(this.getName());
            if(player != null && this.getWorld().hasGroupsOf(player.getWorld().getName())) {
                ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
            }
        }
    }

    public void setSuffix(String suffix) {
        this.data.setValue("players." + this.uuid.toString() + ".info.suffix", suffix);
        this.data.save();
        if(this.module.getManager().isEnabled("Chat")) {
            Player player = Bukkit.getServer().getPlayerExact(this.getName());
            if(player != null && this.getWorld().hasGroupsOf(player.getWorld().getName())) {
                ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
            }
        }
    }

    public void removePrefix() {
        this.data.remove("players." + this.uuid.toString() + ".info.prefix");
        this.data.save();
        if(this.module.getManager().isEnabled("Chat")) {
            Player player = Bukkit.getServer().getPlayerExact(this.getName());
            if(player != null && this.getWorld().hasGroupsOf(player.getWorld().getName())) {
                ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
            }
        }
    }

    public void removeSuffix() {
        this.data.remove("players." + this.uuid.toString() + ".info.suffix");
        this.data.save();
        if(this.module.getManager().isEnabled("Chat")) {
            Player player = Bukkit.getServer().getPlayerExact(this.getName());
            if(player != null && this.getWorld().hasGroupsOf(player.getWorld().getName())) {
                ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(player);
            }
        }
    }

    public List<String> getGroupNames() {
        if(!this.data.contains("players." + this.uuid.toString() + ".groups")) {
            List<String> groups = new ArrayList<String>();
            groups.add(this.world.getDefaultGroup() != null ? this.world.getDefaultGroup().getName() : "default");
            this.data.setValue("players." + this.uuid.toString() + ".groups", groups);
            this.data.save();
        }

        return this.data.getList("players." + this.uuid.toString() + ".groups", String.class);
    }

    public List<PermissionGroup> getGroups() {
        List<PermissionGroup> ret = new ArrayList<PermissionGroup>();
        for(String group : this.getGroupNames()) {
            PermissionGroup g = this.world.getGroup(group);
            if(g != null) {
                ret.add(g);
            }
        }

        return ret;
    }

    public boolean addGroup(PermissionGroup group) {
        return this.addGroup(group.getName());
    }

    public boolean addGroup(String group) {
        group = group.toLowerCase();
        List<String> groups = this.getGroupNames();
        if(!groups.contains(group)) {
            groups.add(group);
            this.data.setValue("players." + this.uuid.toString() + ".groups", groups);
            this.data.save();

            Player p = Bukkit.getServer().getPlayerExact(this.getName());
            if(p != null && this.getWorld().hasGroupsOf(p.getWorld().getName())) {
                this.module.refreshPermissions(p);
            }

            if(this.module.getManager().isEnabled("Chat")) {
                if(p != null && this.getWorld().hasGroupsOf(p.getWorld().getName())) {
                    ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(p);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean removeGroup(PermissionGroup group) {
        return this.removeGroup(group.getName());
    }

    public boolean removeGroup(String group) {
        group = group.toLowerCase();
        List<String> groups = this.getGroupNames();
        if(groups.contains(group)) {
            groups.remove(group);
            this.data.setValue("players." + this.uuid.toString() + ".groups", groups);
            this.data.save();

            Player p = Bukkit.getServer().getPlayerExact(this.getName());
            if(p != null && this.getWorld().hasGroupsOf(p.getWorld().getName())) {
                this.module.refreshPermissions(p);
            }

            if(this.module.getManager().isEnabled("Chat")) {
                if(p != null) {
                    ((PeacecraftChat) this.module.getManager().getModule("Chat")).loadDisplayName(p);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public List<String> getRawPermissions() {
        if(!this.data.contains("players." + this.uuid.toString() + ".permissions")) {
            return new ArrayList<String>();
        }

        return this.data.getList("players." + this.uuid.toString() + ".permissions", String.class);
    }

    public Map<String, Boolean> getOwnPermissions() {
        Set<Permission> known = Bukkit.getPluginManager().getPermissions();
        List<String> perms = this.getRawPermissions();
        Map<String, Boolean> ret = new HashMap<String, Boolean>();
        for(String perm : perms) {
            if(perm.startsWith("INTERNAL_PERMISSION") || perm.startsWith("-INTERNAL_PERMISSION")) continue;
            boolean val = true;
            if(perm.startsWith("-")) {
                perm = perm.substring(1, perm.length());
                val = false;
            }

            if(perm.endsWith("*")) {
                String node = perm.substring(0, perm.length() - 1);
                if(node.endsWith(".")) {
                    node = node.substring(0, node.length() - 1);
                }

                for(Permission p : known) {
                    if(p.getName().startsWith(node) && !p.getName().startsWith("INTERNAL_PERMISSION")) {
                        if(!ret.containsKey(p.getName()) || ret.get(p.getName()) != val) {
                            ret.put(p.getName(), val);
                        }
                    }
                }
            }

            if(!ret.containsKey(perm) || ret.get(perm) != val) {
                ret.put(perm, val);
            }
        }

        return ret;
    }

    public Map<String, Boolean> getAllPermissions() {
        Map<String, Boolean> perms = new HashMap<String, Boolean>();
        for(PermissionGroup group : this.getGroups()) {
            perms.putAll(group.getAllPermissions());
        }

        perms.putAll(this.getOwnPermissions());
        return perms;
    }

    public boolean addPermission(String permission) {
        List<String> perms = this.getRawPermissions();
        if(!perms.contains(permission)) {
            perms.add(permission);
            this.data.setValue("players." + this.uuid.toString() + ".permissions", perms);
            this.data.save();

            Player p = Bukkit.getServer().getPlayerExact(this.getName());
            if(p != null && this.getWorld().hasGroupsOf(p.getWorld().getName())) {
                this.module.refreshPermissions(p);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean removePermission(String permission) {
        List<String> perms = this.getRawPermissions();
        if(perms.contains(permission)) {
            perms.remove(permission);
            this.data.setValue("players." + this.uuid.toString() + ".permissions", perms);
            this.data.save();

            Player p = Bukkit.getServer().getPlayerExact(this.getName());
            if(p != null && this.getWorld().hasGroupsOf(p.getWorld().getName())) {
                this.module.refreshPermissions(p);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean getInfoBoolean(String name, boolean def) {
        return this.data.getBoolean("players." + this.uuid.toString() + ".info." + name, def);
    }

    public double getInfoDouble(String name, double def) {
        return this.data.getDouble("players." + this.uuid.toString() + ".info." + name, def);
    }

    public int getInfoInt(String name, int def) {
        return this.data.getInteger("players." + this.uuid.toString() + ".info." + name, def);
    }

    public String getInfoString(String name, String def) {
        return this.data.getString("players." + this.uuid.toString() + ".info." + name, def);
    }

    public void setInfo(String name, Object value) {
        this.data.setValue("players." + this.uuid.toString() + ".info." + name, value);
        this.data.save();
    }

}
