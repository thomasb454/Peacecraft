package com.peacecraftec.bukkit.protect.listener;

import com.peacecraftec.bukkit.protect.PeacecraftProtect;
import com.peacecraftec.bukkit.protect.ProtectPermissions;
import com.peacecraftec.bukkit.protect.core.Access;
import com.peacecraftec.bukkit.protect.core.Protection;
import com.peacecraftec.bukkit.protect.core.interact.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class ProtectListener implements Listener {
    private static final List<Material> PROTECTABLE = Arrays.asList(Material.LEVER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.CHEST, Material.TRAPPED_CHEST, Material.ANVIL, Material.HOPPER, Material.DISPENSER, Material.DROPPER, Material.BEACON, Material.BREWING_STAND, Material.FURNACE, Material.JUKEBOX, Material.NOTE_BLOCK, Material.CAKE_BLOCK, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF, Material.SIGN_POST, Material.WALL_SIGN);
    private PeacecraftProtect module;

    public ProtectListener(PeacecraftProtect module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Protection protection = this.module.getProtectManager().getProtection(event.getClickedBlock().getLocation());
            InteractAction action = this.module.getAction(event.getPlayer().getName());
            if(action != null) {
                if(!PROTECTABLE.contains(event.getClickedBlock().getType())) {
                    sendMessage(event.getPlayer(), "protect.not-protectable");
                } else if(protection != null) {
                    if(!protection.getOwner().equals(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission(ProtectPermissions.MODIFY_ALL)) {
                        sendMessage(event.getPlayer(), "protect.not-owner");
                    } else if(action instanceof UnprotectAction) {
                        this.module.getProtectManager().removeProtection(protection.getLocation());
                        sendMessage(event.getPlayer(), "protect.unprotected");
                    } else if(action instanceof AccessAddAction) {
                        for(String player : ((AccessAddAction) action).getPlayers()) {
                            UUID uuid = this.module.getManager().getUUID(player);
                            if(uuid == null) {
                                sendMessage(event.getPlayer(), "protect.unknown-player", player);
                                return;
                            }

                            this.module.getProtectManager().addAllowedPlayer(protection.getLocation(), uuid.toString());
                        }

                        StringBuilder build = new StringBuilder();
                        boolean first = true;
                        for(String player : ((AccessAddAction) action).getPlayers()) {
                            if(!first) {
                                build.append(", ");
                            }

                            first = false;
                            build.append(player);
                        }

                        sendMessage(event.getPlayer(), "protect.added-allowed-players", build.toString());
                    } else if(action instanceof AccessRemoveAction) {
                        for(String player : ((AccessRemoveAction) action).getPlayers()) {
                            UUID uuid = this.module.getManager().getUUID(player);
                            if(uuid == null) {
                                sendMessage(event.getPlayer(), "protect.unknown-player", player);
                                return;
                            }

                            this.module.getProtectManager().removeAllowedPlayer(protection.getLocation(), uuid.toString());
                        }

                        StringBuilder build = new StringBuilder();
                        boolean first = true;
                        for(String player : ((AccessRemoveAction) action).getPlayers()) {
                            if(!first) {
                                build.append(", ");
                            }

                            first = false;
                            build.append(player);
                        }

                        sendMessage(event.getPlayer(), "protect.removed-allowed-players", build.toString());
                    } else if(action instanceof AccessListAction) {
                        StringBuilder build = new StringBuilder();
                        boolean first = true;
                        for(String allowed : protection.getAllowedPlayers()) {
                            if(!first) {
                                build.append(", ");
                            }

                            first = false;
                            build.append(this.module.getManager().getUsername(UUID.fromString(allowed)));
                        }

                        sendMessage(event.getPlayer(), "protect.allowed-players", build.toString());
                    } else {
                        sendMessage(event.getPlayer(), "protect.protection-already-exists");
                    }
                } else {
                    if(action instanceof ProtectAction) {
                        this.module.getProtectManager().addProtection(new Protection(event.getClickedBlock().getLocation(), event.getPlayer().getUniqueId().toString(), new ArrayList<String>(), ((ProtectAction) action).getAccess()));
                        sendMessage(event.getPlayer(), "protect.protected");
                    } else {
                        sendMessage(event.getPlayer(), "protect.protection-doesnt-exist");
                    }
                }

                this.module.setAction(event.getPlayer().getName(), null);
                event.setCancelled(true);
            } else if(protection != null) {
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    sendMessage(event.getPlayer(), "protect.protected-by", this.module.getManager().getCasedUsername(this.module.getManager().getUsername(UUID.fromString(protection.getOwner()))));
                    if(protection.getAccess() == Access.PRIVATE && !protection.getAllowedPlayers().contains(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission(ProtectPermissions.ACCESS_ALL)) {
                        event.setCancelled(true);
                    }
                } else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if(!protection.getOwner().equals(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission(ProtectPermissions.MODIFY_ALL)) {
                        sendMessage(event.getPlayer(), "protect.protected-by", this.module.getManager().getCasedUsername(this.module.getManager().getUsername(UUID.fromString(protection.getOwner()))));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        InventoryHolder source = event.getSource().getHolder();
        if(source instanceof DoubleChest) {
            source = ((DoubleChest) source).getLeftSide();
        }

        InventoryHolder dest = event.getDestination().getHolder();
        if(dest instanceof DoubleChest) {
            dest = ((DoubleChest) dest).getLeftSide();
        }

        if(source instanceof BlockState) {
            Protection protection = this.module.getProtectManager().getProtection(((BlockState) source).getLocation());
            if(protection != null && protection.getAccess() == Access.PRIVATE) {
                event.setCancelled(true);
                return;
            }
        }

        if(dest instanceof BlockState) {
            Protection protection = this.module.getProtectManager().getProtection(((BlockState) dest).getLocation());
            if(protection != null && protection.getAccess() == Access.PRIVATE) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(PROTECTABLE.contains(event.getBlock().getType())) {
            Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getLocation());
            if(protection == null) {
                this.module.getProtectManager().addProtection(new Protection(event.getBlock().getLocation(), event.getPlayer().getUniqueId().toString(), new ArrayList<String>(), Access.PRIVATE));
                sendMessage(event.getPlayer(), "protect.protected");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.module.setAction(event.getPlayer().getName(), null);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.module.getProtectManager().removeProtection(event.getBlock().getLocation());
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        List<BlockState> blocks = event.getBlocks();
        for(BlockState block : blocks) {
            if(!PROTECTABLE.contains(block.getBlock().getType())) {
                continue;
            }

            Protection protection = this.module.getProtectManager().getProtection(block.getBlock().getLocation());
            if(protection != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getLocation());
        if(protection != null && protection.getAccess() == Access.PRIVATE && !protection.getAllowedPlayers().contains(event.getPlayer().getUniqueId().toString()) && !event.getPlayer().hasPermission(ProtectPermissions.ACCESS_ALL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block piston = event.getBlock();
        BlockState state = piston.getState();
        MaterialData data = state.getData();
        BlockFace direction = null;
        if(data instanceof PistonBaseMaterial) {
            direction = ((PistonBaseMaterial) data).getFacing();
        }

        if(direction == null) {
            return;
        }

        if(this.module.getProtectManager().getProtection(piston.getRelative(direction, 2).getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Block piston = event.getBlock();
        BlockState state = piston.getState();
        MaterialData data = state.getData();
        BlockFace direction = null;
        if(data instanceof PistonBaseMaterial) {
            direction = ((PistonBaseMaterial) data).getFacing();
            Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getRelative(direction).getLocation());
            if(protection != null) {
                event.setCancelled(true);
                return;
            }
        }

        if(direction == null) {
            return;
        }

        for(int i = 0; i < event.getLength() + 2; i++) {
            Block block = piston.getRelative(direction, i);
            if(block.getType() == Material.AIR) {
                break;
            }

            Protection protection = this.module.getProtectManager().getProtection(block.getLocation());
            if(protection != null) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getLocation());
        if(protection != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getLocation());
        if(protection != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Protection protection = this.module.getProtectManager().getProtection(event.getBlock().getLocation());
        if(protection != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        for(Block block : event.blockList()) {
            Protection protection = this.module.getProtectManager().getProtection(block.getLocation());
            if(protection != null) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
