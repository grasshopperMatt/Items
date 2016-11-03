package com.projectkorra.items.attributes.nbt.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.projectkorra.items.attributes.nbt.NbtHandler;

public final class CachedNativeWrapper {
	private final ConcurrentMap<Object, Object> cache = new MapMaker().weakKeys().makeMap();
	
	
	public Object wrap(Object value) {
		Object current = cache.get(value);
		
		if (current == null) {
			current = NbtHandler.wrapNative(value);
			
			if (current != null) {
				cache.put(value, current);
			}
		}
		
		return current;
	}
	
	
	public Object unwrap(String name, Object value) {
		return NbtHandler.unwrapValue(name, value);
	}
}