package com.peacecraftec.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericStorage implements Storage {
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
    public boolean getBoolean(String path) {
        return this.getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        Boolean val = castBoolean(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public byte getByte(String path) {
        return this.getByte(path, (byte) 0);
    }

    @Override
    public byte getByte(String path, byte def) {
        Byte val = castByte(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public short getShort(String path) {
        return this.getShort(path, (short) 0);
    }

    @Override
    public short getShort(String path, short def) {
        Short val = castShort(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public int getInteger(String path) {
        return this.getInteger(path, 0);
    }

    @Override
    public int getInteger(String path, int def) {
        Integer val = castInt(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public long getLong(String path) {
        return this.getLong(path, 0);
    }

    @Override
    public long getLong(String path, long def) {
        Long val = castLong(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public float getFloat(String path) {
        return this.getFloat(path, 0);
    }

    @Override
    public float getFloat(String path, float def) {
        Float val = castFloat(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public double getDouble(String path) {
        return this.getDouble(path, 0);
    }

    @Override
    public double getDouble(String path, double def) {
        Double val = castDouble(this.getValue(path, def));
        if(val != null) {
            return val;
        } else {
            this.setValue(path, def);
            this.save();
        }

        return def;
    }

    @Override
    public <T> List<T> getList(String path, Class<T> type) {
        return this.getList(path, type, new ArrayList<T>());
    }

    @Override
    public <T> List<T> getList(String path, Class<T> type, List<T> def) {
        List<T> copy = def != null ? new ArrayList<T>(def) : null;
        Object value = this.getValue(path, copy);
        if(value != null && value instanceof List) {
            try {
                return new ArrayList<T>((List<T>) value);
            } catch(ClassCastException e) {
            }
        }

        this.setValue(path, copy);
        this.save();
        return def;
    }

    @Override
    public Map<String, Object> getMap(String path) {
        return this.getMap(path, new HashMap<String, Object>());
    }

    @Override
    public Map<String, Object> getMap(String path, Map<String, Object> def) {
        Map<String, Object> copy = def != null ? new HashMap<String, Object>(def) : null;
        Object value = this.getValue(path, copy);
        if(value != null && value instanceof Map) {
            try {
                return new HashMap<String, Object>((Map<String, Object>) value);
            } catch(ClassCastException e) {
            }
        }

        this.setValue(path, copy);
        this.save();
        return def;
    }

    @Override
    public void applyDefault(String path, Object value) {
        if(!this.contains(path)) {
            this.setValue(path, value);
        }
    }

    private static Boolean castBoolean(Object o) {
        if(o instanceof Boolean) {
            return (Boolean) o;
        } else if(o instanceof String) {
            return Boolean.parseBoolean((String) o);
        }

        return null;
    }

    private static Byte castByte(Object o) {
        if(o instanceof Number) {
            return ((Number) o).byteValue();
        } else if(o instanceof String) {
            try {
                return Byte.parseByte((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

    private static Short castShort(Object o) {
        if(o instanceof Number) {
            return ((Number) o).shortValue();
        } else if(o instanceof String) {
            try {
                return Short.parseShort((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

    private static Integer castInt(Object o) {
        if(o instanceof Number) {
            return ((Number) o).intValue();
        } else if(o instanceof String) {
            try {
                return Integer.parseInt((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

    private static Long castLong(Object o) {
        if(o instanceof Number) {
            return ((Number) o).longValue();
        } else if(o instanceof String) {
            try {
                return Long.parseLong((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

    private static Float castFloat(Object o) {
        if(o instanceof Number) {
            return ((Number) o).floatValue();
        } else if(o instanceof String) {
            try {
                return Float.parseFloat((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

    private static Double castDouble(Object o) {
        if(o instanceof Number) {
            return ((Number) o).doubleValue();
        } else if(o instanceof String) {
            try {
                return Double.parseDouble((String) o);
            } catch(NumberFormatException e) {
            }
        }

        return null;
    }

}
