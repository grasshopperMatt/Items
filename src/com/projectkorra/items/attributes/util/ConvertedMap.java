package com.projectkorra.items.attributes.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.projectkorra.items.attributes.Nbt;

public class ConvertedMap extends AbstractMap<String, Object> implements Wrapper {
	private final Object handle;
	private final Map<String, Object> original;
	private final CachedNativeWrapper cache;

	public ConvertedMap(Object nmsHandle, Map<String, Object> originalData) {
		handle = nmsHandle;
		original = originalData;
		cache = new CachedNativeWrapper();
	}
	
	@Override
	public Object getHandle() {
		return handle;
	}
	

	protected Object wrapOutgoing(Object value) {
		return cache.wrap(value);
	}
	

	protected Object unwrapIncoming(String key, Object wrapped) {
		return Nbt.get().unwrapValue(key, wrapped);
	}
	
	
	@Override
	public Object put(String key, Object value) {
		return wrapOutgoing(original.put((String) key, unwrapIncoming((String) key, value)));
	}
	
	
	@Override
	public Object get(Object key) {
		return wrapOutgoing(original.get(key));
	}
	
	
	@Override
	public Object remove(Object key) {
		return wrapOutgoing(original.remove(key));
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

				original.put(key, unwrapIncoming(key, value));
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
		final Iterator<Entry<String, Object>> proxy = original
				.entrySet()
				.iterator();
		
		return new Iterator<Entry<String, Object>>() {

			@Override
			public boolean hasNext() {
				return proxy.hasNext();
			}
			

			@Override
			public Entry<String, Object> next() {
				Entry<String, Object> entry = proxy.next();

				return new SimpleEntry<String, Object>(entry.getKey(), wrapOutgoing(entry.getValue()));
			}
			

			@Override
			public void remove() {
				proxy.remove();
			}
			
		};
	}
}