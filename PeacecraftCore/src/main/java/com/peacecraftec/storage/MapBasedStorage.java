package com.peacecraftec.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class MapBasedStorage extends GenericFileStorage {

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
            } catch(ClassCastException e) {
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
