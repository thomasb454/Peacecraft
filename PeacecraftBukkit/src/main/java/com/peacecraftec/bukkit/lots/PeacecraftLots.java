package com.peacecraftec.bukkit.lots;

import com.peacecraftec.bukkit.internal.vault.VaultAPI;
import com.peacecraftec.bukkit.lots.command.LotCommands;
import com.peacecraftec.bukkit.lots.core.Lot;
import com.peacecraftec.bukkit.lots.core.LotManager;
import com.peacecraftec.bukkit.lots.listener.LotsListener;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.broadcastMessage;
import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class PeacecraftLots extends Module {

    private LotManager manager;
    private int rentTask;

    public PeacecraftLots(String name, ModuleManager manager) {
        super(name, manager);
    }

    @Override
    public void onEnable() {
        this.loadConfig();
        this.manager = new LotManager(this);
        this.getManager().getPermissionManager().register(this, LotPermissions.class);
        this.getManager().getCommandManager().register(this, new LotCommands(this));
        this.getManager().getEventManager().register(this, new LotsListener(this));
        this.startRentTask();
    }

    @Override
    public void onDisable() {
        this.stopRentTask();
        this.manager.cleanup();
    }

    @Override
    public void reload() {
        this.stopRentTask();
        this.loadConfig();
        this.manager.cleanup();
        this.manager = new LotManager(this);
        this.startRentTask();
    }

    public LotManager getLotManager() {
        return this.manager;
    }

    private void loadConfig() {
        this.getConfig().load();
        this.getConfig().applyDefault("max-town-lots-per-player", 1);
        this.getConfig().applyDefault("show-all-worlds-towns", true);
        this.getConfig().applyDefault("weeks-between-rent", 2);
        this.getConfig().applyDefault("next-rent", this.getWeeksAhead(getConfig().getInteger("weeks-between-rent", 2)));
        this.getConfig().save();
    }

    private void startRentTask() {
        if(this.rentTask != -1) {
            this.stopRentTask();
        }

        this.rentTask = this.getManager().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                long time = getConfig().getLong("next-rent");
                if(System.currentTimeMillis() >= time) {
                    getConfig().setValue("next-rent", getWeeksAhead(getConfig().getInteger("weeks-between-rent", 2)));
                    getConfig().save();
                    for(Lot lot : manager.getLots()) {
                        if(lot.getOwner() != null && !lot.getOwner().equals("") && lot.getRent() > 0) {
                            VaultAPI.getEconomy().withdrawPlayer(lot.getOwner().toLowerCase(), lot.getWorld().getName().toLowerCase(), lot.getRent());
                            Player player = Bukkit.getServer().getPlayerExact(lot.getOwner());
                            if(player != null) {
                                sendMessage(player, "lot.lot-rent-collected", VaultAPI.getEconomy().format(lot.getRent()), lot.getId());
                            }
                        }
                    }

                    broadcastMessage("lots.collected-all-rent");
                }
            }
        }, 20, 20);
    }

    private void stopRentTask() {
        this.getManager().getScheduler().cancelTask(this, this.rentTask);
        this.rentTask = -1;
    }

    private long getWeeksAhead(int weeks) {
        return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7 * weeks);
    }
}
