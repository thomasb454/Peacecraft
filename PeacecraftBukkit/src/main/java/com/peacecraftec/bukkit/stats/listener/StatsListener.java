package com.peacecraftec.bukkit.stats.listener;

import com.peacecraftec.bukkit.stats.PeacecraftStats;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class StatsListener implements Listener {

    private PeacecraftStats module;

    public StatsListener(PeacecraftStats module) {
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.module.getStatSystem().addPlayer(event.getPlayer().getName());
        if(!this.module.getStatSystem().contains("login.join_time", event.getPlayer().getName())) {
            this.module.getStatSystem().setStat("login.join_time", event.getPlayer().getName(), System.currentTimeMillis());
        }

        this.module.getStatSystem().setStat("login.last_login", event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        long lastLogin = this.module.getStatSystem().getLong("login.last_login", event.getPlayer().getName());
        this.module.getStatSystem().increment("login.playtime", event.getPlayer().getName(), System.currentTimeMillis() - lastLogin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        this.module.getStatSystem().setStat("status.levels", event.getPlayer().getName(), event.getNewLevel());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        this.module.getStatSystem().increment("blocks.placed", event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockBreakEvent event) {
        this.module.getStatSystem().increment("blocks.broken", event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        this.module.getStatSystem().increment("taming." + event.getEntity().getType().name().toLowerCase(), event.getOwner().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if(e instanceof PlayerDeathEvent) {
            PlayerDeathEvent event = (PlayerDeathEvent) e;
            String field = "unknown";
            EntityDamageEvent last = event.getEntity().getLastDamageCause();
            if(last != null) {
                DamageCause cause = last.getCause();
                switch(cause) {
                    case CUSTOM:
                        return;
                    case CONTACT:
                        field = "cactus";
                        break;
                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                        field = "explosion";
                        break;
                    case FIRE_TICK:
                        field = "fire";
                        break;
                    case MAGIC:
                        field = "potion";
                        break;
                    case WITHER:
                        field = "wither_effect";
                        break;
                    case ENTITY_ATTACK:
                        Entity attacker = ((EntityDamageByEntityEvent) last).getDamager();
                        if(attacker instanceof Projectile) {
                            Projectile proj = (Projectile) attacker;
                            ProjectileSource shooter = proj.getShooter();
                            if(shooter instanceof Entity) {
                                attacker = (Entity) shooter;
                            } else if(shooter instanceof BlockProjectileSource) {
                                String str = ((BlockProjectileSource) shooter).getBlock().getType().name().toLowerCase();
                                field = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
                                break;
                            }
                        }

                        if(attacker instanceof PigZombie) {
                            field = "zombie_pigman";
                        } else if(attacker instanceof MushroomCow) {
                            field = "mooshroom";
                        } else if(attacker instanceof Skeleton && ((Skeleton) attacker).getSkeletonType() == SkeletonType.WITHER) {
                            field = "wither_skeleton";
                        } else {
                            field = attacker.getType().name().toLowerCase();
                        }

                        break;
                    default:
                        field = cause.name().toLowerCase();
                        return;
                }

                this.module.getStatSystem().increment("deaths." + field, event.getEntity().getName());
            }
        } else if(e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager() instanceof Player) {
            Player player = (Player) ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager();
            String field = "";
            Entity killed = e.getEntity();
            if(killed instanceof PigZombie) {
                field = "zombie_pigman";
            } else if(killed instanceof MushroomCow) {
                field = "mooshroom";
            } else if(killed instanceof Skeleton && ((Skeleton) killed).getSkeletonType() == SkeletonType.WITHER) {
                field = "wither_skeleton";
            } else {
                field = killed.getType().name().toLowerCase();
            }

            this.module.getStatSystem().increment("kills." + field, player.getName());
        }
    }

}
