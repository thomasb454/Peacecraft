package com.peacecraftec.bukkit.internal.module.permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import com.peacecraftec.module.Module;
import com.peacecraftec.module.permission.Perm;
import com.peacecraftec.module.permission.PermissionContainer;
import com.peacecraftec.module.permission.PermissionManager;

public class BukkitPermissionManager implements PermissionManager {
	
	private Map<Module, List<Class<? extends PermissionContainer>>> permissions = new HashMap<Module, List<Class<? extends PermissionContainer>>>();
	
	@Override
	public void register(Module module, Class<? extends PermissionContainer> clazz) {
		for(Field f : clazz.getDeclaredFields()) {
			try {
				Perm p = f.getAnnotation(Perm.class);
				if(p != null) {
					String val = (String) f.get(null);
					if(Bukkit.getPluginManager().getPermission(val) != null) {
						return;
					}
					
					Permission perm = new Permission(val, p.desc(), org.bukkit.permissions.PermissionDefault.valueOf(p.def().name()));
					Bukkit.getPluginManager().addPermission(perm);
				}
			} catch (Exception e) {
				System.err.println("Failed to register permission of field \"" + f.getName() + "\"");
				e.printStackTrace();
			}
		}
		
		if(!this.permissions.containsKey(module)) {
			this.permissions.put(module, new ArrayList<Class<? extends PermissionContainer>>());
		}
		
		this.permissions.get(module).add(clazz);
	}

	@Override
	public void unregister(Module module, Class<? extends PermissionContainer> clazz) {
		for(Field f : clazz.getDeclaredFields()) {
			try {
				Perm p = f.getAnnotation(Perm.class);
				if(p != null) {
					String val = (String) f.get(null);
					if(Bukkit.getPluginManager().getPermission(val) != null) {
						Bukkit.getPluginManager().removePermission(val);
					}
				}
			} catch (Exception e) {
				System.err.println("Failed to remove permission of field \"" + f.getName() + "\"");
				e.printStackTrace();
			}
		}
		
		if(this.permissions.containsKey(module)) {
			this.permissions.get(module).remove(clazz);
		}
	}

	@Override
	public void unregisterAll(Module module) {
		if(this.permissions.containsKey(module)) {
			for(Class<? extends PermissionContainer> clazz : new ArrayList<Class<? extends PermissionContainer>>(this.permissions.get(module))) {
				this.unregister(module, clazz);
			}
			
			this.permissions.remove(module);
		}
	}
	
}
