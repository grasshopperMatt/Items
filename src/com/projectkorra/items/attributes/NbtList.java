package com.projectkorra.items.attributes;

import com.projectkorra.items.attributes.util.ConvertedList;

public final class NbtList extends ConvertedList {

	public NbtList(Object handle) {
		super(handle, Nbt.get().getDataList(handle));
	}
}
