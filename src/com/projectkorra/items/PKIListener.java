package com.projectkorra.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.nbt.NbtFactory;
import com.projectkorra.items.attributes.nbt.NbtCompound;

public class PKIListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ItemStack item = NbtFactory.getCraftItemStack(new ItemStack(Material.DIAMOND_SWORD));
		
		NbtCompound compound = NbtFactory.createCompound();
		compound.putPath("earthblast.damage", "test");
		
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
	
}
