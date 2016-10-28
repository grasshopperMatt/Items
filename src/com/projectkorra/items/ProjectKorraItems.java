package com.projectkorra.items;

import java.io.File;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.projectkorra.items.api.PKItemLoader;

public class ProjectKorraItems extends JavaPlugin {	
	public static ProjectKorraItems instance;
	

	public PKItemLoader itemLoader;
	
	
	@Override
	public void onEnable() {
		instance = this;
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();			
		}
		
		itemLoader = new PKItemLoader(new File(this.getDataFolder() + "/extensions"));
		
		final PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new PKIListener(), this);
	}
	
	@Override
	public void onDisable() {
		instance = null;
	}
}
