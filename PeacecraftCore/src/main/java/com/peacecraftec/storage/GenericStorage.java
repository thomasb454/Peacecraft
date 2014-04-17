package com.peacecraftec.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peacecraftec.storage.Storage;

public abstract class GenericStorage implements Storage {
	
	private File file;
	
	public GenericStorage() {
		this("");
	}
	
	public GenericStorage(String path) {
		this(path != null && !path.equals("") ? new File(path) : null);
	}
	
	public GenericStorage(File file) {
		this.file = file;
	}
	
	@Override
	public void load() {
		if(this.file == null) {
			return;
		}
		
		FileInputStream in = null;
		try {
			if(!this.file.exists()) {
				if(this.file.getParentFile() != null && !this.file.getParentFile().exists()) {
					this.file.getParentFile().mkdirs();
				}

				this.file.createNewFile();
			}

			in = new FileInputStream(this.file);
			this.load(in);
		} catch (IOException e) {
			System.err.println("Failed to load from file " + this.file.getPath() + "!");
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public void save() {
		if(this.file == null) {
			return;
		}
		
		FileOutputStream out = null;
		try {
			if(!this.file.exists()) {
				if(!this.file.getParentFile().exists()) {
					this.file.getParentFile().mkdirs();
				}
				
				this.file.createNewFile();
			}

			out = new FileOutputStream(this.file);
			this.save(out);
		} catch (IOException e) {
			System.err.println("Failed to save config file to " + this.file.getPath() + "!");
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public List<String> getAbsoluteKeys(boolean deep) {
		return this.getAbsoluteKeys("", deep);
	}
	
	@Override
	public List<String> getRelativeKeys(boolean deep) {
		return this.getAbsoluteKeys("", deep);
	}
	
	protected Object getValue(String path) {
		return this.getValue(path, null);
	}
	
	protected abstract Object getValue(String path, Object def);
	
	@Override
	public boolean contains(String path) {
		return this.getValue(path) != null;
	}

	@Override
	public String getString(String path) {
		return this.getString(path, "");
	}

	@Override
	public boolean getBoolean(String path) {
		return this.getBoolean(path, false);
	}

	@Override
	public byte getByte(String path) {
		return this.getByte(path, (byte) 0);
	}

	@Override
	public short getShort(String path) {
		return this.getShort(path, (short) 0);
	}

	@Override
	public int getInteger(String path) {
		return this.getInteger(path, 0);
	}

	@Override
	public long getLong(String path) {
		return this.getLong(path, 0);
	}

	@Override
	public float getFloat(String path) {
		return this.getFloat(path, 0);
	}

	@Override
	public double getDouble(String path) {
		return this.getDouble(path, 0);
	}

	@Override
	public <T> List<T> getList(String path, Class<T> type) {
		return this.getList(path, type, new ArrayList<T>());
	}
	
	@Override
	public Map<String, Object> getMap(String path) {
		return this.getMap(path, new HashMap<String, Object>());
	}
	
	@Override
	public void applyDefault(String path, Object value) {
		if(!this.contains(path)) {
			this.setValue(path, value);
		}
	}

}
