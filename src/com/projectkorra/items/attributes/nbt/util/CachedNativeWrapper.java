package com.projectkorra.items.attributes.nbt.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.projectkorra.items.attributes.nbt.Nbt;

public final class CachedNativeWrapper {
	
	private final ConcurrentMap<Object, Object> cache;
	private Nbt nbt;
	
	
	public CachedNativeWrapper() {
		this.cache = new MapMaker().weakKeys().makeMap();
		this.nbt = Nbt.getInstance();
	}
	
	
	public Object wrap(Object value) {
		Object current = cache.get(value);
		
		if (current == null) {
			current = nbt.wrapNative(value);
			
			if (current instanceof ConvertedMap || current instanceof ConvertedList) {
				cache.put(value, current);
			}
		}
		return current;
	}
	
	public Object unwrap(String name, Object value) {
		return nbt.unwrapValue(name, value);
	}
}