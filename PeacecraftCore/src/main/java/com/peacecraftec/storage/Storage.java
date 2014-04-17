package com.peacecraftec.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface Storage {

	public void load();
	
	public void load(InputStream in);
	
	public void save();
	
	public void save(OutputStream out);
	
	public List<String> getAbsoluteKeys(boolean deep);
	
	public List<String> getAbsoluteKeys(String path, boolean deep);
	
	public List<String> getRelativeKeys(boolean deep);
	
	public List<String> getRelativeKeys(String path, boolean deep);
	
	public boolean contains(String path);
	
	public String getString(String path);
	
	public String getString(String path, String def);
	
	public boolean getBoolean(String path);
	
	public boolean getBoolean(String path, boolean def);
	
	public byte getByte(String path);
	
	public byte getByte(String path, byte def);
	
	public short getShort(String path);
	
	public short getShort(String path, short def);
	
	public int getInteger(String path);
	
	public int getInteger(String path, int def);
	
	public long getLong(String path);
	
	public long getLong(String path, long def);
	
	public float getFloat(String path);
	
	public float getFloat(String path, float def);
	
	public double getDouble(String path);
	
	public double getDouble(String path, double def);
	
	public <T> List<T> getList(String path, Class<T> type);
	
	public <T> List<T> getList(String path, Class<T> type, List<T> def);
	
	public Map<String, Object> getMap(String path);
	
	public Map<String, Object> getMap(String path, Map<String, Object> def);
	
	public void applyDefault(String path, Object value);
	
	public void setValue(String path, Object value);
	
	public void remove(String path);
	
}
