package com.projectkorra.items.attributes.nbt.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.projectkorra.items.attributes.nbt.NBTHandler;

public final class WrapperCache {
	private final ConcurrentMap<Object, Object> cache = new MapMaker().weakKeys().makeMap();
	
	
	public Object wrap(Object value) {
		Object current = cache.get(value);
		
		if (current == null) {
			current = NBTHandler.wrapNative(value);
			
			if (current instanceof ConvertedMap || current instanceof ConvertedList) {
				cache.put(value, current);
			}
		}
		
		return current;
	}
	
	
	public Object unwrap(String name, Object value) {
		return NBTHandler.unwrapValue(name, value);
	}
}