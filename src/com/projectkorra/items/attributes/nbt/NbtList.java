package com.projectkorra.items.attributes.nbt;

import com.projectkorra.items.attributes.nbt.util.ConvertedList;

public final class NbtList extends ConvertedList {

	public NbtList(Object handle) {
		super(handle, NbtFactory.getInstance().getDataList(handle));
	}
}
