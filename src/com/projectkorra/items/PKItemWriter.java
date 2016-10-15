package com.projectkorra.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

public class PKItemWriter {

	public PKItemWriter() {

	}

	public void write(ItemStack item, JSONObject object) {

		if (item.getType() == Material.AIR)
			return;

		ItemMeta itemMeta = item.getItemMeta();

		List<String> lore = new ArrayList<String>();
		lore.add(object.toJSONString());

		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}
}
