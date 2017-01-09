package com.projectkorra.items.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.projectkorra.command.PKCommand;

public class BaseCommand extends PKCommand {

	public BaseCommand() {
		super("items", "/bending items", "Base command for the Items side plugin", new String[] { "items", "item" });
		
		new GiveItemCommand();
		new ListItemCommand();
	}

	@Override
	public void execute(CommandSender sender, List<String> args) {
		if (args.size() == 0) {
			for (String i : PKICommand.instances.keySet()) {
				sender.sendMessage(ChatColor.RED + "/bending items " + i);
			}
		}
		
		else {
			for (PKICommand command : PKICommand.instances.values()) {
				if (Arrays.asList(command.getAliases()).contains(args.get(0).toLowerCase())) {
					command.execute(sender, args.subList(1, args.size()));
				}
			}
		}
	}
}