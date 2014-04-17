package com.peacecraftec.bukkit.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.peacecraftec.bukkit.backup.command.BackupCommands;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.ModuleManager;

public class PeacecraftBackup extends Module {

	private boolean backingUp = false;
	private int task;
	private int backupTask;
	private boolean cancelled = false;

	public PeacecraftBackup(ModuleManager manager) {
		super("Backup", manager);
	}

	@Override
	public void onEnable() {
		this.loadConfig();
		this.getManager().getPermissionManager().register(this, BackupPermissions.class);
		this.getManager().getCommandManager().register(this, new BackupCommands(this));
		this.startTask();
	}

	@Override
	public void onDisable() {
		this.getManager().getScheduler().cancelTask(this, this.task);
		this.getManager().getScheduler().cancelTask(this, this.backupTask);
		this.backingUp = false;
		this.cancelled = true;
	}

	@Override
	public void reload() {
		this.getManager().getScheduler().cancelTask(this, this.task);
		this.getManager().getScheduler().cancelTask(this, this.backupTask);
		this.cancelled = true;
		this.loadConfig();
		this.startTask();
	}

	private void loadConfig() {
		this.getConfig().load();
		this.getConfig().applyDefault("backup-limit", 5);
		List<String> worlds = new ArrayList<String>();
		worlds.add("world");
		this.getConfig().applyDefault("worlds", worlds);
		List<String> pluginExclude = new ArrayList<String>();
		pluginExclude.add("examplefilename");
		this.getConfig().applyDefault("plugin-exclude", pluginExclude);
		List<Integer> hours = new ArrayList<Integer>();
		hours.add(0);
		hours.add(6);
		hours.add(12);
		hours.add(18);
		this.getConfig().applyDefault("hours", hours);
		this.getConfig().save();
	}
	
	public void cancel() {
		if(!this.backingUp) {
			return;
		}
		
		this.cancelled = true;
	}

	private void startTask() {
		this.task = this.getManager().getScheduler().runTaskTimer(this, new Runnable() {
			private int lastHr = -1;

			@Override
			public void run() {
				int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				int minutes = Calendar.getInstance().get(Calendar.MINUTE);
				if(hour != this.lastHr && minutes <= 4 && getConfig().getList("hours", Integer.class).contains(hour)) {
					this.lastHr = hour;
					if(!backingUp) {
						initiateBackup();
					}
				}
			}
		}, 200, 2400);
	}
	
	public boolean isBackingUp() {
		return this.backingUp;
	}
	
	public boolean isBackupCancelled() {
		return this.cancelled;
	}

