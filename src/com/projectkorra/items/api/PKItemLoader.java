package com.projectkorra.items.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;

public class PKItemLoader extends PKLoader {
	
	
	public PKItemLoader(File file) {
		List<String> loaded = new ArrayList<String>();
		
		for (File f : file.listFiles()) {
			loaded.add(read(f));
		}
		
		for (String s : loaded) {
			ItemStack i = (ItemStack)analyze(s);
			Bukkit.broadcastMessage(NBTHandler.fromTag(i).toString());
		}
		
		
		//TODO: CHECK DIRECTORY, LOAD ALL FILES
	}
	

	@Override @SuppressWarnings("unchecked")
	public Object analyze(String string) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		String[] splitString = string.split("\n");
		for (String l : splitString) {
			l = l.trim();
			
			if (l.length() == 0) {
				continue;
			}
			
			else if (l.startsWith("material")) {
				data.put("material", l.substring("material".length(), l.length()));
			}
			
			else if (l.startsWith("reference")) {
				data.put("reference", Arrays.asList(l.substring("reference".length(), l.length()).split(",")));
				Bukkit.broadcastMessage(data.get("reference").toString());
			}
			
			else {
				readDataLine((List<String>) data.get("reference"), l);
			}
		}
		
		ItemStack item = NBTHandler.getCraftItem(new ItemStack(Material.matchMaterial((String) data.get("material"))));
		data.remove("material");
		data.remove("reference");
		
		NBTCompound compound = NBTHandler.newCompound();
		compound.putAll(data);
		NBTHandler.setTag(item, compound);
		
		return item;
	}
	
	
	private HashMap<String, Object> readDataLine(List<String> string, String line) {
		final Map<String, Object> data = new HashMap<String, Object>();
		
		for (String r : (List<String>) string) {
			if (line.startsWith(r)) {
				String key = "";
				String value = "";
				
				for (String s : line.split(":")) {
					if (s.startsWith(r)) {
						key = s;
					}
					
					else {
						value = s;
					}
				}
				
				data.put(key, value);
			}
		}
		return (HashMap<String, Object>) data;
	}
}
