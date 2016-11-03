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

import com.projectkorra.items.attributes.nbt.NbtHandler;

public class PKItemLoader {
	private static PKItemLoader instance;
	private File directory;
	
	
	public PKItemLoader(File directory) {
		this.directory = directory;
	}
	
	
	public static PKItemLoader getInstance(File directory) {
		if (instance == null) {
			instance = new PKItemLoader(directory);
		}
		
		return instance;
	}
	
	
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
	

	public ItemStack analyzeFile(String configString) {
		final HashMap<String, Object> abilityData = new HashMap<String, Object>();
		String materialString = null;
		List<String> abilityString = null;

		String[] splitString = configString.split("\n");
		for (String l : splitString) {
			l = l.trim();

			if (l.length() == 0) {
				continue;
			}

			if (l.startsWith("material:")) {
				materialString = l.substring("material:".length(), l.length());
			}
			
			else if (l.startsWith("abilities:")) {
				abilityString = splitString(l.substring("abilities:".length(), l.length()), ",");
			}

			else {
				abilityData.putAll(readAbilityString(abilityString, l));
			}
		}
		
		Material material = Material.matchMaterial(materialString);
		return NbtHandler.getCraftItemStack(new ItemStack(material));
	}
	
	
	private HashMap<String, Object> readAbilityString(List<String> abilityString, String line) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		for (String a : (List<String>) abilityString) {
			if (line.startsWith(a)) {
				String key = "";
				String value = "";
				
				for (String s : line.split(":")) {
					if (s.startsWith(a)) {
						key = s;
					}
					
					else {
						value = s;
					}
				}
				
				data.put(key, value);
			}
		}
		return data;
	}
	
	
	private List<String> splitString(String string, String divider) {
		final List<String> strings = new ArrayList<String>();
		
		for (String s : string.split(divider)) {
			strings.add(s);
		}
		
		return strings;
	}
	
	
	public List<String> readFiles() {
		final List<String> readFiles = new ArrayList<String>();
		
		for (File f : directory.listFiles()) {
			readFiles.add(readFile(f));
		}
		
		return readFiles;
	}
	
	
	public List<ItemStack> analyzeFiles(List<String> readFiles) {
		final List<ItemStack> analyzedFiles = new ArrayList<ItemStack>();
		
		for (String f : readFiles) {
			analyzedFiles.add(analyzeFile(f));
		}
		
		return analyzedFiles;
	}
}
