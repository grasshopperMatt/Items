package com.projectkorra.items.attributes.util;

import java.util.AbstractList;
import java.util.List;

import com.projectkorra.items.attributes.Nbt;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class ConvertedList extends AbstractList<Object> implements Wrapper {
	private final Object handle;
	private final List<Object> original;
	private final CachedNativeWrapper cache;
	
	private Nbt nbt;

	public ConvertedList(Object nmsHandle, List<Object> originalData) {
		nbt = Nbt.get();
		
		handle = nmsHandle;
		original = originalData;
		cache = new CachedNativeWrapper();

		if (nbt.NBT_LIST_TYPE == null) {
			try {
				
				nbt.NBT_LIST_TYPE = ReflectionHandler.getField(handle.getClass(), true, "type");
				
			} catch (NoSuchFieldException | SecurityException exception) {
				
				exception.printStackTrace();
			}
		}
		
	}
	
	protected Object wrapOutgoing(Object value) {
		return cache.wrap(value);
	}
	

	protected Object unwrapIncoming(Object wrapped) {
		return Nbt.get().unwrapValue("", wrapped);
	}
	
	
	@Override
	public Object getHandle() {
		return handle;
	}
	
	
	@Override
	public int size() {
		return original.size();
	}
	
	
	@Override
	public void add(int index, Object element) {
		Object nbt = unwrapIncoming(element);

		if (size() == 0) {
			try {
				ReflectionHandler.setValue(handle, true, Nbt.get().NBT_LIST_TYPE.toString(), Nbt.get().getType(nbt).id);

			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
					| SecurityException exception) {
				exception.printStackTrace();
			}
		}
		original.add(index, nbt);
	}
	
	@Override
	public boolean remove(Object o) {
		return original.remove(unwrapIncoming(o));
	}
	

	@Override
	public Object set(int index, Object element) {
		return wrapOutgoing(original.set(index, unwrapIncoming(element)));
	}
	

	@Override
	public Object get(int index) {
		return wrapOutgoing(original.get(index));
	}
	
}