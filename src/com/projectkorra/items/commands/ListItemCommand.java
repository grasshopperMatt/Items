package com.projectkorra.items.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.projectkorra.items.ItemsLoader;
import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;

import net.md_5.bungee.api.ChatColor;

public class ListItemCommand extends PKICommand {

	public ListItemCommand() {
		super("list", "/pki list", "Displays loaded item(s)", new String[] { "list", "l", "lis" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			sender.sendMessage(ChatColor.RED + "-Permission");
			return;
		}
		
		for (String s : ItemsLoader.items.keySet()) {
			sender.sendMessage(ChatColor.GOLD + "Loaded Items:");
			NBTCompound compound = NBTHandler.fromTag(ItemsLoader.items.get(s));
			sender.sendMessage(ChatColor.BLUE + (String) compound.get("Name"));
		}
	}

}
