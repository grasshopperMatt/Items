package com.projectkorra.items;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

import com.projectkorra.items.api.PKItemLoader;

public class ProjectKorraItems extends JavaPlugin {
	
	private static ProjectKorraItems instance;
	
	
	public PKItemWriter itemWriter;
	public PKItemReader itemReader;
	public PKItemLoader itemLoader;
	
	
	@Override
	public void onEnable() {
		instance = this;
		
		if (!getDataFolder().exists()) {
			
			getDataFolder().mkdir();
			
		}
		
		itemReader = new PKItemReader();
		itemWriter = new PKItemWriter();
		itemLoader = new PKItemLoader(new File(this.getDataFolder() + "/extensions"));
		
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		pluginManager.registerEvents(new PKIListener(), this);
		
	}	
	
	public ItemStack createItemStack(JSONObject object) {
		Material material = Material.matchMaterial((String) object.get("material"));
		
		if (material == Material.AIR) {
			
			return null;
		}
		
		ItemStack item = new ItemStack(material);
		itemWriter.write(item, object);
		
		return item;
	}
	
	public static ProjectKorraItems getInstance() {
		return instance;
	}
}
