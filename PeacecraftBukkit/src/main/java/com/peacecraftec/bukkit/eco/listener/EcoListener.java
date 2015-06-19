package com.peacecraftec.bukkit.eco.listener;

import com.peacecraftec.bukkit.eco.EcoPermissions;
import com.peacecraftec.bukkit.eco.PeacecraftEco;
import com.peacecraftec.bukkit.eco.core.EcoPlayer;
import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.bukkit.stats.PeacecraftStats;
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
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;

import java.util.List;
import java.util.regex.Pattern;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class EcoListener implements Listener {

    private static final BlockFace UDNSEW[] = new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

    private static final byte NAME_LINE = 0;
    private static final byte QUANTITY_LINE = 1;
    private static final byte PRICE_LINE = 2;
    private static final byte ITEM_LINE = 3;

    private static final Pattern[] SHOP_SIGN_PATTERN = { Pattern.compile("^[\\w -.]*$"), Pattern.compile("^[1-9][0-9]*$"), Pattern.compile("(?i)^[\\d.bs(free) :]+$"), Pattern.compile("^[\\w #:-]+$") };

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
            Block block = event.getClickedBlock();
            if(block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                if(isValid(sign)) {
                    if(sign.getLine(NAME_LINE).equalsIgnoreCase(player.getName())) {
                        return;
                    }

                    if(!player.hasPermission(EcoPermissions.USE_CHEST_SHOP)) {
                        sendMessage(player, "economy.chestshop.cannot-use");
                        return;
                    }

                    EcoPlayer eplayer = this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(player.getName());
                    EcoPlayer owner = isAdminShop(sign) ? null : this.module.getEcoManager().getWorld(player.getWorld().getName()).getPlayer(sign.getLine(0));
                    if(owner == null && !isAdminShop(sign)) {
                        sendMessage(player, "economy.chestshop.unknown-sign-player");
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
                        quantity = Integer.parseInt(sign.getLine(QUANTITY_LINE));
                    } catch(NumberFormatException e) {
                        sendMessage(player, "economy.chestshop.invalid-quantity");
                        return;
                    }

                    if(quantity < 0) {
                        sendMessage(player, "economy.chestshop.invalid-quantity");
                        return;
                    }

                    double price = getPrice(sign.getLine(PRICE_LINE), event.getAction() == Action.RIGHT_CLICK_BLOCK ? "b" : "s");
                    if(price < 0) {
                        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            sendMessage(player, "economy.chestshop.cannot-buy");
                        } else {
                            sendMessage(player, "economy.chestshop.cannot-sell");
                        }

                        return;
                    }

                    String split[] = sign.getLine(ITEM_LINE).split(":");
                    Material mat = Material.matchMaterial(split[0]);
                    if(mat == null) {
                        sendMessage(player, "economy.chestshop.invalid-item");
                        return;
                    }

                    short data = 0;
                    if(split.length > 1) {
                        try {
                            data = Short.parseShort(split[1]);
                        } catch(NumberFormatException e) {
                            sendMessage(player, "economy.chestshop.invalid-core");
                            return;
                        }
                    }

                    ItemStack i = new ItemStack(mat, quantity, data);
                    Player powner = null;
                    if(owner != null) {
                        powner = Bukkit.getServer().getPlayerExact(owner.getName());
                    }

                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if(owner != null) {
                            if(!containsAtLeast(chest.getInventory(), i, i.getAmount())) {
                                sendMessage(player, "economy.chestshop.out-of-stock");
                                if(powner != null) {
                                    sendMessage(powner, "economy.chestshop.your-shop-stock", mat.name());
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
                                sendMessage(player, "economy.chestshop.no-space-inv");
                                return;
                            }
                        }

                        if(eplayer.getBalance() < price) {
                            sendMessage(player, "economy.chestshop.not-enough-money");
                            return;
                        }

                        player.getInventory().addItem(i);
                        player.updateInventory();
                        eplayer.removeBalance(price);
                        sendMessage(player, "economy.chestshop.bought", quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price));
                        if(owner != null) {
                            chest.getInventory().removeItem(i);
                            double taxed = price - (price * this.module.getConfig().getDouble("chest-shop-tax.buy", 10));
                            owner.addBalance(taxed);
                            if(powner != null) {
                                sendMessage(powner, "economy.chestshop.bought-from", player.getName(), quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price), taxed);
                            }
                        }
                    } else {
                        if(!containsAtLeast(player.getInventory(), i, i.getAmount())) {
                            sendMessage(player, "economy.chestshop.not-enough-item");
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
                                    sendMessage(player, "economy.chestshop.no-space-chest");
                                    return;
                                }
                            }
                        }

                        if(owner != null) {
                            if(owner.getBalance() < price) {
                                sendMessage(player, "economy.chestshop.owner-not-enough-money", owner.getName());
                                return;
                            }
                        }

                        player.getInventory().removeItem(i);
                        player.updateInventory();
                        double taxed = owner == null ? price : price - (price * this.module.getConfig().getDouble("chest-shop-tax.sell", 10));
                        eplayer.addBalance(taxed);
                        sendMessage(player, owner != null ? "economy.chestshop.sold" : "economy.chestshop.sold-no-tax", quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price), this.module.getEcoManager().format(taxed));
                        if(owner != null) {
                            chest.getInventory().addItem(i);
                            owner.removeBalance(price);
                            if(powner != null) {
                                sendMessage(powner, "economy.chestshop.sold-to", player.getName(), quantity, mat.name() + (quantity > 1 ? "s" : ""), this.module.getEcoManager().format(price));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(isValidPreparedSign(event.getLines())) {
            if(!event.getPlayer().hasPermission(EcoPermissions.MAKE_CHEST_SHOP)) {
                sendMessage(event.getPlayer(), "economy.chestshop.cannot-use");
                event.getBlock().breakNaturally();
                return;
            }

            Material mat = Material.matchMaterial(event.getLines()[ITEM_LINE].split(":")[0]);
            if(mat != null) {
                if(!event.getPlayer().hasPermission(EcoPermissions.MAKE_ADMIN_SHOP) || (event.getLine(NAME_LINE) != null && !event.getLine(NAME_LINE).replaceAll(" ", "").equalsIgnoreCase("AdminShop"))) {
                    event.setLine(NAME_LINE, event.getPlayer().getName());
                    Chest chest = null;
                    for(BlockFace face : UDNSEW) {
                        Block relative = event.getBlock().getRelative(face);
                        if(relative.getState() instanceof Chest) {
                            chest = (Chest) relative.getState();
                            break;
                        }
                    }

                    if(chest == null) {
                        sendMessage(event.getPlayer(), "economy.chestshop.chest-not-found");
                        event.getBlock().breakNaturally();
                        return;
                    }
                }

                sendMessage(event.getPlayer(), "economy.chestshop.shop-created");
            } else {
                sendMessage(event.getPlayer(), "economy.chestshop.invalid-material");
                event.getBlock().breakNaturally();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(this.module.getConfig().getBoolean("mob-money.enabled", true) && event.getSpawnReason() == SpawnReason.NATURAL) {
            event.getEntity().setMetadata("natural", new MetadataValueAdapter(Bukkit.getServer().getPluginManager().getPlugin(this.module.getManager().getImplementationName())) {
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
                if(killer != null) {
                    if(killer.getGameMode() == GameMode.SURVIVAL) {
                        double amt = this.module.getConfig().getDouble("mob-money.mobs." + ename);
                        EconomyResponse resp = VaultAPI.getEconomy().depositPlayer(killer.getName(), killer.getWorld().getName(), amt);
                        if(resp.transactionSuccess()) {
                            String name = ename.toLowerCase().replaceAll("_", " ");
                            boolean a = true;
                            char c = name.charAt(0);
                            if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                                a = false;
                            }

                            sendMessage(killer, a ? "economy.mobmoney.recieved-a" : "economy.mobmoney.recieved-an", VaultAPI.getEconomy().format(amt), name);
                        } else {
                            sendMessage(killer, "economy.mobmoney.failed");
                        }
                    }
                }
            }
        }
    }

    private static boolean containsAtLeast(Inventory inv, ItemStack item, int amount) {
        if(item == null) {
            return false;
        }

        if(amount <= 0) {
            return true;
        }

        for(ItemStack i : inv.getContents()) {
            if(isSimilar(item, i) && (amount -= i.getAmount()) <= 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSimilar(ItemStack stack, ItemStack other) {
        if(other == null) {
            return false;
        }

        if(other == stack) {
            return true;
        }

        return stack.getType() == other.getType() && stack.getDurability() == other.getDurability();
    }

    private static boolean isAdminShop(Sign sign) {
        return sign.getLine(NAME_LINE).replace(" ", "").equalsIgnoreCase("AdminShop");
    }

    private static boolean isValid(Sign sign) {
        return isValidPreparedSign(sign.getLines()) && (sign.getLine(PRICE_LINE).toUpperCase().contains("B") || sign.getLine(PRICE_LINE).toUpperCase().contains("S")) && !sign.getLine(PRICE_LINE).isEmpty();
    }

    private static boolean isValidPreparedSign(String lines[]) {
        for(int i = 0; i < 4; i++) {
            if(!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) {
                return false;
            }
        }

        return lines[PRICE_LINE].indexOf(':') == lines[PRICE_LINE].lastIndexOf(':');
    }

    private static double getPrice(String line, String character) {
        String[] split = line.replace(" ", "").toLowerCase().split(":");
        for(String part : split) {
            if(!part.contains(character)) {
                continue;
            }

            part = part.replace(character, "");
            if(part.equals("free")) {
                return 0;
            }

            try {
                double price = Double.parseDouble(part);
                if(price <= 0) {
                    return -1;
                } else {
                    return price;
                }
            } catch(NumberFormatException e) {
            }
        }

        return -1;
    }
}
