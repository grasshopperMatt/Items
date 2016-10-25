package com.projectkorra.items.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.projectkorra.items.ProjectKorraItems;

public class PKItemLoader {
	public File directory;
	private File[] files;

	public PKItemLoader(File file) {
		directory = file;
		files = directory.listFiles();
		
		load();
	}

	public void load() {
		List<JSONObject> objects = new ArrayList<JSONObject>();

		try {
			for (File file : files) {

				objects.add(readFile(file));
			}

		} catch (IOException | ParseException exception) {
			
			exception.printStackTrace();
		}
		
		for (JSONObject object : objects) {
			ItemStack item = ProjectKorraItems.getInstance().createItemStack(object);
			
			try {
				HashMap<Object, Object> values = ProjectKorraItems.getInstance().itemReader.values(item);
				@SuppressWarnings("unused")
				String name = (String)values.get("name");
				
				//ProjectKorraItems.getInstance().itemManager.itemData.put(name, values);
				
			} catch (ParseException exception) {
				
				exception.printStackTrace();
			}
		}
	}

	public void writeFile(JSONObject object) throws IOException {
		String name = (String) object.get("name");
		FileWriter writer = null;

		for (File file : files) {
			if (file.getName() != name) {

				writer = new FileWriter(directory + "/" + name);
				writer.write(object.toJSONString());
				writer.close();
			}
		}
	}

	public JSONObject readFile(File file) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject object = (JSONObject) parser.parse(new FileReader(file));

		return object;
	}

}
