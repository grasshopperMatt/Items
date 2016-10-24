package com.projectkorra.items.attributes.nbt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.projectkorra.items.attributes.nbt.util.ConvertedMap;

public final class NbtCompound extends ConvertedMap {

	public NbtCompound(Object handle) {
		super(handle, Nbt.getInstance().getDataMap(handle));
	}
	
	
	public NbtList getList(String key, boolean createNew) {
		NbtList list = (NbtList) get(key);
		if (list == null) {
			if (createNew) {	
				put(key, list = Nbt.createList());
			}
		}
		
		return list;
	}
	

    public NbtCompound getMap(String name, boolean createNew) {
    	Iterable<String> path = Arrays.asList(name);
        NbtCompound current = this;

        for (String entry : path) {
            NbtCompound child = (NbtCompound) current.get(entry);

            if (child == null) {
                if (createNew) {
                    current.put(entry, child = Nbt.createCompound());
                }    
            }    
            current = child;
        }
        
        return current;
    }
    
    
    private List<String> getPathElements(String path) {
        return Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(path));
    }
    
    
    @SuppressWarnings("unchecked")
    public <T> T getPath(String path) {
        List<String> entries = getPathElements(path);
        NbtCompound map = getMap(entries.subList(0, entries.size() - 1).toString(), false);     

        if (map != null) {
            return (T) map.get(entries.get(entries.size() - 1));
        }

        return null;
    }  
    
    
    public NbtCompound putPath(String path, Object value) {
        List<String> entries = getPathElements(path);
        
        Map<String, Object> map = getMap(entries.subList(0, entries.size() - 1).toString(), true);
        map.put(entries.get(entries.size() - 1), value);
        
        return this;
    }
    

	public Byte getByte(String key, byte defaultValue) {
		if (containsKey(key)) {
			return (Byte) get(key);
		}

		return defaultValue;
	}
	

	public Short getShort(String key, short defaultValue) {
		if (containsKey(key)) {
			return (Short) get(key);
		}

		return defaultValue;
	}
	

	public Long getLong(String key, long defaultValue) {
		if (containsKey(key)) {
			return (Long) get(key);
		}

		return defaultValue;
	}
	

	public Integer getInteger(String key, int defaultValue) {
		if (containsKey(key)) {
			return (Integer) get(key);
		}

		return defaultValue;
	}
	

	public Float getFloat(String key, float defaultValue) {
		if (containsKey(key)) {
			return (Float) get(key);
		}

		return defaultValue;
	}
	

	public Double getDouble(String key, double defaultValue) {
		if (containsKey(key)) {
			return (Double) get(key);
		}

		return defaultValue;
	}
	

	public String getString(String key, String defaultValue) {
		if (containsKey(key)) {
			return (String) get(key);
		}

		return defaultValue;
	}
	

	public Byte[] getByteArray(String key, Byte[] defaultValue) {
		if (containsKey(key)) {
			return (Byte[]) get(key);
		}

		return defaultValue;
	}
	

	public Integer[] getIntegerArray(String key, Integer[] defaultValue) {
		if (containsKey(key)) {
			return (Integer[]) get(key);
		}

		return defaultValue;
	}
	
}
