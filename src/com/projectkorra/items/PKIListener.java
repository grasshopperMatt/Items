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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NbtCompound;
import com.projectkorra.items.attributes.nbt.NbtFactory;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.event.AbilityStartEvent;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class PKIListener implements Listener {
	private ProjectKorraItems instance;
	
	
	public PKIListener() {
		this.instance = ProjectKorraItems.getInstance();
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ItemStack item = NbtFactory.getCraftItemStack(new ItemStack(Material.DIAMOND_SWORD));
		
		NbtCompound compound = NbtFactory.createCompound();
		compound.putPath("earthblast.damage", 2);
		compound.putPath("earthblast.speed", 2);
	
		NbtFactory.setTag(item, compound);
		
		event.getPlayer().getInventory().addItem(item);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		NbtCompound compound = NbtFactory.fromTag(event.getPlayer().getInventory().getItemInMainHand());
		for (String s : compound.keySet()) {
			Bukkit.broadcastMessage(s + compound.get(s).toString());
		}
	}
	
	@EventHandler
	public void onAbilityStart(AbilityStartEvent event) {
		Ability ability = event.getAbility();
		HashMap<String, Method> data = instance.data.get(ability.getName());
		
		Player player = ability.getPlayer();
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		
		if (heldItem.getType() == Material.AIR) {
			return;
		}
			
		NbtCompound compound = NbtFactory.fromTag(heldItem);
		NbtCompound subCompound = compound.getPath("earthblast");
		
		Bukkit.broadcastMessage(subCompound.toString());
		
		for (String c : subCompound.keySet()) {
			c = c.toLowerCase();
			
			if (data.containsKey(c)) {
				try {
					ReflectionHandler.invokeMethod(ability, data.get(c).getName(), compound.get(c));	
				} 
				
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException exception) {
					exception.printStackTrace();
				}
			}
		}
	}
}
