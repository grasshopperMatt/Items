package com.projectkorra.items.attributes.nbt;

import java.util.Arrays;

import com.projectkorra.items.attributes.nbt.util.ConvertedMap;

public final class NbtCompound extends ConvertedMap {

	public NbtCompound(Object handle) {
		super(handle, NbtFactory.getInstance().getDataMap(handle));
	}
    
    
	public NbtList getList(String key, boolean createNew) {
		NbtList list = (NbtList) get(key);
		if (list == null) {
			if (createNew) {	
				put(key, list = NbtFactory.createList());
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
                    current.put(entry, child = NbtFactory.createCompound());
                }    
            }    
            current = child;
        }
        
        return current;
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