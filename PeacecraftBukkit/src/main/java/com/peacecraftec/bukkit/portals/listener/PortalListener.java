package com.peacecraftec.bukkit.portals.listener;

import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import java.lang.reflect.Field;
import java.util.logging.Level;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class PortalListener implements Listener {
    private static final String CRAFTBUKKIT_PACKAGE;

    static {
        String p = null;
        for(Package pkg : Package.getPackages()) {
            String name = pkg.getName();
            if(name.startsWith("org.bukkit.craftbukkit.v")) {
                int index = "org.bukkit.craftbukkit.v".length();
                String after = name.substring(index);
                p = name.substring(0, (after.contains(".") ? after.indexOf(".") : after.length()) + index);
                break;
            }
        }

        CRAFTBUKKIT_PACKAGE = p;
    }

    private PeacecraftPortals module;

    public PortalListener(PeacecraftPortals module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo() != null) {
            String portal = this.module.getPortalManager().getPortal(event.getTo());
            if(portal != null) {
                String destPortal = this.module.getPortalManager().getDestPortal(portal);
                if(destPortal != null) {
                    if(!this.module.getPortalManager().isPortal(destPortal)) {
                        sendMessage(event.getPlayer(), "portals.dest-doesnt-exist");
                        return;
                    }

                    Location dest = this.module.getPortalManager().getPortalPoint(destPortal);
                    event.getPlayer().teleport(dest);
                }
            } else if((event.getTo().getBlock().getType() == Material.PORTAL || event.getTo().getBlock().getRelative(BlockFace.UP).getType() == Material.PORTAL) && event.getFrom().getBlock().getType() != Material.PORTAL && event.getFrom().getBlock().getRelative(BlockFace.UP).getType() != Material.PORTAL) {
                World world = event.getTo().getWorld();
                World to = null;
                if(world.getName().endsWith("_nether")) {
                    to = Bukkit.getServer().getWorld(world.getName().substring(0, world.getName().length() - "_nether".length()));
                } else if(world.getName().endsWith("_the_end")) {
                    to = Bukkit.getServer().getWorld(world.getName().substring(0, world.getName().length() - "_the_end".length()) + "_nether");
                } else {
                    to = Bukkit.getServer().getWorld(world.getName() + "_nether");
                }

                if(to != null) {
                    Location loc = event.getTo().clone();
                    loc.setWorld(to);
                    if(to.getName().endsWith("_nether")) {
                        loc.multiply(1 / 8.0);
                    } else {
                        loc.multiply(1 * 8.0);
                    }

                    event.getPlayer().teleport(findOrCreatePortal(loc));
                }
            } else if(event.getTo().getBlock().getType() == Material.ENDER_PORTAL || event.getTo().getBlock().getRelative(BlockFace.UP).getType() == Material.ENDER_PORTAL) {
                World world = event.getTo().getWorld();
                World to = null;
                if(world.getName().endsWith("_the_end")) {
                    to = Bukkit.getServer().getWorld(world.getName().substring(0, world.getName().length() - "_the_end".length()));
                } else if(world.getName().endsWith("_nether")) {
                    to = Bukkit.getServer().getWorld(world.getName().substring(0, world.getName().length() - "_nether".length()) + "_the_end");
                } else {
                    to = Bukkit.getServer().getWorld(world.getName() + "_the_end");
                }

                if(to != null) {
                    event.getPlayer().teleport(to.getSpawnLocation());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        String portal = this.module.getPortalManager().getPortal(event.getFrom());
        if(portal != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if(event.getEntity() instanceof Player) {
            return;
        }

        String portal = this.module.getPortalManager().getPortal(event.getFrom());
        if(portal != null) {
            event.setCancelled(true);
            String destPortal = this.module.getPortalManager().getDestPortal(portal);
            if(destPortal != null) {
                if(!this.module.getPortalManager().isPortal(destPortal)) {
                    return;
                }

                Location dest = this.module.getPortalManager().getPortalPoint(destPortal);
                event.getEntity().teleport(dest);
            }
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if(event.getChangedType() == Material.PORTAL || event.getBlock().getType() == Material.PORTAL) {
            String portal = this.module.getPortalManager().getPortal(event.getBlock().getLocation());
            if(portal != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if(event.getBlock().getType() == Material.WATER) {
            String portal = this.module.getPortalManager().getPortal(event.getBlock().getLocation());
            if(portal != null) {
                event.setCancelled(true);
            }
        }
    }

    private static Location findOrCreatePortal(Location loc) {
        try {
            Field f = Class.forName(CRAFTBUKKIT_PACKAGE + ".CraftTravelAgent").getDeclaredField("DEFAULT");
            return ((TravelAgent) f.get(null)).findOrCreate(loc);
        } catch(Exception e) {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "[PeacecraftBukkit] Failed to find or create nether portal.", e);
            return loc;
        }
    }

}
