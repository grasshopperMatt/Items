package com.projectkorra.items;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NbtCompound;
import com.projectkorra.items.attributes.nbt.NbtFactory;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.event.AbilityStartEvent;

public class PKIListener implements Listener {
	private ProjectKorraItems instance;
	
	
	public PKIListener() {
		this.instance = ProjectKorraItems.getInstance();
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ItemStack item = NbtFactory.getCraftItemStack(new ItemStack(Material.DIAMOND_SWORD));
		Player player = event.getPlayer();
		
		NbtCompound compound = NbtFactory.createCompound();
		compound.put("earthblast.damage", 50);
		compound.put("earthblast.speed", 2);
	
		NbtFactory.setTag(item, compound);
		player.getInventory().addItem(item);
	}
	
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		Ability ability = event.getAbility();
		Player player = ability.getPlayer();
		
		HashMap<String, Method> data = instance.data.get(ability.getName().toLowerCase());
		if (data == null || data.isEmpty()) {
			return;
		}
		
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() == Material.AIR) {
			return;
		}
		
		NbtCompound compound = NbtFactory.fromTag(item);
		for (String s : compound.keySet()) {
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
