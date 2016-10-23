package com.projectkorra.items.attributes.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;
import com.projectkorra.items.attributes.Nbt;

public final class CachedNativeWrapper {
	private final ConcurrentMap<Object, Object> cache = new MapMaker()
			.weakKeys()
			.makeMap();

	public Object wrap(Object value) {
		Object current = cache.get(value);

		if (current == null) {

			current = Nbt.get().wrapNative(value);

			if (current instanceof ConvertedMap || current instanceof ConvertedList) {

				cache.put(value, current);
			}
		}

		return current;
	}
}