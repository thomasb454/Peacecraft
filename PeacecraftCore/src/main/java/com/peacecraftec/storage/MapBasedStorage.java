package com.peacecraftec.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peacecraftec.util.CastUtil;

@SuppressWarnings("unchecked")
public abstract class MapBasedStorage extends GenericStorage {
	
	protected Map<String, Object> root = new HashMap<String, Object>();
	
	public MapBasedStorage() {
		super();
	}
	
	public MapBasedStorage(String path) {
		super(path);
	}
	
	public MapBasedStorage(File file) {
		super(file);
		this.root = new HashMap<String, Object>();
	}

	@Override
	public List<String> getAbsoluteKeys(String path, boolean deep) {
		List<String> keys = this.getRelativeKeys(path, deep);
		if(!path.equals("")) {
			List<String> ret = new ArrayList<String>();
			for(String key : keys) {
				ret.add(path + "." + key);
			}
			
			keys = ret;
		}
		
		return keys;
	}

	@Override
	public List<String> getRelativeKeys(String path, boolean deep) {
		List<String> ret = new ArrayList<String>();
		Map<String, Object> m = this.getMap(path);
		if(m != null) {
			for(String key : m.keySet()) {
				ret.add(key);
			}
			
			if(deep) {
				for(String key : m.keySet()) {
					Object ob = m.get(key);
					if(ob instanceof Map) {
						this.addKeys(key, (Map<String, Object>) ob, ret);
					}
				}
			}
		}
		
		return ret;
	}
	
	private void addKeys(String path, Map<String, Object> m, List<String> list) {
		for(String key : m.keySet()) {
			list.add(path + "." + key);
			Object ob = m.get(key);
			if(ob instanceof Map) {
				this.addKeys(path + "." + key, (Map<String, Object>) ob, list);
			}
		}
	}

	@Override
	protected Object getValue(String path, Object def) {
		if(path.equals("")) {
			return new HashMap<String, Object>(this.root);
		}
		
		if(!path.contains(".")) {
			Object value = this.root.get(path);
			return value;
		}

		String[] parts = path.split("\\.");
		Map<String, Object> node = this.root;
		boolean change = false;
		for(int index = 0; index < parts.length; index++) {
			Object obj = node.get(parts[index]);
			if(obj == null) {
				if(def != null) {
					if(index == parts.length - 1) {
						obj = def;
					} else {
						obj = new HashMap<String, Object>();
					}
					
					node.put(parts[index], obj);
					change = true;
				} else {
					return null;
				}
			}

			if(index == parts.length - 1) {
				if(change) {
					this.save();
				}
				
				return obj;
			}

			try {
				node = (Map<String, Object>) obj;
			} catch (ClassCastException e) {
				if(change) {
					this.save();
				}
				
				return null;
			}
		}

		if(change) {
			this.save();
		}
		
		return null;
	}

	@Override
	public String getString(String path, String def) {
		Object value = this.getValue(path, def);
		if(value instanceof String) {
			return (String) value;
		} else if(def != null) {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		Boolean val = CastUtil.castBoolean(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public byte getByte(String path, byte def) {
		Byte val = CastUtil.castByte(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public short getShort(String path, short def) {
		Short val = CastUtil.castShort(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public int getInteger(String path, int def) {
		Integer val = CastUtil.castInt(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public long getLong(String path, long def) {
		Long val = CastUtil.castLong(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public float getFloat(String path, float def) {
		Float val = CastUtil.castFloat(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public double getDouble(String path, double def) {
		Double val = CastUtil.castDouble(this.getValue(path, def));
		if(val != null) {
			return val;
		} else {
			this.setValue(path, def);
			this.save();
		}

		return def;
	}

	@Override
	public <T> List<T> getList(String path, Class<T> type, List<T> def) {
		List<T> copy = def != null ? new ArrayList<T>(def) : null;
		Object value = this.getValue(path, copy);
		if(value != null && value instanceof List) {
			try {
				return new ArrayList<T>((List<T>) value);
			} catch(ClassCastException e) {
				this.setValue(path, copy);
				this.save();
			}
		} else {
			this.setValue(path, copy);
			this.save();
		}

		return copy != null ? new ArrayList<T>(copy) : null;
	}

	@Override
	public Map<String, Object> getMap(String path, Map<String, Object> def) {
		Map<String, Object> copy = def != null ? new HashMap<String, Object>(def) : null;
		Object value = this.getValue(path, copy);
		if(value != null && value instanceof Map) {
			try {
				return new HashMap<String, Object>((Map<String, Object>) value);
			} catch(ClassCastException e) {
				this.setValue(path, copy);
				this.save();
			}
		} else {
			this.setValue(path, copy);
			this.save();
		}

		return copy != null ? new HashMap<String, Object>(copy) : null;
	}

	@Override
	public void setValue(String path, Object value) {
		if(!path.contains(".")) {
			if(value == null) {
				this.root.remove(path);
			} else {
				this.root.put(path, value);
			}
			
			return;
		}

		String[] parts = path.split("\\.");
		Map<String, Object> node = this.root;

		for(int index = 0; index < parts.length; index++) {
			Object obj = node.get(parts[index]);

			if(index == parts.length - 1) {
				if(value == null) {
					node.remove(parts[index]);
				} else {
					node.put(parts[index], value);
				}
				
				return;
			}

			if(obj == null || !(obj instanceof Map)) {
				if(value != null) {
					obj = new HashMap<String, Object>();
					node.put(parts[index], obj);
				} else {
					return;
				}
			}

			node = (Map<String, Object>) obj;
		}
	}

	@Override
	public void remove(String path) {
		this.setValue(path, null);
	}

}
