package com.peacecraftec.bukkit.eco.listener;

import java.util.List;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;

import com.peacecraftec.bukkit.eco.EcoPermissions;
import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.bukkit.eco.core.EcoPlayer;
import com.peacecraftec.bukkit.eco.util.ShopAction;
import com.peacecraftec.bukkit.eco.util.SignShopUtil;
import com.peacecraftec.bukkit.internal.hook.VaultAPI;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
import com.peacecraftec.module.cmd.sender.PlayerSender;

public class EcoListener implements Listener {

	private static final BlockFace UDNSEW[] = new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
	
	private PeacecraftEco module;
	
	public EcoListener(PeacecraftEco module) {
		this.module = module;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.module.getEcoManager().getWorld(event.getPlayer().getWorld().getName()).addIfMissing(event.getPlayer().getName());
		if(this.module.getManager().isEnabled("Stats") && this.module.getEcoManager().getStatsWorlds().contains(event.getPlayer().getWorld().getName())) {
			PeacecraftStats stats = (PeacecraftStats) this.module.getManager().getModule("Stats");
			stats.getStatSystem().setStat("money." + event.getPlayer().getWorld().getName().toLowerCase(), event.getPlayer().getName(), this.module.getEcoManager().getWorld(event.getPlayer().getWorld().getName()).getPlayer(event.getPlayer().getName()).getBalance());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(this.module.getManager().isEnabled("Stats") && this.module.getEcoManager().getStatsWorlds().contains(event.getPlayer().getWorld().getName())) {
			PeacecraftStats stats = (PeacecraftStats) this.module.getManager().getModule("Stats");
			stats.getStatSystem().setStat("money." + event.getPlayer().getWorld().getName().toLowerCase(), event.getPlayer().getName(), this.module.getEcoManager().getWorld(event.getPlayer().getWorld().getName()).getPlayer(event.getPlayer().getName()).getBalance());
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		this.module.getEcoManager().getWorld(event.getPlayer().getWorld().getName()).addIfMissing(event.getPlayer().getName());
		if(this.module.getManager().isEnabled("Stats")) {
			PeacecraftStats stats = (PeacecraftStats) this.module.getManager().getModule("Stats");
			if(this.module.getEcoManager().getStatsWorlds().contains(event.getPlayer().getWorld().getName())) {
				stats.getStatSystem().setStat("money." + event.getPlayer().getWorld().getName().toLowerCase(), event.getPlayer().getName(), this.module.getEcoManager().getWorld(event.getPlayer().getWorld().getName()).getPlayer(event.getPlayer().getName()).getBalance());
			}
			
			if(this.module.getEcoManager().getStatsWorlds().contains(event.getFrom().getName())) {
				stats.getStatSystem().setStat("money." + event.getFrom().getName().toLowerCase(), event.getPlayer().getName(), this.module.getEcoManager().getWorld(event.getFrom().getName()).getPlayer(event.getPlayer().getName()).getBalance());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			PlayerSender playerSender = (PlayerSender) BukkitCommandSender.wrap(player, this.module.getManager().getLanguageManager());
			Block block = event.getClickedBlock();
			if(block.getState() instanceof Sign) {
				Sign sign = (Sign) block.getState();
				if(SignShopUtil.isValid(sign)) {
					if(sign.getLine(SignShopUtil.NAME_LINE).equalsIgnoreCase(player.getName())) {
						return;
					}
					
					if(!player.hasPermission(EcoPermissions.USE_CHEST_SHOP)) {
						playerSender.sendMessage("economy.chestshop.cannot-use");
						return;
					}
					
					EcoPlayer eplayer = this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(player.getName());
					ShopAction action = event.getAction() == Action.RIGHT_CLICK_BLOCK ? ShopAction.BUY : ShopAction.SELL;
					EcoPlayer owner = SignShopUtil.isAdminShop(sign) ? null : this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(sign.getLine(0));
					if(owner == null && !SignShopUtil.isAdminShop(sign)) {
						playerSender.sendMessage("economy.chestshop.unknown-sign-player");
						return;
					}
					
					Chest chest = null;
					if(owner != null) {
						for(BlockFace face : UDNSEW) {
							Block relative = block.getRelative(face);
							if(relative.getState() instanceof Chest) {
								chest = (Chest) relative.getState();
								break;
							}
						}
						
						if(chest == null) {
							return;
						}
					}
					
					int quantity = 0;
					try {
						quantity = Integer.parseInt(sign.getLine(SignShopUtil.QUANTITY_LINE));
					} catch(NumberFormatException e) {
						playerSender.sendMessage("economy.chestshop.invalid-quantity");
						return;
					}
					
					if(quantity < 0) {
						playerSender.sendMessage("economy.chestshop.invalid-quantity");
						return;
					}
					
					double price = action.getPrice(sign.getLine(SignShopUtil.PRICE_LINE));
					if(price < 0) {
						if(action == ShopAction.BUY) {
							playerSender.sendMessage("economy.chestshop.cannot-buy");
						} else {
							playerSender.sendMessage("economy.chestshop.cannot-sell");
						}
						
						return;
					}
					
					String split[] = sign.getLine(SignShopUtil.ITEM_LINE).split(":");
					Material mat = Material.matchMaterial(split[0]);
					if(mat == null) {
						playerSender.sendMessage("economy.chestshop.invalid-item");
						return;
					}
					
					short data = 0;
					if(split.length > 1) {
						try {
							data = Short.parseShort(split[1]);
						} catch(NumberFormatException e) {
							playerSender.sendMessage("economy.chestshop.invalid-data");
							return;
						}
					}
					
					ItemStack i = new ItemStack(mat, quantity, data);
					Player powner = null;
					PlayerSender pownerSender = null;
					if(owner != null) {
						pownerSender = this.module.getManager().getPlayerSender(owner.getName());
						powner = (Player) BukkitCommandSender.unwrap(pownerSender);
					}
					
					if(action == ShopAction.BUY) {
						if(owner != null) {
							if(!this.containsAtLeast(chest.getInventory(), i, i.getAmount())) {
								playerSender.sendMessage("economy.chestshop.out-of-stock");
								if(powner != null) {
									pownerSender.sendMessage("economy.chestshop.your-shop-stock", mat.name());
								}
								
								return;
							}
						}
						
						if(player.getInventory().firstEmpty() == -1) {
							int remaining = 0;
							for(ItemStack item : player.getInventory().getContents()) {
								if(item.getType() == mat && item.getDurability() == data) {
									int rem = mat.getMaxStackSize() - item.getAmount();
									if(rem > 0) {
										remaining += rem;
									}
								}
							}
							
							if(remaining < quantity) {
								playerSender.sendMessage("economy.chestshop.no-space-inv");
								return;
							}
						}
						
						if(eplayer.getBalance() < price) {
							playerSender.sendMessage("economy.chestshop.not-enough-money");
							return;
						}
						
						player.getInventory().addItem(i);
						player.updateInventory();
						eplayer.removeBalance(price);
						playerSender.sendMessage("economy.chestshop.bought", quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price));
						if(owner != null) {
							chest.getInventory().removeItem(i);
							double taxed = price - (price * this.module.getConfig().getDouble("chest-shop-tax.buy", 10));
							owner.addBalance(taxed);
							if(powner != null) {
								pownerSender.sendMessage("economy.chestshop.bought-from", player.getName(), quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price), taxed);
							}
						}
					} else {
						if(!this.containsAtLeast(player.getInventory(), i, i.getAmount())) {
							playerSender.sendMessage("economy.chestshop.not-enough-item");
							return;
						}
						
						if(owner != null) {
							if(chest.getInventory().firstEmpty() == -1) {
								int remaining = 0;
								for(ItemStack item : chest.getInventory().getContents()) {
									if(item.getType() == mat && item.getDurability() == data) {
										int rem = mat.getMaxStackSize() - item.getAmount();
										if(rem > 0) {
											remaining += rem;
										}
									}
								}
								
								if(remaining < quantity) {
									playerSender.sendMessage("economy.chestshop.no-space-chest");
									return;
								}
							}
						}
						
						if(owner != null) {
							if(owner.getBalance() < price) {
								playerSender.sendMessage("economy.chestshop.owner-not-enough-money", owner.getName());
								return;
							}
						}
						
						player.getInventory().removeItem(i);
						player.updateInventory();
						double taxed = owner == null ? price : price - (price * this.module.getConfig().getDouble("chest-shop-tax.sell", 10));
						eplayer.addBalance(taxed);
						playerSender.sendMessage(owner != null ? "economy.chestshop.sold" : "economy.chestshop.sold-no-tax", quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price), this.module.getEcoManager().format(taxed));
						if(owner != null) {
							chest.getInventory().addItem(i);
							owner.removeBalance(price);
							if(powner != null) {
								pownerSender.sendMessage("economy.chestshop.sold-to", player.getName(), quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		PlayerSender playerSender = (PlayerSender) BukkitCommandSender.wrap(event.getPlayer(), this.module.getManager().getLanguageManager());
		if(SignShopUtil.isValidPreparedSign(event.getLines())) {
			if(!event.getPlayer().hasPermission(EcoPermissions.MAKE_CHEST_SHOP)) {
				playerSender.sendMessage("economy.chestshop.cannot-use");
				event.getBlock().breakNaturally();
				return;
			}
			
			Material mat = Material.matchMaterial(event.getLines()[SignShopUtil.ITEM_LINE].split(":")[0]);
			if(mat != null) {
				if(!event.getPlayer().hasPermission(EcoPermissions.MAKE_ADMIN_SHOP) || (event.getLine(SignShopUtil.NAME_LINE) != null && !event.getLine(SignShopUtil.NAME_LINE).replaceAll(" ", "").equalsIgnoreCase("AdminShop"))) {
					event.setLine(SignShopUtil.NAME_LINE, event.getPlayer().getName());
					Chest chest = null;
					for(BlockFace face : UDNSEW) {
						Block relative = event.getBlock().getRelative(face);
						if(relative.getState() instanceof Chest) {
							chest = (Chest) relative.getState();
							break;
						}
					}
						
					if(chest == null) {
						playerSender.sendMessage("economy.chestshop.chest-not-found");
						event.getBlock().breakNaturally();
						return;
					}
				}
				
				playerSender.sendMessage("economy.chestshop.shop-created");
			} else {
				playerSender.sendMessage("economy.chestshop.invalid-material");
				event.getBlock().breakNaturally();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(this.module.getConfig().getBoolean("mob-money.enabled", true) && event.getSpawnReason() == SpawnReason.NATURAL) {
			event.getEntity().setMetadata("natural", new MetadataValueAdapter(Bukkit.getServer().getPluginManager().getPlugin("Peacecraft")) {
				@Override
				public void invalidate() {
				}

				@Override
				public Object value() {
					return true;
				}
			});
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		String ename = event.getEntity().getType().name();
		if(event.getEntity() instanceof Skeleton && ((Skeleton) event.getEntity()).getSkeletonType() == SkeletonType.WITHER) {
			ename = "WITHER_SKELETON";
		}
		
		if(this.module.getConfig().getBoolean("mob-money.enabled", true) && this.module.getConfig().contains("mob-money.mobs." + ename)) {
			List<MetadataValue> vals = event.getEntity().getMetadata("natural");
			if(vals.size() > 0 && vals.get(0).asBoolean()) {
				Player killer = event.getEntity().getKiller();
				PlayerSender killerSender = (PlayerSender) BukkitCommandSender.wrap(killer, this.module.getManager().getLanguageManager());
				if(killer != null && killer.getGameMode() == GameMode.SURVIVAL) {
					double amt = this.module.getConfig().getDouble("mob-money.mobs." + ename);
					EconomyResponse resp = VaultAPI.getEconomy().depositPlayer(killer.getName(), killer.getWorld().getName(), amt);
					if(resp.transactionSuccess()) {
						String name = ename.toLowerCase().replaceAll("_", " ");
						boolean a = true;
						char c = name.charAt(0);
						if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
							a = false;
						}
						
						killerSender.sendMessage(a ? "economy.mobmoney.recieved-a" : "economy.mobmoney.recieved-an", VaultAPI.getEconomy().format(amt), name);
					} else {
						killerSender.sendMessage("economy.mobmoney.failed");
					}
				}
			}
		}
	}
	
	private boolean containsAtLeast(Inventory inv, ItemStack item, int amount) {
		if(item == null) {
			return false;
		}

		if(amount <= 0) {
			return true;
		}

		for(ItemStack i : inv.getContents()) {
			if(this.isSimilar(item, i) && (amount -= i.getAmount()) <= 0) {
				return true;
			}
		}
		
		return false;
	}

    private boolean isSimilar(ItemStack stack, ItemStack other) {
        if (other == null) {
            return false;
        }
        
        if (other == stack) {
            return true;
        }
        
        return stack.getType() == other.getType() && stack.getDurability() == other.getDurability();
    }
	
}
