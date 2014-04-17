package com.peacecraftec.module.cmd.sender;

public interface CommandSender {

	public String getName();
	
	public String getDisplayName();
	
	public String getLanguage();
	
	public void sendMessage(String key);
	
	public void sendMessage(String key, Object... args);
	
	public boolean hasPermission(String permission);
	
}
