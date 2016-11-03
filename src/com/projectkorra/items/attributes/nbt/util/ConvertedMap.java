package com.projectkorra.items.attributes.nbt.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConvertedMap extends AbstractMap<String, Object> implements NbtWrapper {
	private final Object handle;
	private final Map<String, Object> original;
	private final CachedNativeWrapper cache = new CachedNativeWrapper();
	

	public ConvertedMap(Object handle, Map<String, Object> original) {
		this.handle = handle;
		this.original = original;
	}
	
	
	@Override
	public Object get(Object key) {
		return wrap(original.get(key));
	}
	
	
	@Override
	public Object put(String key, Object value) {
		return wrap(original.put((String) key, unwrap((String) key, value)));
	}
	
	
	@Override
	public Object remove(Object key) {
		return wrap(original.remove(key));
	}
	
	
	@Override
	public boolean containsKey(Object key) {
		return original.containsKey(key);
	}
	
	
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return new AbstractSet<Entry<String, Object>>() {
			
			@Override
			public boolean add(Entry<String, Object> e) {
				String key = e.getKey();
				Object value = e.getValue();

				original.put(key, unwrap(key, value));
				return true;
			}
			
			@Override
			public int size() {
				return original.size();
			}
			
			@Override
			public Iterator<Entry<String, Object>> iterator() {
				return ConvertedMap.this.iterator();
			}
			
		};
	}
	
	
	private Iterator<Entry<String, Object>> iterator() {
		Iterator<Entry<String, Object>> proxy = original.entrySet().iterator();
		return new Iterator<Entry<String, Object>>() {
			
			@Override
			public boolean hasNext() {
				return proxy.hasNext();
			}
			
			@Override
			public Entry<String, Object> next() {
				Entry<String, Object> entry = proxy.next();

				return new SimpleEntry<String, Object>(entry.getKey(), wrap(entry.getValue()));
			}
			
			@Override
			public void remove() {
				proxy.remove();
			}
		};
	}
	
	
	@Override
	public Object getHandle() {
		return handle;
	}
	
	
	private Object wrap(Object value) {
		return cache.wrap(value);
	}
	
	
	private Object unwrap(String key, Object wrapped) {
		return cache.unwrap(key, wrapped);
	}
}