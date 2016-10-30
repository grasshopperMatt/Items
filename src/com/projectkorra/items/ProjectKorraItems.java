package com.projectkorra.items;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.items.api.PKItemLoader;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;

public class ProjectKorraItems extends JavaPlugin {	
	private static ProjectKorraItems instance;
	

	public PKItemLoader itemLoader;
	public HashMap<String, HashMap<String, Method>> data = new HashMap<String, HashMap<String, Method>>(); 
	
	
	@Override
	public void onEnable() {
		instance = this;
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();			
		}
		
		// FOR TEST
		
		HashMap<String, Method> earthblastInformation = new HashMap<String, Method>();
		HashMap<String, Method> watermanipulationInformation = new HashMap<String, Method>();
		
		try {
			earthblastInformation.put("earthblast.damage", EarthBlast.class.getDeclaredMethod("setDamage", double.class));
			watermanipulationInformation.put("watermanipulation.damage", WaterManipulation.class.getDeclaredMethod("setDamage", double.class));
		} 
		
		catch (NoSuchMethodException | SecurityException exception) {
			exception.printStackTrace();
		}
		
		data.put("earthblast", earthblastInformation);
		data.put("watermanipulation", watermanipulationInformation);
		
		// FOR TEST
		
		this.itemLoader = new PKItemLoader(new File(this.getDataFolder() + "/extensions"));
		
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new PKIListener(), this);
	}
	
	
	@Override
	public void onDisable() {
		instance = null;
	}
	
	
	public List<ItemStack> getYmlItems() {
		final List<ItemStack> loaded = new ArrayList<ItemStack>();
		
		for (ItemStack i : itemLoader.analyzeFiles(itemLoader.readFiles())) {
			loaded.add(i);
		}
		
		return loaded;
	}
	
	
	public static ProjectKorraItems getInstance() {
		return instance;
	}
}
