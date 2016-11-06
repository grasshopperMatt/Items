package com.projectkorra.items.attributes.nbt;

import java.util.List;

import com.projectkorra.items.attributes.nbt.NBTHandler.Type;
import com.projectkorra.items.attributes.nbt.util.ConvertedList;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public final class NBTList extends ConvertedList {

	public NBTList(Object handle) {
		super(handle, getDataList(handle));
	}
	
	
	@SuppressWarnings("unchecked")
	private static List<Object> getDataList(Object handle) {
		try {
			return (List<Object>) ReflectionHandler.getValue(handle, true, Type.TAG_LIST.getName());
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}
}
