package com.projectkorra.items;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.event.AbilityStartEvent;

public class PKIListener implements Listener {
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
	ItemStack item = NBTHandler.getCraftItem(new ItemStack(Material.DIAMOND_SWORD));
		Player player = event.getPlayer();
		
		NBTCompound compound = NBTHandler.newCompound();
		compound.put("earthblast.damage", 50);
		compound.put("earthblast.speed", 2);
	
		NBTHandler.setTag(item, compound);
		player.getInventory().addItem(item.clone());
	}
	
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		Ability ability = event.getAbility();
		Player player = ability.getPlayer();
		
		HashMap<String, Method> data = ProjectKorraItems.getInstance().getData(ability.getName().toLowerCase());
		if (data == null) {
			return;
		}
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() == Material.AIR) {
			return;
		}
		
		NBTCompound compound = NBTHandler.fromTag(item);
		for (String s : compound.keySet()) {
			Bukkit.broadcastMessage(s);
			if (compound.get(s) != null) {
				try {
					if (!data.containsKey(s)) {
						continue;
					}
					
					data.get(s).invoke(ability, compound.get(s));
				} 
					
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}
