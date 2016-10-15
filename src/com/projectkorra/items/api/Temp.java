package com.projectkorra.items.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Temp {
	
	private File directory;
	
	public String readFile(File file) {
		String configString = "";
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file.getPath()));
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}

		try {
			StringBuilder builder = new StringBuilder();
			String line = reader.readLine();

			while (line != null) {
				builder.append(line);
				builder.append("\n");
				line = reader.readLine();
			}
			configString = builder.toString();
		} catch (IOException exception) {
			exception.printStackTrace();
			return configString;
		}

		try {
			reader.close();
		} catch (IOException exception) {
			exception.printStackTrace();
			return configString;
		}

		return configString;
	}
	

	@SuppressWarnings("unchecked")
	public ItemStack analyzeFile(String configString) {
		HashMap<Object, Object> itemData = new HashMap<Object, Object>();
		Material material = null;

		String[] splitString = configString.split("\n");
		for (String l : splitString) {
			l = l.trim();

			if (l.length() == 0)
				continue;

			if (l.startsWith("material:")) {
				material = Material.matchMaterial(l.substring("material:".length(), l.length()));
			}

			else if (l.startsWith("abilities:")) {
				String string = l.substring("abilities:".length(), l.length());
				itemData.put("abilities", splitString(string, ","));
			}

			else {
				for (String a : (List<String>) itemData.get("abilities")) {
					if (l.startsWith(a)) {
						String key = "";
						String value = "";
						for (String s : l.split(":")) {
							if (s.startsWith(a))
								key = s;
							else
								value = s;
						}
						itemData.put(key, value);
					}
				}
			}
		}
		
		ItemStack item = new ItemStack(material);
		//ProjectKorraItems.getInstance().itemWriter.write(item, itemData);
		return item;
		
	}
	
	public List<String> readFiles() {
		List<String> configStrings = new ArrayList<String>();
		for (File f : directory.listFiles()) {
			configStrings.add(readFile(new File(directory + "/" + f.getName())));
		}
		return configStrings;
	}
	
	public List<ItemStack> analyzeFiles(List<String> configStrings) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (String s : configStrings) {
			items.add(analyzeFile(s));
		}
		return items;
	}
	
	private List<String> splitString(String string, String divider) {
		List<String> strings = new ArrayList<String>();
		for (String s : string.split(divider)) {
			strings.add(s);
		}
		return strings;
	}

}
