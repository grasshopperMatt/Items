package com.projectkorra.items.attributes.nbt.util;

import java.util.AbstractList;
import java.util.List;

import com.projectkorra.items.attributes.nbt.Nbt;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class ConvertedList extends AbstractList<Object> implements Wrapper {
	
	private final Object handle;
	private final List<Object> original;
	private final CachedNativeWrapper cache = new CachedNativeWrapper();
	private Nbt nbt = Nbt.getInstance();
	

	public ConvertedList(Object handle, List<Object> original) {
		this.handle = handle;
		this.original = original;
		
		if (nbt.NBT_LIST_TYPE == null) {
			try {
				
				nbt.NBT_LIST_TYPE = ReflectionHandler.getField(handle.getClass(), true, "type");
				
			} catch (NoSuchFieldException | SecurityException exception) {
				
				exception.printStackTrace();
			}
		}
	}
	
	
	@Override
	public Object getHandle() {
		return handle;
	}
	
	
	protected Object wrapOutgoing(Object value) {
		return cache.wrap(value);
	}
	

	protected Object unwrapIncoming(Object wrapped) {
		return cache.unwrap("", wrapped);
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
				
				ReflectionHandler.setValue(handle, true, Nbt.getInstance().NBT_LIST_TYPE.toString(), Nbt.getInstance().getType(nbt).getId());
				
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