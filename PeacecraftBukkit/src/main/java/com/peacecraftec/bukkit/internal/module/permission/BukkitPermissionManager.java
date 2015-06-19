package com.peacecraftec.bukkit.internal.module.permission;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BukkitPermissionManager implements PermissionManager {

    private Map<Module, List<Class<?>>> permissions = new HashMap<Module, List<Class<?>>>();

    @Override
    public void register(Module module, Class<?> clazz) {
        for(Field f : clazz.getDeclaredFields()) {
            try {
                if(String.class.isAssignableFrom(f.getType())) {
                    Perm p = f.getAnnotation(Perm.class);
                    if(p != null) {
                        String val = (String) f.get(null);
                        if(Bukkit.getPluginManager().getPermission(val) != null) {
                            return;
                        }

                        Permission perm = new Permission(val, p.desc(), org.bukkit.permissions.PermissionDefault.valueOf(p.def().name()));
                        Bukkit.getPluginManager().addPermission(perm);
                    }
                }
            } catch(Exception e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to register permission of field \"" + f.getName() + "\".", e);
            }
        }

        if(!this.permissions.containsKey(module)) {
            this.permissions.put(module, new ArrayList<Class<?>>());
        }

        this.permissions.get(module).add(clazz);
    }

    @Override
    public void unregister(Module module, Class<?> clazz) {
        for(Field f : clazz.getDeclaredFields()) {
            try {
                Perm p = f.getAnnotation(Perm.class);
                if(p != null) {
                    String val = (String) f.get(null);
                    if(Bukkit.getPluginManager().getPermission(val) != null) {
                        Bukkit.getPluginManager().removePermission(val);
                    }
                }
            } catch(Exception e) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to remove permission of field \"" + f.getName() + "\".", e);
            }
        }

        if(this.permissions.containsKey(module)) {
            this.permissions.get(module).remove(clazz);
        }
    }

    @Override
    public void unregisterAll(Module module) {
        if(this.permissions.containsKey(module)) {
            for(Class<?> clazz : new ArrayList<Class<?>>(this.permissions.get(module))) {
                this.unregister(module, clazz);
            }

            this.permissions.remove(module);
        }
    }

}
