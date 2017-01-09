package com.projectkorra.items;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.items.commands.BaseCommand;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;

public class Items extends JavaPlugin {	
	private static Items instance;
	public static final Map<String, HashMap<String, Field>> attributes = new HashMap<String, HashMap<String, Field>>(); 
	
	@Override
	public void onEnable() {
		instance = this;
			
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();			
		}		
		
		new ItemsLoader();
		
		HashMap<String, Field> earthblast = new HashMap<String, Field>();
		HashMap<String, Field> watermanipulation = new HashMap<String, Field>();
		
		try {
			
			earthblast.put("earthblast.damage", EarthBlast.class.getDeclaredField("damage"));
			earthblast.put("earthblast.speed", EarthBlast.class.getDeclaredField("speed"));
			
			watermanipulation.put("watermanipulation.damage", WaterManipulation.class.getDeclaredField("damage"));
			watermanipulation.put("watermanipulation.speed", WaterManipulation.class.getDeclaredField("speed"));
		} 
		
		catch (SecurityException | NoSuchFieldException exception) {
			exception.printStackTrace();
		}
		
		attributes.put("earthblast", earthblast);
		attributes.put("watermanipulation", watermanipulation);
		
		getServer().getPluginManager().registerEvents(new ItemsListener(), this);
		new BaseCommand();
	}
		
	@Override
	public void onDisable() {
		instance = null;
	}
	
	public static Class<?> fromPrimative(Class<?> primative) throws ClassNotFoundException {
		String name = primative.getName();
		
		if (name == null) {
			return primative;
		}
		
		if (!primative.isPrimitive()) {
			return primative;	
		}
		
		name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		return Class.forName("java.lang." + name);
	}
	
	public static File getItemsFolder() {
		return new File(Items.getInstance().getDataFolder().getPath() + "/items");
	}
	
	public static File getRecipeFolder() {
		return new File(Items.getInstance().getDataFolder().getPath() + "/recipes");
	}
	
	public static Items getInstance() {
		return instance;
	}
}
