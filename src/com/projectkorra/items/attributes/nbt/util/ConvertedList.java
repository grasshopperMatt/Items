package com.projectkorra.items.attributes.nbt.util;

import java.lang.reflect.Field;
import java.util.AbstractList;
import java.util.List;

import com.projectkorra.items.attributes.nbt.NBTHandler;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class ConvertedList extends AbstractList<Object> implements Wrapper {
	private final WrapperCache cache = new WrapperCache();
	private final Object handle;
	private final List<Object> original;
	private static Field nbtListType;
	

	public ConvertedList(Object handle, List<Object> original) {
		this.handle = handle;
		this.original = original;
	}	
	
	@Override
	public void add(int index, Object element) {
		Object nbt = unwrap(element);

		if (size() == 0) {
			try {
				if (nbtListType == null) {
					nbtListType = ReflectionHandler.getField(handle.getClass(), true, "type");
				}
				
				ReflectionHandler.setValue(handle, true, nbtListType.getName(), (byte) NBTHandler.getType(nbt).getId());
			} 
			
			catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
				exception.printStackTrace();
			}
		}
		
		original.add(index, nbt);
	}
	

	@Override
	public boolean remove(Object o) {
		return original.remove(unwrap(o));
	}
	
	
	@Override
	public Object set(int index, Object element) {
		return wrap(original.set(index, unwrap(element)));
	}
	

	@Override
	public Object get(int index) {
		return wrap(original.get(index));
	}
	
	
	@Override
	public int size() {
		return original.size();
	}
	
	
	@Override
	public Object getHandle() {
		return handle;
	}
	
	
	private Object wrap(Object value) {
		return cache.wrap(value);
	}
	

	private Object unwrap(Object wrapped) {
		return cache.unwrap("", wrapped);
	}
}