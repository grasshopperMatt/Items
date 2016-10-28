package com.projectkorra.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Temp {
	
	public void write(ItemStack item, JSONObject object) {

		if (item.getType() == Material.AIR)
			return;

		ItemMeta itemMeta = item.getItemMeta();

		List<String> lore = new ArrayList<String>();
		lore.add(object.toJSONString());

		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
	}
	
	
	public HashMap<Object, Object> values(ItemStack item) throws ParseException {
		JSONObject object = read(item);
		
		if (object == null)
			return null;
		
		HashMap<Object, Object> json = new HashMap<Object, Object>();
		for (Object o : object.keySet()) {
			json.put(o, object.get(o));
		}
		return json;
	}
	
	
	public JSONObject read(ItemStack item) throws ParseException {
		
		if (item == null)
			return null;
		
		if (item.getType() == Material.AIR)
			return null;
		
		ItemMeta itemMeta = item.getItemMeta();
		
		if (!itemMeta.hasLore())
			return null;
		
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(itemMeta.getLore().get(0));
	}
}
