package com.peacecraftec.bukkit.donation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.peacecraftec.bukkit.chat.PeacecraftChat;
import com.peacecraftec.bukkit.donation.listener.DonationListener;
import com.peacecraftec.bukkit.internal.hook.DonationPointsAPI;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.web.donation.DonationCallback;
import com.peacecraftec.web.donation.DonationSystem;
import com.peacecraftec.web.donation.DonationFactory;
import com.peacecraftec.bukkit.perms.PeacecraftPerms;

public class PeacecraftDonation extends Module implements DonationCallback {
	
	private DonationSystem storage;
	
	public PeacecraftDonation(ModuleManager manager) {
		super("Donation", manager);
	}

	@Override
	public void onEnable() {
		this.loadConfig();
		this.storage = DonationFactory.create(this.getManager(), this);
		this.getManager().getEventManager().register(this, new DonationListener());
	}
	
	@Override
	public void onDisable() {
		this.storage.cleanup();
		this.storage = null;
	}

	@Override
	public void reload() {
		this.storage.cleanup();
		this.storage = null;
		this.loadConfig();
		this.storage = DonationFactory.create(this.getManager(), this);
	}
	
	private void loadConfig() {
		this.getConfig().load();
		this.getConfig().applyDefault("points-per-dollar", 1000);
		this.getConfig().applyDefault("prefix", "&9[&4$&9]");
		this.getConfig().applyDefault("donor-permissions", Arrays.asList("permissions.go.here"));
		this.getConfig().save();
	}

	public DonationSystem getStorage() {
		return this.storage;
	}
	
	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"));
	}
	
	public Map<String, Boolean> getDonorPermissions() {
		Map<String, Boolean> ret = new HashMap<String, Boolean>();
		for(String perm : this.getConfig().getList("donor-permissions", String.class, new ArrayList<String>())) {
			boolean has = true;
			if(perm.startsWith("-")) {
				perm = perm.substring(1, perm.length());
				has = false;
			}
			
			ret.put(perm, has);
		}
		
		return ret;
	}

	@Override
	public void onDonation(String player, int dollars) {
		Player p = Bukkit.getServer().getPlayer(player);
		if(p != null && this.getManager().isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getManager().getModule("Chat");
			chat.loadDisplayName(p);
		}
		
		String name = null;
		if(p != null) {
			name = p.getDisplayName();
		} else if(this.getManager().isEnabled("Chat")) {
			PeacecraftChat chat = (PeacecraftChat) this.getManager().getModule("Chat");
			name = chat.getDisplayName(player, this.getManager().getDefaultWorld());
		} else {
			name = this.getManager().getCasedUsername(player);
		}
		
		if(p != null && this.getManager().isEnabled("Permissions")) {
			((PeacecraftPerms) this.getManager().getModule("Permissions")).refreshPermissions(p);
		}
		
		this.getManager().broadcastMessage("donation.donated", name, dollars);
		DonationPointsAPI.addPoints(player, dollars * this.getConfig().getInteger("points-per-dollar", 1000));
		BufferedWriter writer = null;
		try {
			File file = new File(this.getDirectory(), "donations.log");
			if(!file.exists()) {
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				
				file.createNewFile();
			}
			
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(player + " donated $" + dollars);
			writer.newLine();
		} catch(IOException e) {
			this.getLogger().severe("Failed to log donation by player \"" + player + "\" of $" + dollars + ".");
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
}