	public void initiateBackup() {
		this.backupTask = this.getManager().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				backingUp = true;
				getManager().broadcastMessage("backup.broadcast.initiating");
				
				File root = new File("backups");
				if(!root.exists()) {
					root.mkdirs();
				}
				
				if(cancelled) {
					cancelled();
					return;
				}

				for(String world : getConfig().getList("worlds", String.class)) {
					if(cancelled) {
						cancelled();
						return;
					}
					
					File from = new File(world);
					if(!from.exists()) {
						getLogger().warning("World \"" + world + "\" was not found.");
						continue;
					}
					
					if(cancelled) {
						cancelled();
						return;
					}

					File todir = new File(root, world);
					if(!todir.exists()) {
						todir.mkdirs();
					}
					
					if(cancelled) {
						cancelled();
						return;
					}
					
					File files[] = todir.listFiles();
					if(files.length >= getConfig().getInteger("backup-limit")) {
						int num = files.length - getConfig().getInteger("backup-limit");
						Arrays.sort(files, new Comparator<File>() {
							@Override
							public int compare(File f1, File f2) {
								return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
							}
						});
						
						for(File file : files) {
							if(num <= 0) {
								break;
							}
							
							file.delete();
							num--;
						}
					}
					
					if(cancelled) {
						cancelled();
						return;
					}

					File to = new File(todir, world + "-" + new Date().toString().replaceAll(" ", "-").replaceAll("/", "-").replaceAll(":", "-") + ".zip");
					try {
						to.delete();
						to.createNewFile();
					} catch (IOException e) {
						getLogger().severe("Failed to create zip file for world backup.");
						e.printStackTrace();
						getManager().broadcastMessage("backup.broadcast.error");
						cancelled = false;
						backingUp = false;
						return;
					}
					
					if(cancelled) {
						cancelled();
						to.delete();
						return;
					}
					
					World w = Bukkit.getWorld(world);
					if(w != null) {
						w.setAutoSave(false);
					}

					this.zip(from, to);
					if(w != null) {
						w.setAutoSave(true);
					}
					
					if(cancelled) {
						cancelled();
						to.delete();
						return;
					}
				}
				
				if(cancelled) {
					cancelled();
					return;
				}
				
				File from = new File("plugins");
				File todir = new File(root, "plugins");
				if(!todir.exists()) {
					todir.mkdirs();
				}
				
				if(cancelled) {
					cancelled();
					return;
				}
				
				File files[] = todir.listFiles();
				if(files.length >= getConfig().getInteger("backup-limit")) {
					int num = files.length - getConfig().getInteger("backup-limit");
					Arrays.sort(files, new Comparator<File>() {
						@Override
						public int compare(File f1, File f2) {
							return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
						}
					});
					
					for(File file : files) {
						if(num <= 0) {
							break;
						}
						
						file.delete();
						num--;
					}
				}
				
				if(cancelled) {
					cancelled();
					return;
				}
				
				File to = new File(todir, "plugins-" + new Date().toString().replaceAll(" ", "-").replaceAll("/", "-").replaceAll(":", "-") + ".zip");
				try {
					to.delete();
					to.createNewFile();
				} catch (IOException e) {
					getLogger().severe("Failed to create zip file for plugin backup.");
					e.printStackTrace();
					getManager().broadcastMessage("backup.broadcast.error");
					cancelled = false;
					backingUp = false;
					return;
				}
				
				if(cancelled) {
					cancelled();
					to.delete();
					return;
				}
				
				this.zip(from, to, getConfig().getList("plugin-exclude", String.class));
				if(cancelled) {
					cancelled();
					to.delete();
					return;
				}
				
				getManager().broadcastMessage("backup.broadcast.finished");
				backingUp = false;
				cancelled = false;
			}

			private void zip(File from, File to) {
				this.zip(from, to, new ArrayList<String>());
			}

			private void zip(File from, File to, List<String> exclude) {
				ZipOutputStream zos = null;
				try {
					zos = new ZipOutputStream(new FileOutputStream(to));
					this.zipInternal(from, zos, from.getName(), exclude);
				} catch (IOException e) {
					getLogger().severe("Failed to zip folder \"" + from.getName() + "\"");
					e.printStackTrace();
				} finally {
					if(zos != null) {
						try {
							zos.close();
						} catch(IOException e) {
						}
					}
				}
			}

			private void zipInternal(File dir, ZipOutputStream zos, String prefix, List<String> exclude) throws IOException {
				byte[] buffer = new byte[1024];
				for(File file : dir.listFiles()) {
					if(cancelled) {
						return;
					}
					
					if(exclude.contains(file.getName())) {
						continue;
					}

					if(file.isDirectory()) {
						this.zipInternal(file, zos, prefix + File.separatorChar + file.getName(), exclude);
						continue;
					}

					ZipEntry ze = new ZipEntry(prefix + File.separatorChar + file.getName());
					FileInputStream in = null;
					boolean nextEntry = false;
					try {
						zos.putNextEntry(ze);
						nextEntry = true;
						in = new FileInputStream(file);
						int len;
						while((len = in.read(buffer)) > 0) {
							zos.write(buffer, 0, len);
						}
					} finally {
						if(in != null) {
							try {
								in.close();
							} catch (IOException e) {
							}
						}
						
						if(nextEntry) {
							try {
								zos.closeEntry();
							} catch (IOException e) {
							}
						}
					}
				}
			}
			
			private void cancelled() {
				getManager().broadcastMessage("backup.broadcast.cancelled");
				cancelled = false;
				backingUp = false;
			}
		});
	}

}
