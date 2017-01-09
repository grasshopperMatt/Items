package com.projectkorra.items;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class ItemsListener implements Listener {
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		Ability ability = event.getAbility();
		HashMap<String, Field> attributes = Items.attributes.get(ability.getName().toLowerCase());
		if (attributes == null || attributes.isEmpty()) {
			return;
		}
		
		Player player = ability.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (item == null || item.getType() == Material.AIR) {
			return;
		}
		
		NBTCompound compound = NBTHandler.fromTag(item);
		Items.getInstance().getLogger().log(Level.SEVERE, compound.toString());
		
		if (compound.get("Triggers") == null || !((List<String>) compound.get("Triggers")).contains(event.getEventName())) {
			return;
		}
		
		if (compound.get("Include") == null || !((List<String>) compound.get("Include")).contains(ability.getName())) {
			return;
		}
		
		NBTCompound attributeCompound = compound.getMap("Attributes", false);
		
		for (String k : attributeCompound.keySet()) {
			if (!attributes.containsKey(k)) {
				continue;
			}
			
			if (attributes.get(k).getType().isPrimitive()) {
				try {
					Class<?> clazz = Items.fromPrimative(attributes.get(k).getType());
					Method valueOf = clazz.getDeclaredMethod("valueOf", String.class);
					ReflectionHandler.setValue(ability, true, attributes.get(k).getName(), valueOf.invoke(null, String.valueOf(attributeCompound.get(k))));
				} 
				
				catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException | NoSuchMethodException | InvocationTargetException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
	
	
	//@SuppressWarnings("unchecked")
	//@EventHandler
	//public void onAbilityStart(AbilityStartEvent event) {
		//Ability ability = event.getAbility();
		//HashMap<String, Field> attributes = Items.attributes.get(ability.getName().toLowerCase());
		//if (attributes.isEmpty()) {
		//	return;
		//}
		
		//ItemStack item = ability.getPlayer().getInventory().getItemInMainHand();
	//	if (item.getType() == Material.AIR) {
		//	return;
		//}

		//NBTCompound compound = NBTHandler.fromTag(item);
		//Bukkit.broadcastMessage(compound.toString());
		//if (!((List<String>) compound.get("triggers")).contains("abilitystart")) {
		//	return;
		//}
		
		//else if (!((List<String>) compound.get("reference")).contains(ability.getName().toLowerCase())) {
		//	if (!((List<String>) compound.get("reference")).contains("library")) {
		//		return;
		//	}
		//}
		
		//for (String s : compound.keySet()) {
		//	if (compound.get(s) != null) {
		//		if (!attributes.containsKey(s)) {
		//			continue;
		//		}
		//		
		//		try {
		//			
		//			Class<?> clazz = null;
		//			Method valueOf = null;
		//			
		//			if (attributes.get(s).getClass().isPrimitive()) {
		//				clazz = Items.fromPrimative(attributes.get(s).getType());
		//				valueOf = clazz.getDeclaredMethod("valueOf", String.class);
		//				
		//				ReflectionHandler.setValue(ability, true, attributes.get(s).getName(), valueOf.invoke(null, compound.get(s)));
		//			}
		//		} 
		//		
		//		catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException 
		//		| SecurityException | NoSuchFieldException | ClassNotFoundException exception) {
		//			exception.printStackTrace();
		//		}
		//	}
		//}
	//}
}
