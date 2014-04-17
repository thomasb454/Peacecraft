package com.peacecraftec.bukkit.extras.listener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.peacecraftec.bukkit.extras.PeacecraftExtras;

public class AutoCraftListener implements Listener {

	private static EnumSet<BlockFace> NSEW = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
	private static EnumSet<Material> FILLED_BUCKETS = EnumSet.of(Material.MILK_BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET);

	private PeacecraftExtras module;

	public AutoCraftListener(PeacecraftExtras module) {
		this.module = module;
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		if(event.getSource().getHolder() instanceof Dropper) {
			if(!sourceIsInitiator(event)) {
				return;
			}
			
			ItemStack item = this.tryCraft((Dropper) event.getSource().getHolder(), event.getItem(), event);
			if(item != null) {
				event.setItem(item);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockDispense(BlockDispenseEvent event) {
		if(event.getBlock().getType().equals(Material.DROPPER)) {
			ItemStack item = this.tryCraft((Dropper) event.getBlock().getState(), event.getItem(), event);
			if(item != null) {
				event.setItem(item);
			}
		}
	}
	
	private ItemStack tryCraft(final Dropper dropper, ItemStack item, Cancellable event) {
		ItemFrame frame = getAttachedFrame(dropper.getBlock());
		if(frame == null) {
			return null;
		}

		if(frame != null && frame.getItem() != null && !frame.getItem().getType().equals(Material.AIR)) {
			if(item.equals(getFrameItem(frame))) {
				return null;
			}

			ItemStack result = null;
			Recipe recipe = null;
			boolean crafted = false;
			for(Recipe r : Bukkit.getServer().getRecipesFor(getFrameItem(frame))) {
				if(r instanceof ShapedRecipe || r instanceof ShapelessRecipe) {
					ItemStack ingredients[] = getIngredients(r).toArray(new ItemStack[0]);
					crafted = true;
					for(ItemStack ingred : ingredients) {
						if(!dropper.getInventory().containsAtLeast(ingred, ingred.getAmount())) {
							crafted = false;
							break;
						}
					}
					
					if(crafted) {
						recipe = r;
						result = r.getResult().clone();
						break;
					}
				}
			}
			
			if(!crafted) {
				event.setCancelled(true);
			} else {
				final Recipe ref = recipe;
				this.module.getManager().getScheduler().runTaskLater(this.module, new Runnable() {
					public void run() {
						for(ItemStack ing : getIngredients(ref)) {
							dropper.getInventory().removeItem(ing);
							if(FILLED_BUCKETS.contains(ing.getType())) {
								dropper.getInventory().addItem(new ItemStack(Material.BUCKET));
							}
						}
					}
				}, 0);
				
				return result;
			}
		}
		
		return null;
	}
	
	private static boolean sourceIsInitiator(InventoryMoveItemEvent event) {
		InventoryHolder source = event.getSource().getHolder();
		InventoryHolder initiator = event.getInitiator().getHolder();
		if(source instanceof Dropper) {
			if(initiator instanceof Dropper) {
				Location sourceLoc = ((Dropper) source).getLocation();
				Location initiatorLoc = ((Dropper) source).getLocation();
				return sourceLoc.equals(initiatorLoc);
			}
		}
		
		return false;
	}

	private static ItemStack getFrameItem(ItemFrame frame) {
		ItemStack stack = frame.getItem();
		short max = stack.getType().getMaxDurability();
		if(max > 0 && stack.getDurability() != 0) {
			stack = stack.clone();
			stack.setDurability((short) 0);
		}

		return stack;
	}

	private static ItemFrame getAttachedFrame(Block block) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		chunks.add(block.getChunk());
		for(BlockFace side : NSEW) {
			Chunk c = block.getRelative(side).getChunk();
			if(!block.getChunk().equals(c)) {
				chunks.add(c);
			}
		}
		
		Location loc = new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
		for(Chunk c : chunks) {
			for(Entity entity : c.getEntities()) {
				if(entity.getType() == EntityType.ITEM_FRAME && entity.getLocation().distanceSquared(loc) == 0.31640625) {
					return (ItemFrame) entity;
				}
			}
		}
		
		return null;
	}
	
	private static List<ItemStack> getIngredients(Recipe recipe) {
		List<ItemStack> ingredients = new ArrayList<ItemStack>();
		if(recipe instanceof ShapedRecipe) {
			ShapedRecipe shaped = (ShapedRecipe) recipe;
			String[] shape = shaped.getShape();
			for(String row : shape) {
				for(int col = 0; col < row.length(); col++) {
					ItemStack stack = shaped.getIngredientMap().get(row.charAt(col));
					for(ItemStack ingred : ingredients) {
						int maxSize = ingred.getType().getMaxStackSize();
						if(ingred.isSimilar(stack) && ingred.getAmount() < maxSize) {
							int canAdd = maxSize - ingred.getAmount();
							int add = Math.min(canAdd, stack.getAmount());
							ingred.setAmount(ingred.getAmount() + add);
							int remaining = stack.getAmount() - add;
							if(remaining >= 1) {
								stack.setAmount(remaining);
							} else {
								stack = null;
								break;
							}
						}
					}
					
					if(stack != null && stack.getAmount() > 0) {
						ingredients.add(stack);
					}
				}
			}
		} else if(recipe instanceof ShapelessRecipe) {
			for(ItemStack item : ((ShapelessRecipe) recipe).getIngredientList()) {
				for(ItemStack ingred : ingredients) {
					int maxSize = ingred.getType().getMaxStackSize();
					if(ingred.isSimilar(item) && ingred.getAmount() < maxSize) {
						int canAdd = maxSize - ingred.getAmount();
						ingred.setAmount(ingred.getAmount() + Math.min(canAdd, item.getAmount()));
						int remaining = item.getAmount() - Math.min(canAdd, item.getAmount());
						if(remaining >= 1) {
							item.setAmount(remaining);
						} else {
							break;
						}
					}
				}
				
				if(item.getAmount() > 0) {
					ingredients.add(item);
				}
			}
		}
		
		return ingredients;
	}

}
