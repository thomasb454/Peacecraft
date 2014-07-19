package com.peacecraftec.bukkit.chat;

import com.peacecraftec.bukkit.perms.PeacecraftPerms;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.*;

public class WebchatSender implements Player {

	private PeacecraftChat module;
	private UUID uuid;
	private String name;
	
	public WebchatSender(PeacecraftChat module, UUID uuid, String name) {
		this.module = module;
		this.name = name;
		this.uuid = uuid;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int arg1) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String arg1, boolean arg2) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String arg1, boolean arg2, int arg3) {
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override
	public boolean hasPermission(String perm) {
		if(this.module.getManager().isEnabled("Permissions")) {
			return ((PeacecraftPerms) this.module.getManager().getModule("Permissions")).hasPermission(this.name, perm);
		}

		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return this.hasPermission(perm.getName());
	}

	@Override
	public boolean isPermissionSet(String perm) {
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return this.isPermissionSet(perm.getName());
	}

	@Override
	public void recalculatePermissions() {
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
	}

	@Override
	public boolean isOp() {
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(this.name);
		return player != null ? player.isOp() : false;
	}

	@Override
	public void setOp(boolean op) {
		OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(this.name);
		if(player != null) {
			player.setOp(op);
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public void sendMessage(String msg) {
		this.sendMessage(new String[] { msg });
	}

	@Override
	public void sendMessage(String[] msgs) {
		for(String msg : msgs) {
			this.module.sendWebchatMessage(this.getName(), msg);
		}
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public Inventory getEnderChest() {
		return null;
	}

	@Override
	public int getExpToLevel() {
		return 0;
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public PlayerInventory getInventory() {
		return null;
	}

	@Override
	public ItemStack getItemInHand() {
		return null;
	}

	@Override
	public ItemStack getItemOnCursor() {
		return null;
	}

	@Override
	public InventoryView getOpenInventory() {
		return null;
	}

	@Override
	public int getSleepTicks() {
		return 0;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean isSleeping() {
		return false;
	}

	@Override
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		return null;
	}

	@Override
	public InventoryView openInventory(Inventory arg0) {
		return null;
	}

	@Override
	public void openInventory(InventoryView arg0) {
	}

	@Override
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		return null;
	}

	@Override
	public void setGameMode(GameMode arg0) {
	}

	@Override
	public void setItemInHand(ItemStack arg0) {
	}

	@Override
	public void setItemOnCursor(ItemStack arg0) {
	}

	@Override
	public boolean setWindowProperty(Property arg0, int arg1) {
		return false;
	}

	@Override
	public int _INVALID_getLastDamage() {
		return 0;
	}

	@Override
	public void _INVALID_setLastDamage(int arg0) {
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0) {
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
		return false;
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> arg0) {
		return false;
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		return null;
	}

	@Override
	public boolean getCanPickupItems() {
		return false;
	}

	@Override
	public String getCustomName() {
		return null;
	}

	@Override
	public EntityEquipment getEquipment() {
		return null;
	}

	@Override
	public double getEyeHeight() {
		return 0;
	}

	@Override
	public double getEyeHeight(boolean arg0) {
		return 0;
	}

	@Override
	public Location getEyeLocation() {
		return null;
	}

	@Override
	public Player getKiller() {
		return null;
	}

	@Override
	public double getLastDamage() {
		return 0;
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) {
		return null;
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return null;
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
		return null;
	}

	@Override
	public int getMaximumAir() {
		return 0;
	}

	@Override
	public int getMaximumNoDamageTicks() {
		return 0;
	}

	@Override
	public int getNoDamageTicks() {
		return 0;
	}

	@Override
	public int getRemainingAir() {
		return 0;
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return false;
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
		return null;
	}

	@Override
	public boolean hasLineOfSight(Entity arg0) {
		return false;
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType arg0) {
		return false;
	}

	@Override
	public boolean isCustomNameVisible() {
		return false;
	}

	@Override
	public boolean isLeashed() {
		return false;
	}

	@Override
	public void removePotionEffect(PotionEffectType arg0) {
	}

	@Override
	public void setCanPickupItems(boolean arg0) {
	}

	@Override
	public void setCustomName(String arg0) {
	}

	@Override
	public void setCustomNameVisible(boolean arg0) {
	}

	@Override
	public void setLastDamage(double arg0) {
	}

	@Override
	public boolean setLeashHolder(Entity arg0) {
		return false;
	}

	@Override
	public void setMaximumAir(int arg0) {
	}

	@Override
	public void setMaximumNoDamageTicks(int arg0) {
	}

	@Override
	public void setNoDamageTicks(int arg0) {
	}

	@Override
	public void setRemainingAir(int arg0) {
	}

	@Override
	public void setRemoveWhenFarAway(boolean arg0) {
	}

	@Override
	public Arrow shootArrow() {
		return null;
	}

	@Override
	public Egg throwEgg() {
		return null;
	}

	@Override
	public Snowball throwSnowball() {
		return null;
	}

	@Override
	public boolean eject() {
		return false;
	}

	@Override
	public int getEntityId() {
		return 0;
	}

	@Override
	public float getFallDistance() {
		return 0;
	}

	@Override
	public int getFireTicks() {
		return 0;
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return null;
	}

	@Override
	public Location getLocation() {
		return this.getWorld().getSpawnLocation();
	}

	@Override
	public Location getLocation(Location arg0) {
		return this.getWorld().getSpawnLocation();
	}

	@Override
	public int getMaxFireTicks() {
		return 0;
	}

	@Override
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		return null;
	}

	@Override
	public Entity getPassenger() {
		return null;
	}

	@Override
	public int getTicksLived() {
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}

	@Override
	public Entity getVehicle() {
		return null;
	}

	@Override
	public Vector getVelocity() {
		return new Vector(0, 0, 0);
	}

	@Override
	public World getWorld() {
		return Bukkit.getServer().getWorlds().get(0);
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isInsideVehicle() {
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean leaveVehicle() {
		return true;
	}

	@Override
	public void playEffect(EntityEffect arg0) {
	}

	@Override
	public void remove() {
	}

	@Override
	public void setFallDistance(float arg0) {
	}

	@Override
	public void setFireTicks(int arg0) {
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent arg0) {
	}

	@Override
	public boolean setPassenger(Entity arg0) {
		return false;
	}

	@Override
	public void setTicksLived(int arg0) {
	}

	@Override
	public void setVelocity(Vector arg0) {
	}

	@Override
	public boolean teleport(Location arg0) {
		return false;
	}

	@Override
	public boolean teleport(Entity arg0) {
		return false;
	}

	@Override
	public boolean teleport(Location arg0, TeleportCause arg1) {
		return false;
	}

	@Override
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		return false;
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		return null;
	}

	@Override
	public boolean hasMetadata(String arg0) {
		return false;
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1) {
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
	}

	@Override
	public void _INVALID_damage(int arg0) {
	}

	@Override
	public void _INVALID_damage(int arg0, Entity arg1) {
	}

	@Override
	public int _INVALID_getHealth() {
		return 0;
	}

	@Override
	public int _INVALID_getMaxHealth() {
		return 0;
	}

	@Override
	public void _INVALID_setHealth(int arg0) {
	}

	@Override
	public void _INVALID_setMaxHealth(int arg0) {
	}

	@Override
	public void damage(double arg0) {
	}

	@Override
	public void damage(double arg0, Entity arg1) {
	}

	@Override
	public double getHealth() {
		return 0;
	}

	@Override
	public double getMaxHealth() {
		return 0;
	}

	@Override
	public void resetMaxHealth() {
	}

	@Override
	public void setHealth(double arg0) {
	}

	@Override
	public void setMaxHealth(double arg0) {
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		return null;
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0, Vector arg1) {
		return null;
	}

	@Override
	public void abandonConversation(Conversation arg0) {
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
	}

	@Override
	public void acceptConversationInput(String arg0) {
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		return false;
	}

	@Override
	public boolean isConversing() {
		return false;
	}

	@Override
	public long getFirstPlayed() {
		return 0;
	}

	@Override
	public long getLastPlayed() {
		return 0;
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isBanned() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		return false;
	}

	@Override
	public void setBanned(boolean arg0) {
	}

	@Override
	public void setWhitelisted(boolean arg0) {
	}

	@Override
	public Map<String, Object> serialize() {
		return null;
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return null;
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
	}

	@Override
	public void awardAchievement(Achievement arg0) {
	}

	@Override
	public boolean canSee(Player arg0) {
		return false;
	}

	@Override
	public void chat(String arg0) {
	}

	@Override
	public void decrementStatistic(Statistic arg0) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
	}

	@Override
	public void decrementStatistic(Statistic arg0, EntityType arg1, int arg2) {
	}

	@Override
	public InetSocketAddress getAddress() {
		return null;
	}

	@Override
	public boolean getAllowFlight() {
		return false;
	}

	@Override
	public Location getBedSpawnLocation() {
		return null;
	}

	@Override
	public Location getCompassTarget() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public float getExhaustion() {
		return 0;
	}

	@Override
	public float getExp() {
		return 0;
	}

	@Override
	public float getFlySpeed() {
		return 0;
	}

	@Override
	public int getFoodLevel() {
		return 0;
	}

	@Override
	public double getHealthScale() {
		return 0;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public String getPlayerListName() {
		return null;
	}

	@Override
	public long getPlayerTime() {
		return 0;
	}

	@Override
	public long getPlayerTimeOffset() {
		return 0;
	}

	@Override
	public WeatherType getPlayerWeather() {
		return null;
	}

	@Override
	public float getSaturation() {
		return 0;
	}

	@Override
	public Scoreboard getScoreboard() {
		return null;
	}

	@Override
	public int getStatistic(Statistic arg0) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public int getStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public int getStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
		return 0;
	}

	@Override
	public int getTotalExperience() {
		return 0;
	}

	@Override
	public float getWalkSpeed() {
		return 0;
	}

	@Override
	public void giveExp(int arg0) {
	}

	@Override
	public void giveExpLevels(int arg0) {
	}

	@Override
	public boolean hasAchievement(Achievement arg0) {
		return false;
	}

	@Override
	public void hidePlayer(Player arg0) {
	}

	@Override
	public void incrementStatistic(Statistic arg0) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
	}

	@Override
	public void incrementStatistic(Statistic arg0, EntityType arg1, int arg2) throws IllegalArgumentException {
	}

	@Override
	public boolean isFlying() {
		return false;
	}

	@Override
	public boolean isHealthScaled() {
		return false;
	}

	@Override
	public boolean isOnGround() {
		return false;
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return false;
	}

	@Override
	public boolean isSleepingIgnored() {
		return false;
	}

	@Override
	public boolean isSneaking() {
		return false;
	}

	@Override
	public boolean isSprinting() {
		return false;
	}

	@Override
	public void kickPlayer(String arg0) {
	}

	@Override
	public void loadData() {
	}

	@Override
	public boolean performCommand(String arg0) {
		return false;
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2) {
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
	}

	@Override
	public void playNote(Location arg0, byte arg1, byte arg2) {
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
	}

	@Override
	public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
	}

	@Override
	public void playSound(Location arg0, String arg1, float arg2, float arg3) {
	}

	@Override
	public void removeAchievement(Achievement arg0) {
	}

	@Override
	public void resetPlayerTime() {
	}

	@Override
	public void resetPlayerWeather() {
	}

	@Override
	public void saveData() {
	}

	@Override
	public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
	}

	@Override
	public void sendBlockChange(Location arg0, int arg1, byte arg2) {
	}

	@Override
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3, byte[] arg4) {
		return false;
	}

	@Override
	public void sendMap(MapView arg0) {
	}

	@Override
	public void sendRawMessage(String arg0) {
	}

	@Override
	public void setAllowFlight(boolean arg0) {
	}

	@Override
	public void setBedSpawnLocation(Location arg0) {
	}

	@Override
	public void setBedSpawnLocation(Location arg0, boolean arg1) {
	}

	@Override
	public void setCompassTarget(Location arg0) {
	}

	@Override
	public void setDisplayName(String arg0) {
	}

	@Override
	public void setExhaustion(float arg0) {
	}

	@Override
	public void setExp(float arg0) {
	}

	@Override
	public void setFlySpeed(float arg0) throws IllegalArgumentException {
	}

	@Override
	public void setFlying(boolean arg0) {
	}

	@Override
	public void setFoodLevel(int arg0) {
	}

	@Override
	public void setHealthScale(double arg0) throws IllegalArgumentException {
	}

	@Override
	public void setHealthScaled(boolean arg0) {
	}

	@Override
	public void setLevel(int arg0) {
	}

	@Override
	public void setPlayerListName(String arg0) {
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1) {
	}

	@Override
	public void setPlayerWeather(WeatherType arg0) {
	}

	@Override
	public void setResourcePack(String arg0) {
	}

	@Override
	public void setSaturation(float arg0) {
	}

	@Override
	public void setScoreboard(Scoreboard arg0) throws IllegalArgumentException, IllegalStateException {
	}

	@Override
	public void setSleepingIgnored(boolean arg0) {
	}

	@Override
	public void setSneaking(boolean arg0) {
	}

	@Override
	public void setSprinting(boolean arg0) {
	}

	@Override
	public void setStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
	}

	@Override
	public void setStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
	}

	@Override
	public void setStatistic(Statistic arg0, EntityType arg1, int arg2) {
	}

	@Override
	public void setTexturePack(String arg0) {
	}

	@Override
	public void setTotalExperience(int arg0) {
	}

	@Override
	public void setWalkSpeed(float arg0) throws IllegalArgumentException {
	}

	@Override
	public void showPlayer(Player arg0) {
	}

	@Override
	public void updateInventory() {
	}
	
	@Override
	public void sendSignChange(Location loc, String[] lines) {
	}

}
