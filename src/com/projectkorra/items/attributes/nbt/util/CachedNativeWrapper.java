package com.projectkorra.items.attributes.nbt.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.projectkorra.items.attributes.nbt.NbtFactory;

public final class CachedNativeWrapper {
	private final ConcurrentMap<Object, Object> cache = new MapMaker().weakKeys().makeMap();
	
	
	public Object unwrap(String name, Object value) {
		return NbtFactory.getInstance().unwrapValue(name, value);
	}
	
	
	public Object wrap(Object value) {
		Object current = cache.get(value);
		
		if (current == null) {
			current = NbtFactory.getInstance().wrapNative(value);
			if (current != null) {
				cache.put(value, current);
			}
		}
		
		return current;
	}
}