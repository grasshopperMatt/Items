package com.projectkorra.items;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;

public class ProjectKorraItems extends JavaPlugin {	
	private static ProjectKorraItems pluginInstance;
	
	private HashMap<String, HashMap<String, Method>> data = new HashMap<String, HashMap<String, Method>>(); 
	
	
	public static ProjectKorraItems getInstance() {
		return pluginInstance;
	}
	
	
	public HashMap<String, Method> getData(String ability) {
		return data.get(ability);
	}
	
	
	@Override
	public void onEnable() {
		pluginInstance = this;
			
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();			
		}
		
		HashMap<String, Method> earthblast = new HashMap<String, Method>();
		HashMap<String, Method> watermanipulation = new HashMap<String, Method>();
		
		try {
			earthblast.put("earthblast.damage", EarthBlast.class.getDeclaredMethod("setDamage", double.class));
			earthblast.put("earthblast.speed", EarthBlast.class.getDeclaredMethod("setSpeed", double.class));
			
			watermanipulation.put("watermanipulation.damage", WaterManipulation.class.getDeclaredMethod("setDamage", double.class));
			watermanipulation.put("watermanipulation.speed", WaterManipulation.class.getDeclaredMethod("setSpeed", double.class));
		} 
		
		catch (NoSuchMethodException | SecurityException exception) {
			exception.printStackTrace();
		}
		
		data.put("earthblast", earthblast);
		data.put("watermanipulation", watermanipulation);
		
		//new PKItemLoader(new File(this.getDataFolder() + "/extensions"));
		getServer().getPluginManager().registerEvents(new PKIListener(), this);
	}
	
	
	@Override
	public void onDisable() {
		pluginInstance = null;
	}
}
