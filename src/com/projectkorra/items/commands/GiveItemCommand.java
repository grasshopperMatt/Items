package com.projectkorra.items.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.Items;
import com.projectkorra.items.ItemsLoader;

public class GiveItemCommand extends PKICommand {

	public GiveItemCommand() {
		super("give", "/bending items give <Item> [Amount] [Player]", "Give a player a loaded Bending Item.", new String[] { "give", "g", "giv" });
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (!hasPermission(sender)) {
			sender.sendMessage("-Permission!");
			return;
		}
		
		if (correctLength(sender, args.size(), 0, 3)) {
			if (args.size() == 0) {
				instances.get("list").execute(sender, args);
				return;
			}
			
			if (isPlayer(sender)) {
				Player player = (Player)sender;
				
				ItemStack item = ItemsLoader.items.get("t2");
				int amount = 1;
					
				if (args.size() >= 1) {
					Bukkit.broadcastMessage(args.get(0));
					item = ItemsLoader.items.get(args.get(0));
					
					if (item == null) {
						sender.sendMessage("Item not found");
						return;
					}
						
					if (args.size() >= 2) {
						try {
							amount = Integer.parseInt(args.get(1));
						} catch (Exception e) {
							sender.sendMessage("Not a number");
							return;
						}
						item.setAmount(amount);
							
						if (args.size() == 3) {
							Player target = (Player)Items.getInstance().getServer().getPlayer(args.get(2));
							
							if (target == null) {
								sender.sendMessage("Invalid player");
								return;
							}
								
							target.getInventory().addItem(item);
							return;
						}
					}
				}
				player.getInventory().addItem(item);
				return;
			}
			sender.sendMessage("Player only");
			return;
		}
	}

}