package com.projectkorra.items;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PKItemReader {
	
	public PKItemReader() {
		
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
