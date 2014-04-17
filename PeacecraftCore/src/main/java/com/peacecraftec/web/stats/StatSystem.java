package com.peacecraftec.web.stats;

import java.util.Map;

public interface StatSystem {

	public Map<String, Map<String, Double>> getAll();
	
	public Map<String, Double> getAll(String player);
	
	public void addPlayer(String player);
	
	public boolean contains(String category, String player);
	
	public byte getByte(String stat, String player);
	
	public boolean getBoolean(String stat, String player);
	
	public char getChar(String stat, String player);
	
	public short getShort(String stat, String player);
	
	public int getInt(String stat, String player);
	
	public long getLong(String stat, String player);
	
	public float getFloat(String stat, String player);
	
	public double getDouble(String stat, String player);
	
	public void setStat(String stat, String player, byte value);
	
	public void setStat(String stat, String player, boolean value);
	
	public void setStat(String stat, String player, char value);
	
	public void setStat(String stat, String player, short value);
	
	public void setStat(String stat, String player, int value);
	
	public void setStat(String stat, String player, long value);
	
	public void setStat(String stat, String player, float value);
	
	public void setStat(String stat, String player, double value);
	
	public void increment(String stat, String player);
	
	public void increment(String stat, String player, double amount);
	
	public void cleanup();
	
}
