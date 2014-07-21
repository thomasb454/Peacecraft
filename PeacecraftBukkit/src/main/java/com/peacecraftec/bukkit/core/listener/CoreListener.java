package com.peacecraftec.bukkit.core.listener;

import com.peacecraftec.bukkit.core.CorePermissions;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.internal.selection.Selection;
import com.peacecraftec.bukkit.internal.selection.Selector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CoreListener implements Listener {

	private PeacecraftCore module;
	
	public CoreListener(PeacecraftCore module) {
		this.module = module;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.module.getManager().setUserPair(event.getPlayer().getUniqueId(), event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Selector.get().clearPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String msg = event.getMessage().toLowerCase();
		if((msg.startsWith("/plugins ") || msg.equals("/plugins") || msg.startsWith("/pl ") || msg.equals("/pl")) && !event.getPlayer().hasPermission(CorePermissions.PLUGINS)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getPlayer().hasPermission(CorePermissions.WAND)) {
			ItemStack item = event.getItem();
			if(item != null && item.getType() == Material.WOOD_AXE) {
				ItemMeta meta = item.getItemMeta();
				if(meta.getDisplayName().equals(ChatColor.GOLD + "Peacecraft Wand")) {
					if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
						Selection selection = Selector.get().getSelection(event.getPlayer());
						Selector.get().setSelection(event.getPlayer(), new Selection(event.getClickedBlock().getLocation(), selection != null ? selection.getSecondPoint() : null));
						BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("first-pos-set", event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
						event.setCancelled(true);
					} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						Selection selection = Selector.get().getSelection(event.getPlayer());
						Selector.get().setSelection(event.getPlayer(), new Selection(selection != null ? selection.getFirstPoint() : null, event.getClickedBlock().getLocation()));
						BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager()).sendMessage("second-pos-set", event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
}
