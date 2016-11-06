package com.projectkorra.items.attributes.nbt;

import java.util.Arrays;
import java.util.Map;

import com.projectkorra.items.attributes.nbt.NBTHandler.Type;
import com.projectkorra.items.attributes.nbt.util.ConvertedMap;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public final class NBTCompound extends ConvertedMap {

	public NBTCompound(Object handle) {
		super(handle, getDataMap(handle));
	}
	

	@SuppressWarnings("unchecked")
	private static Map<String, Object> getDataMap(Object handle) {
		try {
			return (Map<String, Object>) ReflectionHandler.getValue(handle, true, Type.TAG_COMPOUND.getName());
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}
	
	
    public NBTCompound getMap(String key, boolean createNew) {
    	Iterable<String> path = Arrays.asList(key);
        NBTCompound current = this;

        for (String entry : path) {
            NBTCompound child = (NBTCompound) current.get(entry);

            if (child == null) {
                if (createNew) {
                    current.put(entry, child = NBTHandler.newCompound());
                }    
            }    
            current = child;
        }
        
        return current;
    }
    

	public NBTList getList(String key, boolean createNew) {
		NBTList list = (NBTList) get(key);
		if (list == null) {
			if (createNew) {	
				put(key, list = NBTHandler.newList());
			}
		}
		
		return list;
	}
}