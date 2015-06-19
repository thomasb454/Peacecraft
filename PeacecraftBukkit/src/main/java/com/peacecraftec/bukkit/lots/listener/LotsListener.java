package com.peacecraftec.bukkit.lots.listener;

import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.bukkit.lots.LotPermissions;
import com.peacecraftec.bukkit.lots.PeacecraftLots;
import com.peacecraftec.bukkit.lots.core.Lot;
import com.peacecraftec.bukkit.lots.core.Town;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class LotsListener implements Listener {

    private PeacecraftLots module;

    public LotsListener(PeacecraftLots plugin) {
        this.module = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Lot lot = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Town town = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            sendMessage(event.getPlayer(), "lots.cannot-do");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Lot lot = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Town town = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            sendMessage(event.getPlayer(), "lots.cannot-do");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Lot lot = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Town town = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            sendMessage(event.getPlayer(), "lots.cannot-do");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if(event.getPlayer() != null) {
            Lot lot = this.module.getLotManager().getLot(event.getBlock().getLocation());
            Town town = this.module.getLotManager().getTown(event.getBlock().getLocation());
            if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
                sendMessage(event.getPlayer(), "lots.cannot-do");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Lot from = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Lot to = this.module.getLotManager().getLot(event.getToBlock().getLocation());
        if((from != null && to == null) || (from == null && to != null) || (from != to && from != null && to != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Lot from = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Lot to = this.module.getLotManager().getLot(event.getBlock().getRelative(event.getDirection()).getLocation());
        Town toTown = this.module.getLotManager().getTown(event.getBlock().getRelative(event.getDirection()).getLocation());
        Town fromTown = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if((from != null && to == null) || (from == null && to != null) || (to == null && fromTown == null && toTown != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Location toLoc = event.getBlock().getRelative(event.getDirection()).getLocation();
        Lot from = this.module.getLotManager().getLot(event.getRetractLocation());
        Lot to = this.module.getLotManager().getLot(toLoc);
        Town fromTown = this.module.getLotManager().getTown(event.getRetractLocation());
        Town toTown = this.module.getLotManager().getTown(event.getBlock().getRelative(event.getDirection()).getLocation());
        if((from != null && to == null) || (from == null && to != null) || (from == null && toTown == null && fromTown != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Lot lot = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Town town = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if(lot != null || town != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Lot from = this.module.getLotManager().getLot(event.getSource().getLocation());
        Lot to = this.module.getLotManager().getLot(event.getBlock().getLocation());
        Town fromTown = this.module.getLotManager().getTown(event.getSource().getLocation());
        Town toTown = this.module.getLotManager().getTown(event.getBlock().getLocation());
        if(event.getBlock().getType() == Material.FIRE && (from != null || to != null || fromTown != null || toTown != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getEntity() != null && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN && !(event.getEntity() instanceof IronGolem)) {
            Lot lot = this.module.getLotManager().getLot(event.getEntity().getLocation());
            Town town = this.module.getLotManager().getTown(event.getEntity().getLocation());
            if(lot != null || town != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() != null) {
            Lot lot = this.module.getLotManager().getLot(event.getEntity().getLocation());
            Town town = this.module.getLotManager().getTown(event.getEntity().getLocation());
            if(event.getEntity() instanceof Player && (lot != null || town != null)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(event.getEntity() != null) {
            Lot lot = this.module.getLotManager().getLot(event.getEntity().getLocation());
            Town town = this.module.getLotManager().getTown(event.getEntity().getLocation());
            if(lot != null || town != null) {
                event.setYield(0);
                event.setCancelled(true);
            } else {
                for(Block block : event.blockList()) {
                    Lot l = this.module.getLotManager().getLot(block.getLocation());
                    Town t = this.module.getLotManager().getTown(block.getLocation());
                    if(l != null || t != null) {
                        event.setYield(0);
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(event.getTarget() instanceof Player) {
            Lot lot = this.module.getLotManager().getLot(event.getTarget().getLocation());
            Town town = this.module.getLotManager().getTown(event.getTarget().getLocation());
            if(lot != null || town != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        Lot lot = this.module.getLotManager().getLot(event.getEntity().getLocation());
        Town town = this.module.getLotManager().getTown(event.getEntity().getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if(event instanceof HangingBreakByEntityEvent) {
            Entity attacker = ((HangingBreakByEntityEvent) event).getRemover();
            if(attacker instanceof Player) {
                Player player = (Player) attacker;
                Lot lot = this.module.getLotManager().getLot(event.getEntity().getLocation());
                Town town = this.module.getLotManager().getTown(event.getEntity().getLocation());
                if(((lot != null && !lot.canBuild(player)) || (lot == null && town != null)) && !player.hasPermission(LotPermissions.BUILD_ANYWHERE)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            Lot lot = this.module.getLotManager().getLot(block.getLocation());
            if((event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) && ((Sign) event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase("[BuyLot]") && lot != null && lot.isForSale()) {
                Town town = lot.getTown();
                if(!event.getPlayer().hasPermission(town.getPermission())) {
                    sendMessage(event.getPlayer(), "lots.town-no-perms", lot.getId());
                    return;
                }

                int count = 0;
                for(Lot l : this.module.getLotManager().getPlayerLots(event.getPlayer().getName())) {
                    if(l.getTown().getName().equals(lot.getTown().getName())) {
                        count++;
                    }
                }

                int max = this.module.getConfig().getInteger("max-town-lots-per-player", 1);
                if(count > max && !event.getPlayer().hasPermission(LotPermissions.RESTRICTION_OVERRIDE)) {
                    sendMessage(event.getPlayer(), "lot.lot-cant-have-more", max);
                    return;
                }

                double money = VaultAPI.getEconomy().getBalance(event.getPlayer().getName());
                double price = lot.getPrice();
                if(money < price) {
                    sendMessage(event.getPlayer(), "lots.lot-not-enough-money");
                    return;
                }

                VaultAPI.getEconomy().withdrawPlayer(event.getPlayer().getName(), price);
                if(lot.getOwner() != null && !lot.getOwner().equals("")) {
                    VaultAPI.getEconomy().depositPlayer(lot.getOwner(), price);
                }

                lot.setForSale(false);
                lot.setPrice(0);
                lot.setOwner(event.getPlayer().getName());
                this.module.getLotManager().saveLot(lot);
                sendMessage(event.getPlayer(), "lots.lot-purchased", lot.getId(), VaultAPI.getEconomy().format(price) + " " + (lot.getPrice() <= 1 ? VaultAPI.getEconomy().currencyNameSingular() : VaultAPI.getEconomy().currencyNamePlural()));
                event.setCancelled(true);
                return;
            }

            Town town = this.module.getLotManager().getTown(block.getLocation());
            if(block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN && block.getType() != Material.ENDER_CHEST && block.getType() != Material.WORKBENCH && block.getType() != Material.ENCHANTMENT_TABLE && ((lot != null && !lot.canInteract(event.getPlayer())) || (lot == null && town != null && (event.getItem() == null || event.getItem().getType() == null || (event.getItem().getType() != Material.LEVER && event.getItem().getType() != Material.STONE_BUTTON && event.getItem().getType() != Material.WOOD_BUTTON)))) && !event.getPlayer().hasPermission(LotPermissions.DO_ANYTHING)) {
                sendMessage(event.getPlayer(), "lots.cannot-do");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof Hanging) {
            Lot lot = this.module.getLotManager().getLot(event.getRightClicked().getLocation());
            Town town = this.module.getLotManager().getTown(event.getRightClicked().getLocation());
            if(((lot != null && !lot.canInteract(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.DO_ANYTHING)) {
                sendMessage(event.getPlayer(), "lots.cannot-do");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Lot lot = this.module.getLotManager().getLot(block.getLocation());
        Town town = this.module.getLotManager().getTown(block.getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            sendMessage(event.getPlayer(), "lots.cannot-do");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Block block = event.getBlockClicked();
        Lot lot = this.module.getLotManager().getLot(block.getLocation());
        Town town = this.module.getLotManager().getTown(block.getLocation());
        if(((lot != null && !lot.canBuild(event.getPlayer())) || (lot == null && town != null)) && !event.getPlayer().hasPermission(LotPermissions.BUILD_ANYWHERE)) {
            sendMessage(event.getPlayer(), "lots.cannot-do");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Lot from = this.module.getLotManager().getLot(event.getFrom());
        Lot to = this.module.getLotManager().getLot(event.getTo());
        if(from == null && to != null) {
            sendMessage(event.getPlayer(), "lots.lot-entered-" + (to.getOwner() != null && !to.getOwner().equals("") ? "owned" : "unowned"), to.getId(), (to.getOwner() != null ? to.getOwner() : ""));
        } else if(from != null && to == null) {
            sendMessage(event.getPlayer(), "lots.lot-left", from.getId());
        }

        Town frmTown = this.module.getLotManager().getTown(event.getFrom());
        Town toTown = this.module.getLotManager().getTown(event.getTo());
        if(frmTown == null && toTown != null) {
            sendMessage(event.getPlayer(), "lots.town-entered", toTown.getName());
        } else if(frmTown != null && toTown == null) {
            sendMessage(event.getPlayer(), "lots.town-left", frmTown.getName());
        }
    }

}
