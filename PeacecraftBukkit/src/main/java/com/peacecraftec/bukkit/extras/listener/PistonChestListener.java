package com.peacecraftec.bukkit.extras.listener;

import java.util.HashMap;
import java.util.List;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;

import com.peacecraftec.bukkit.extras.PeacecraftExtras;
import com.peacecraftec.bukkit.internal.hook.LWCAPI;

public class PistonChestListener implements Listener {

	private PeacecraftExtras module;

	public PistonChestListener(PeacecraftExtras module) {
		this.module = module;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if(event.getBlock().getType() == Material.PISTON_BASE || event.getBlock().getType() == Material.PISTON_STICKY_BASE) {
			PistonBaseMaterial p = (PistonBaseMaterial) event.getBlock().getState().getData();
			if(!p.isPowered() && (event.getBlock().isBlockPowered() || event.getBlock().isBlockIndirectlyPowered())) {
				Block container = event.getBlock().getRelative(p.getFacing(), 2);
				Inventory inv = this.getInventory(container);
				if(inv != null && LWCAPI.hasAccess(container)) {
					Block push = event.getBlock().getRelative(p.getFacing(), 1);
					ItemStack stack = this.convert(push.getType(), push.getState().getData());
					if(this.isValidBlock(stack.getType())) {
						stack.setData(push.getState().getData());
						HashMap<Integer, ItemStack> failed = inv.addItem(new ItemStack[] { stack });
						if(failed.isEmpty()) {
							push.setType(Material.AIR);
						} else {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	private ItemStack convert(Material mat, MaterialData data) {
		Material original = mat;
		int amount = 1;
		switch(mat) {
			case CAULDRON:
				mat = Material.CAULDRON_ITEM;
				break;
			case SUGAR_CANE_BLOCK:
				mat = Material.SUGAR_CANE;
				break;
			case BREWING_STAND:
				mat = Material.BREWING_STAND_ITEM;
				break;
			case FLOWER_POT:
				mat = Material.FLOWER_POT_ITEM;
				break;
			case POTATO:
				mat = Material.POTATO_ITEM;
				break;
			case CROPS:
				if(((Crops) data).getState() == CropState.RIPE) {
					mat = Material.WHEAT;
				} else {
					mat = Material.SEEDS;
				}
				
				break;
			case CARROT:
				mat = Material.CARROT_ITEM;
				break;
			case SIGN_POST:
			case WALL_SIGN:
				mat = Material.SIGN;
				break;
			case CAKE_BLOCK:
				mat = Material.CAKE;
				break;
			case DIODE_BLOCK_OFF:
			case DIODE_BLOCK_ON:
				mat = Material.DIODE;
				break;
			case BURNING_FURNACE:
				mat = Material.FURNACE;
				break;
			case REDSTONE_LAMP_ON:
				mat = Material.REDSTONE_LAMP_OFF;
				break;
			case TRIPWIRE:
				mat = Material.STRING;
				break;
			case REDSTONE_COMPARATOR_OFF:
			case REDSTONE_COMPARATOR_ON:
				mat = Material.REDSTONE_COMPARATOR;
				break;
			default:
				break;
		}
		
		ItemStack ret = new ItemStack(mat, amount);
		if(mat == original) {
			ret.setData(data);
		}
		
		return ret;
	}

	private boolean isValidBlock(Material mat) {
		return mat != Material.AIR && mat != Material.HOPPER && mat != Material.DISPENSER && mat != Material.FURNACE && mat != Material.DROPPER && mat != Material.CHEST && mat != Material.TRAPPED_CHEST && mat != Material.BREWING_STAND && mat != Material.REDSTONE_TORCH_OFF && mat != Material.REDSTONE_TORCH_ON && mat != Material.COMMAND && mat != Material.IRON_DOOR_BLOCK && mat != Material.WOOD_DOOR && mat != Material.BED_BLOCK && mat != Material.WATER && mat != Material.LAVA && mat != Material.STATIONARY_LAVA && mat != Material.STATIONARY_WATER && mat != Material.PISTON_EXTENSION && mat != Material.ENDER_PORTAL_FRAME && mat != Material.ENDER_PORTAL && mat != Material.PORTAL && mat != Material.MONSTER_EGGS && mat != Material.MELON_STEM && mat != Material.PUMPKIN_STEM && mat != Material.SKULL && mat != Material.DOUBLE_PLANT && mat != Material.BEDROCK && this.isAllowed(mat);
	}

	private Inventory getInventory(Block block) {
		if(block.getType() == Material.CHEST) {
			return ((Chest) block.getState()).getInventory();
		} else if(block.getType() == Material.DISPENSER) {
			return ((Dispenser) block.getState()).getInventory();
		} else if(block.getType() == Material.TRAPPED_CHEST) {
			return ((Chest) block.getState()).getInventory();
		} else if(block.getType() == Material.FURNACE) {
			return ((Furnace) block.getState()).getInventory();
		} else if(block.getType() == Material.DROPPER) {
			return ((Dropper) block.getState()).getInventory();
		} else if(block.getType() == Material.HOPPER) {
			return ((Hopper) block.getState()).getInventory();
		}

		return null;
	}

	private boolean isAllowed(Material mat) {
		List<String> disabled = this.module.getConfig().getList("pistonchest.disabled", String.class);
		for(String dis : disabled) {
			Material m = Material.matchMaterial(dis);
			if(m != null && mat.equals(m)) {
				return false;
			}
		}

		return true;
	}

}
